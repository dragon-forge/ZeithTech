package org.zeith.tech.modules.processing.recipes;

import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.fluids.FluidStack;
import org.zeith.hammerlib.HammerLib;
import org.zeith.hammerlib.api.crafting.building.CustomRecipeGenerator;
import org.zeith.hammerlib.api.crafting.building.GsonFileDecoder;
import org.zeith.hammerlib.api.crafting.itf.IFileDecoder;
import org.zeith.hammerlib.util.mcf.fluid.FluidIngredient;
import org.zeith.hammerlib.util.mcf.fluid.FluidIngredientStack;
import org.zeith.hammerlib.util.mcf.itf.INetworkable;
import org.zeith.tech.api.recipes.base.ExtraOutput;
import org.zeith.tech.api.recipes.processing.RecipeWasteProcessor;
import org.zeith.tech.modules.processing.init.FluidsZT_Processing;
import org.zeith.tech.modules.shared.init.TagsZT;
import org.zeith.tech.modules.world.init.FluidsZT_World;

import java.util.List;
import java.util.Optional;

public class CustomWasteProcessorCentrifugeRecipeGenerator
		extends CustomRecipeGenerator<RecipeWasteProcessor, GsonFileDecoder, JsonElement>
		implements INetworkable<RecipeWasteProcessor>
{
	public CustomWasteProcessorCentrifugeRecipeGenerator(ResourceLocation registryPath)
	{
		super(registryPath, IFileDecoder::gson);
	}
	
	@Override
	public Optional<RecipeWasteProcessor> decodeRecipe(ResourceLocation recipeId, JsonElement jsonElement, MinecraftServer server, ICondition.IContext context)
	{
		try
		{
			if(jsonElement.isJsonObject() && !net.minecraftforge.common.crafting.CraftingHelper.processConditions(jsonElement.getAsJsonObject(), "conditions", context))
			{
				HammerLib.LOG.debug("Skipping loading recipe {} as it's conditions were not met", recipeId);
				return Optional.empty();
			}
			var recipe = fromJson(recipeId, GsonHelper.convertToJsonObject(jsonElement, "top element"), context);
			return Optional.of(recipe);
		} catch(IllegalArgumentException | JsonParseException jsonparseexception)
		{
			HammerLib.LOG.error("Parsing error loading recipe {}", recipeId, jsonparseexception);
		}
		
		return Optional.empty();
	}
	
	public RecipeWasteProcessor fromJson(ResourceLocation recipeId, JsonObject root, ICondition.IContext context)
	{
		var inputA = new FluidIngredientStack(
				FluidIngredient.fromJson(root.get("input_a")),
				GsonHelper.getAsInt(GsonHelper.getAsJsonObject(root, "input_a"), "amount")
		);
		
		var inputB = new FluidIngredientStack(
				FluidIngredient.fromJson(root.get("input_b")),
				GsonHelper.getAsInt(GsonHelper.getAsJsonObject(root, "input_b"), "amount")
		);
		
		var inputItem = Ingredient.fromJson(root.get("input_item"));
		
		var time = GsonHelper.getAsInt(root, "energy", 1000);
		
		var outputA = JsonOps.INSTANCE.withDecoder(FluidStack.CODEC).apply(GsonHelper.getAsJsonObject(root, "result_a")).result().orElseThrow().getFirst();
		var outputB = JsonOps.INSTANCE.withDecoder(FluidStack.CODEC).apply(GsonHelper.getAsJsonObject(root, "result_b")).result().orElseThrow().getFirst();
		
		var extras = ExtraOutput.parse(root.get("byproduct"));
		
		return new RecipeWasteProcessor(recipeId, inputA, inputB, inputItem, time, outputA, outputB, extras);
	}
	
	@Override
	public Optional<JsonElement> createTemplate()
	{
		JsonObject $ = new JsonObject();
		{
			var ingredient = new FluidIngredient(FluidIngredient.CompareMode.TAGS, List.of(new FluidStack(FluidsZT_World.CRUDE_OIL.getSource(), 1)), List.of(TagsZT.Fluids.CRUDE_OIL));
			
			$.add("input_a", JsonOps.INSTANCE.withEncoder(FluidIngredient.CODEC).apply(ingredient).result().orElseThrow());
			$.getAsJsonObject("input_a").addProperty("amount", 1000);
			
			$.add("input_b", JsonOps.INSTANCE.withEncoder(FluidIngredient.CODEC).apply(ingredient).result().orElseThrow());
			$.getAsJsonObject("input_b").addProperty("amount", 1000);
		}
		
		$.add("input_item", ingredientTemplate());
		
		{
			var fluid = new FluidStack(Fluids.WATER.getSource(), 800);
			$.add("result_a", JsonOps.INSTANCE.withEncoder(FluidStack.CODEC).apply(fluid).result().orElseThrow());
			
			fluid = new FluidStack(FluidsZT_Processing.DIESEL_FUEL.getSource(), 400);
			$.add("result_b", JsonOps.INSTANCE.withEncoder(FluidStack.CODEC).apply(fluid).result().orElseThrow());
		}
		
		JsonArray byproduct = new JsonArray();
		{
			JsonObject extra = new JsonObject();
			{
				extra.add("item", itemStackTemplate());
				extra.addProperty("chance", 0.25F);
				extra.addProperty("min", 1);
				extra.addProperty("max", 1);
			}
			byproduct.add(extra);
		}
		$.add("byproduct", byproduct);
		
		$.addProperty("time", 1000);
		
		return Optional.of($);
	}
	
	@Override
	public void toNetwork(FriendlyByteBuf buf, RecipeWasteProcessor obj)
	{
		buf.writeResourceLocation(obj.id);
		
		buf.writeNbt((CompoundTag) NbtOps.INSTANCE.withEncoder(FluidIngredientStack.CODEC).apply(obj.getInputA()).result().orElseThrow());
		buf.writeNbt((CompoundTag) NbtOps.INSTANCE.withEncoder(FluidIngredientStack.CODEC).apply(obj.getInputB()).result().orElseThrow());
		obj.getInputItem().toNetwork(buf);
		
		buf.writeInt(obj.getTime());
		
		buf.writeFluidStack(obj.getOutputA());
		buf.writeFluidStack(obj.getOutputB());
		
		buf.writeCollection(obj.getByproduct(), ExtraOutput::toNetwork);
	}
	
	@Override
	public RecipeWasteProcessor fromNetwork(FriendlyByteBuf buf)
	{
		var rl = buf.readResourceLocation();
		
		var inputA = NbtOps.INSTANCE.withDecoder(FluidIngredientStack.CODEC).apply(buf.readNbt()).result().orElseThrow().getFirst();
		var inputB = NbtOps.INSTANCE.withDecoder(FluidIngredientStack.CODEC).apply(buf.readNbt()).result().orElseThrow().getFirst();
		var inputItem = Ingredient.fromNetwork(buf);
		
		var time = buf.readInt();
		
		var resultA = buf.readFluidStack();
		var resultB = buf.readFluidStack();
		
		var extras = buf.readList(ExtraOutput::fromNetwork);
		
		return new RecipeWasteProcessor(rl, inputA, inputB, inputItem, time, resultA, resultB, extras);
	}
}