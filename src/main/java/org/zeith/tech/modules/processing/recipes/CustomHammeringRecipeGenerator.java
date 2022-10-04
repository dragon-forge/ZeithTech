package org.zeith.tech.modules.processing.recipes;

import com.google.gson.*;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.zeith.hammerlib.HammerLib;
import org.zeith.hammerlib.api.crafting.building.CustomRecipeGenerator;
import org.zeith.hammerlib.api.crafting.building.GsonFileDecoder;
import org.zeith.hammerlib.api.crafting.itf.IFileDecoder;
import org.zeith.hammerlib.util.mcf.itf.INetworkable;
import org.zeith.tech.api.enums.TechTier;
import org.zeith.tech.api.recipes.RecipeHammering;

import java.util.*;

public class CustomHammeringRecipeGenerator
		extends CustomRecipeGenerator<RecipeHammering, GsonFileDecoder, JsonElement>
		implements INetworkable<RecipeHammering>
{
	public CustomHammeringRecipeGenerator(ResourceLocation registryPath)
	{
		super(registryPath, IFileDecoder::gson);
	}
	
	@Override
	public Optional<RecipeHammering> decodeRecipe(ResourceLocation recipeId, JsonElement jsonElement, MinecraftServer server, ICondition.IContext context)
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
	
	public static RecipeHammering fromJson(ResourceLocation recipeId, JsonObject root, ICondition.IContext context)
	{
		var tier = TechTier.values()[GsonHelper.getAsInt(root, "tier", 0)];
		var hits = GsonHelper.getAsInt(root, "hits", 0);
		
		var input = Ingredient.fromJson(root.get("input"));
		var output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(root, "result"));
		
		List<TagKey<Block>> blockHammeringTags = new ArrayList<>();
		
		var arr = GsonHelper.getAsJsonArray(root, "block");
		for(int i = 0; i < arr.size(); i++)
			blockHammeringTags.add(TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(arr.getAsString())));
		
		return new RecipeHammering(recipeId, input, output, hits, List.copyOf(blockHammeringTags), tier);
	}
	
	@Override
	public void toNetwork(FriendlyByteBuf buf, RecipeHammering obj)
	{
		buf.writeResourceLocation(obj.id);
		buf.writeByte(obj.getTier().ordinal());
		obj.getInput().toNetwork(buf);
		buf.writeItemStack(obj.getRecipeOutput(), false);
		buf.writeInt(obj.getHitCountRaw());
		var tags = obj.getHammeringTags();
		buf.writeInt(tags.size());
		for(var tag : tags)
			buf.writeResourceLocation(tag.location());
	}
	
	@Override
	public RecipeHammering fromNetwork(FriendlyByteBuf buf)
	{
		var id = buf.readResourceLocation();
		var tier = TechTier.values()[buf.readByte()];
		var input = Ingredient.fromNetwork(buf);
		var output = buf.readItem();
		var hits = buf.readInt();
		var tagCount = buf.readInt();
		var tags = new ArrayList<TagKey<Block>>();
		for(int i = 0; i < tagCount; ++i)
			tags.add(TagKey.create(Registry.BLOCK_REGISTRY, buf.readResourceLocation()));
		return new RecipeHammering(id, input, output, hits, tags, tier);
	}
}
