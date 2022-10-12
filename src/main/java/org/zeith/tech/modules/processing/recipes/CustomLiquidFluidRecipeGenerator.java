package org.zeith.tech.modules.processing.recipes;

import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.fluids.FluidStack;
import org.zeith.hammerlib.HammerLib;
import org.zeith.hammerlib.api.crafting.building.CustomRecipeGenerator;
import org.zeith.hammerlib.api.crafting.building.GsonFileDecoder;
import org.zeith.hammerlib.api.crafting.itf.IFileDecoder;
import org.zeith.hammerlib.util.mcf.fluid.FluidIngredient;
import org.zeith.hammerlib.util.mcf.itf.INetworkable;
import org.zeith.tech.api.recipes.processing.RecipeLiquidFuel;
import org.zeith.tech.modules.shared.init.TagsZT;
import org.zeith.tech.modules.world.init.FluidsZT_World;

import java.util.List;
import java.util.Optional;

public class CustomLiquidFluidRecipeGenerator
		extends CustomRecipeGenerator<RecipeLiquidFuel, GsonFileDecoder, JsonElement>
		implements INetworkable<RecipeLiquidFuel>
{
	public CustomLiquidFluidRecipeGenerator(ResourceLocation registryPath)
	{
		super(registryPath, IFileDecoder::gson);
	}
	
	@Override
	public Optional<RecipeLiquidFuel> decodeRecipe(ResourceLocation recipeId, JsonElement jsonElement, MinecraftServer server, ICondition.IContext context)
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
	
	public RecipeLiquidFuel fromJson(ResourceLocation recipeId, JsonObject root, ICondition.IContext context)
	{
		var inputJSON = root.getAsJsonObject("input");
		if(inputJSON.get("mode").getAsString().equalsIgnoreCase("VALUES"))
		{
			if(!inputJSON.has("tags"))
				inputJSON.add("tags", new JsonArray());
		} else if(!inputJSON.has("fluids"))
			inputJSON.add("fluids", new JsonArray());
		var input = FluidIngredient.fromJson(inputJSON);
		var burnTime = GsonHelper.getAsInt(root, "burn_time", 400);
		return new RecipeLiquidFuel(recipeId, input, burnTime);
	}
	
	@Override
	public Optional<JsonElement> createTemplate()
	{
		JsonObject $ = new JsonObject();
		{
			var ingredient = new FluidIngredient(FluidIngredient.CompareMode.TAGS, List.of(new FluidStack(FluidsZT_World.CRUDE_OIL.getSource(), 1)), List.of(TagsZT.Fluids.CRUDE_OIL));
			$.add("input", JsonOps.INSTANCE.withEncoder(FluidIngredient.CODEC).apply(ingredient).result().orElseThrow());
		}
		$.addProperty("burn_time", 400);
		return Optional.of($);
	}
	
	@Override
	public void toNetwork(FriendlyByteBuf buf, RecipeLiquidFuel obj)
	{
		buf.writeResourceLocation(obj.id);
		buf.writeNbt((CompoundTag) NbtOps.INSTANCE.withEncoder(FluidIngredient.CODEC).apply(obj.ingredient()).result().orElseThrow());
		buf.writeInt(obj.burnTime());
	}
	
	@Override
	public RecipeLiquidFuel fromNetwork(FriendlyByteBuf buf)
	{
		var rl = buf.readResourceLocation();
		var input = NbtOps.INSTANCE.withDecoder(FluidIngredient.CODEC).apply(buf.readNbt()).result().orElseThrow().getFirst();
		var burnTime = buf.readInt();
		return new RecipeLiquidFuel(rl, input, burnTime);
	}
}