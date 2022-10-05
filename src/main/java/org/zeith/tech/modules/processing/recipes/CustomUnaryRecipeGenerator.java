package org.zeith.tech.modules.processing.recipes;

import com.google.gson.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.zeith.hammerlib.HammerLib;
import org.zeith.hammerlib.api.crafting.building.CustomRecipeGenerator;
import org.zeith.hammerlib.api.crafting.building.GsonFileDecoder;
import org.zeith.hammerlib.api.crafting.itf.IFileDecoder;
import org.zeith.hammerlib.util.mcf.itf.INetworkable;
import org.zeith.tech.api.enums.TechTier;
import org.zeith.tech.api.recipes.base.IUnaryRecipeConstructor;
import org.zeith.tech.api.recipes.base.RecipeUnaryBase;

import java.util.Optional;
import java.util.function.Function;

public class CustomUnaryRecipeGenerator<R extends RecipeUnaryBase>
		extends CustomRecipeGenerator<R, GsonFileDecoder, JsonElement>
		implements INetworkable<R>
{
	protected final IUnaryRecipeConstructor<R> constructor;
	
	public CustomUnaryRecipeGenerator(ResourceLocation registryPath, IUnaryRecipeConstructor<R> constructor)
	{
		super(registryPath, IFileDecoder::gson);
		this.constructor = constructor;
	}
	
	public static <T extends RecipeUnaryBase> Function<ResourceLocation, CustomRecipeGenerator<T, ?, ?>> make(IUnaryRecipeConstructor<T> constructor)
	{
		return rl -> new CustomUnaryRecipeGenerator<>(rl, constructor);
	}
	
	@Override
	public Optional<R> decodeRecipe(ResourceLocation recipeId, JsonElement jsonElement, MinecraftServer server, ICondition.IContext context)
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
	
	public R fromJson(ResourceLocation recipeId, JsonObject root, ICondition.IContext context)
	{
		var tier = TechTier.values()[GsonHelper.getAsInt(root, "tier", 0)];
		var input = Ingredient.fromJson(root.get("input"));
		var inputCount = GsonHelper.getAsInt(root, "input_count", 1);
		var output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(root, "result"));
		var time = GsonHelper.getAsInt(root, "time", 200);
		return constructor.newInstance(recipeId, input, inputCount, output, time, tier);
	}
	
	@Override
	public Optional<JsonElement> createTemplate()
	{
		JsonObject $ = new JsonObject();
		$.addProperty("tier__comment", "Tier 0 is BASIC, 1 is ADVANCED, 2 is QUANTUM");
		$.addProperty("tier", 0);
		$.add("input", ingredientTemplate());
		$.addProperty("input_count", 1);
		$.add("result", itemStackTemplate());
		$.addProperty("time", 200);
		return Optional.of($);
	}
	
	@Override
	public void toNetwork(FriendlyByteBuf buf, R obj)
	{
		buf.writeResourceLocation(obj.id);
		buf.writeByte(obj.getMinTier().ordinal());
		obj.getInput().toNetwork(buf);
		buf.writeShort(obj.getInputCount());
		buf.writeItemStack(obj.getRecipeOutput(), false);
		buf.writeInt(obj.getCraftTime());
	}
	
	@Override
	public R fromNetwork(FriendlyByteBuf buf)
	{
		var id = buf.readResourceLocation();
		var tier = TechTier.values()[buf.readByte()];
		var input = Ingredient.fromNetwork(buf);
		var inputCount = buf.readShort();
		var output = buf.readItem();
		var time = buf.readInt();
		return constructor.newInstance(id, input, inputCount, output, time, tier);
	}
}
