package org.zeith.tech.modules.processing.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.zeith.tech.api.enums.TechTier;
import org.zeith.tech.api.recipes.base.ExtraOutput;
import org.zeith.tech.api.recipes.processing.RecipeSawmill;

import java.util.Optional;

public class CustomSawmillRecipeGenerator
		extends CustomUnaryRecipeGenerator<RecipeSawmill>
{
	public CustomSawmillRecipeGenerator(ResourceLocation registryPath)
	{
		super(registryPath, RecipeSawmill::new);
	}
	
	@Override
	public RecipeSawmill fromJson(ResourceLocation recipeId, JsonObject root, ICondition.IContext context)
	{
		var tier = TechTier.values()[GsonHelper.getAsInt(root, "tier", 0)];
		var input = Ingredient.fromJson(root.get("input"));
		var inputCount = GsonHelper.getAsInt(root, "input_count", 1);
		var output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(root, "result"));
		var time = GsonHelper.getAsInt(root, "time", 200);
		
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
		
		return new RecipeSawmill(recipeId, input, inputCount, output, time, tier, extra);
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
	public void toNetwork(FriendlyByteBuf buf, RecipeSawmill obj)
	{
		buf.writeResourceLocation(obj.id);
		buf.writeByte(obj.getMinTier().ordinal());
		obj.getInput().toNetwork(buf);
		buf.writeShort(obj.getInputCount());
		buf.writeItemStack(obj.getRecipeOutput(), false);
		buf.writeInt(obj.getCraftTime());
		
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
	public RecipeSawmill fromNetwork(FriendlyByteBuf buf)
	{
		var id = buf.readResourceLocation();
		var tier = TechTier.values()[buf.readByte()];
		var input = Ingredient.fromNetwork(buf);
		var inputCount = buf.readShort();
		var output = buf.readItem();
		var time = buf.readInt();
		var extra = buf.readBoolean() ? ExtraOutput.fromNetwork(buf) : null;
		return new RecipeSawmill(id, input, inputCount, output, time, tier, extra);
	}
}