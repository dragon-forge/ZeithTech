package org.zeith.tech.modules.processing.recipes;

import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.fluids.FluidStack;
import org.zeith.hammerlib.HammerLib;
import org.zeith.hammerlib.api.crafting.building.CustomRecipeGenerator;
import org.zeith.hammerlib.api.crafting.building.GsonFileDecoder;
import org.zeith.hammerlib.api.crafting.itf.IFileDecoder;
import org.zeith.hammerlib.util.mcf.itf.INetworkable;
import org.zeith.tech.api.recipes.base.ExtraOutput;
import org.zeith.tech.api.recipes.processing.RecipeFluidCentrifuge;
import org.zeith.tech.api.utils.FluidIngredient;
import org.zeith.tech.modules.processing.init.FluidsZT_Processing;
import org.zeith.tech.modules.shared.init.TagsZT;
import org.zeith.tech.modules.world.init.FluidsZT_World;

import java.util.List;
import java.util.Optional;

public class CustomFluidCentrifugeRecipeGenerator
		extends CustomRecipeGenerator<RecipeFluidCentrifuge, GsonFileDecoder, JsonElement>
		implements INetworkable<RecipeFluidCentrifuge>
{
	public CustomFluidCentrifugeRecipeGenerator(ResourceLocation registryPath)
	{
		super(registryPath, IFileDecoder::gson);
	}
	
	@Override
	public Optional<RecipeFluidCentrifuge> decodeRecipe(ResourceLocation recipeId, JsonElement jsonElement, MinecraftServer server, ICondition.IContext context)
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
	
	public RecipeFluidCentrifuge fromJson(ResourceLocation recipeId, JsonObject root, ICondition.IContext context)
	{
		var input = FluidIngredient.fromJson(root.get("input"));
		var output = JsonOps.INSTANCE.withDecoder(FluidStack.CODEC).apply(GsonHelper.getAsJsonObject(root, "result")).result().orElseThrow().getFirst();
		var energy = GsonHelper.getAsInt(root, "energy", 2000);
		
		ExtraOutput extra = null;
		
		genExtra:
		if(root.has("extra"))
		{
			var ex = GsonHelper.getAsJsonObject(root, "extra");
			
			var extraItem = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(ex, "item"));
			var chance = GsonHelper.getAsFloat(ex, "chance", 1F);
			
			if(root.has("max"))
			{
				var min = GsonHelper.getAsInt(ex, "min", 1);
				var max = GsonHelper.getAsInt(ex, "max");
				extra = new ExtraOutput.Ranged(extraItem, min, max, chance);
				break genExtra;
			}
			
			extra = new ExtraOutput(extraItem, chance);
		}
		
		return new RecipeFluidCentrifuge(recipeId, input, energy, output, extra);
	}
	
	@Override
	public Optional<JsonElement> createTemplate()
	{
		JsonObject $ = new JsonObject();
		{
			var ingredient = new FluidIngredient(FluidIngredient.CompareMode.TAGS, List.of(new FluidStack(FluidsZT_World.CRUDE_OIL.getSource(), 1)), List.of(TagsZT.Fluids.CRUDE_OIL), 100);
			$.add("input", JsonOps.INSTANCE.withEncoder(FluidIngredient.CODEC).apply(ingredient).result().orElseThrow());
		}
		{
			var fluid = new FluidStack(FluidsZT_Processing.REFINED_OIL.getSource(), 100);
			$.add("result", JsonOps.INSTANCE.withEncoder(FluidStack.CODEC).apply(fluid).result().orElseThrow());
		}
		$.addProperty("energy", 2000);
		
		JsonObject extra = new JsonObject();
		{
			extra.add("item", itemStackTemplate());
			extra.addProperty("chance", 0.25F);
			extra.addProperty("min", 1);
			extra.addProperty("max", 1);
		}
		$.add("extra", extra);
		
		return Optional.of($);
	}
	
	@Override
	public void toNetwork(FriendlyByteBuf buf, RecipeFluidCentrifuge obj)
	{
		buf.writeResourceLocation(obj.id);
		buf.writeNbt((CompoundTag) NbtOps.INSTANCE.withEncoder(FluidIngredient.CODEC).apply(obj.getInput()).result().orElseThrow());
		buf.writeInt(obj.getEnergy());
		buf.writeFluidStack(obj.getOutput());
		
		obj.getExtra().ifPresentOrElse(c ->
		{
			buf.writeBoolean(true);
			c.toNetwork(buf);
		}, () ->
		{
			buf.writeBoolean(false);
		});
	}
	
	@Override
	public RecipeFluidCentrifuge fromNetwork(FriendlyByteBuf buf)
	{
		var rl = buf.readResourceLocation();
		var input = NbtOps.INSTANCE.withDecoder(FluidIngredient.CODEC).apply(buf.readNbt()).result().orElseThrow().getFirst();
		var energy = buf.readInt();
		var fs = buf.readFluidStack();
		var extra = buf.readBoolean() ? ExtraOutput.fromNetwork(buf) : null;
		return new RecipeFluidCentrifuge(rl, input, energy, fs, extra);
	}
}