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
import org.zeith.tech.api.block.multiblock.blast_furnace.IBlastFurnaceCasingBlock;
import org.zeith.tech.api.recipes.processing.RecipeBlastFurnace;

import java.util.Optional;

public class CustomBlastFurnaceRecipeGenerator
		extends CustomRecipeGenerator<RecipeBlastFurnace, GsonFileDecoder, JsonElement>
		implements INetworkable<RecipeBlastFurnace>
{
	public CustomBlastFurnaceRecipeGenerator(ResourceLocation registryPath)
	{
		super(registryPath, IFileDecoder::gson);
	}
	
	@Override
	public Optional<RecipeBlastFurnace> decodeRecipe(ResourceLocation recipeId, JsonElement jsonElement, MinecraftServer server, ICondition.IContext context)
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
	
	public RecipeBlastFurnace fromJson(ResourceLocation recipeId, JsonObject root, ICondition.IContext context)
	{
		var inputs = GsonHelper.getAsJsonArray(root, "items");
		if(inputs.size() > 2) throw new JsonSyntaxException("Amount of input item exceeds 2.");
		if(inputs.isEmpty()) throw new JsonSyntaxException("No input items specified.");
		
		var inputA = Ingredient.fromJson(inputs.get(0));
		var inputB = inputs.size() > 1 ? Ingredient.fromJson(inputs.get(1)) : Ingredient.EMPTY;
		
		var tier = IBlastFurnaceCasingBlock.BlastFurnaceTier.values()[GsonHelper.getAsInt(root, "tier", 0)];
		var temp = GsonHelper.getAsFloat(root, "temperature", 1536.0F);
		var time = GsonHelper.getAsInt(root, "time", 2000);
		
		var result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(root, "result"));
		
		return new RecipeBlastFurnace(recipeId, tier, temp, result, inputA, inputB, time);
	}
	
	@Override
	public Optional<JsonElement> createTemplate()
	{
		JsonObject $ = new JsonObject();
		
		$.addProperty("tier", 0);
		$.addProperty("time", 2000);
		$.addProperty("temperature", 1536.0F);
		
		JsonArray inputs = new JsonArray();
		inputs.add(ingredientTemplate());
		inputs.add(ingredientTemplate());
		$.add("items", inputs);
		
		$.add("result", itemStackTemplate());
		
		return Optional.of($);
	}
	
	@Override
	public void toNetwork(FriendlyByteBuf buf, RecipeBlastFurnace obj)
	{
		buf.writeResourceLocation(obj.id);
		buf.writeVarInt(obj.getTier().ordinal());
		buf.writeFloat(obj.getNeededTemperature());
		buf.writeInt(obj.getCraftTime());
		buf.writeItemStack(obj.assemble(), false);
		obj.getInputA().toNetwork(buf);
		obj.getInputB().toNetwork(buf);
	}
	
	@Override
	public RecipeBlastFurnace fromNetwork(FriendlyByteBuf buf)
	{
		var rl = buf.readResourceLocation();
		var tier = IBlastFurnaceCasingBlock.BlastFurnaceTier.values()[buf.readInt()];
		var temp = buf.readFloat();
		var time = buf.readInt();
		var result = buf.readItem();
		var inputA = Ingredient.fromNetwork(buf);
		var inputB = Ingredient.fromNetwork(buf);
		
		return new RecipeBlastFurnace(rl, tier, temp, result, inputA, inputB, time);
	}
}