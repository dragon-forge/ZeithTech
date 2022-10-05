package org.zeith.tech.modules.processing.recipes;

import com.google.common.collect.Maps;
import com.google.gson.*;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.zeith.hammerlib.HammerLib;
import org.zeith.hammerlib.api.crafting.building.CustomRecipeGenerator;
import org.zeith.hammerlib.api.crafting.building.GsonFileDecoder;
import org.zeith.hammerlib.api.crafting.impl.ItemStackResult;
import org.zeith.hammerlib.api.crafting.itf.IFileDecoder;
import org.zeith.hammerlib.core.adapter.recipe.RecipeShape;
import org.zeith.hammerlib.util.mcf.itf.INetworkable;
import org.zeith.tech.api.enums.TechTier;
import org.zeith.tech.api.recipes.processing.RecipeMachineAssembler;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CustomMachineAssemblyRecipeGenerator
		extends CustomRecipeGenerator<RecipeMachineAssembler, GsonFileDecoder, JsonElement>
		implements INetworkable<RecipeMachineAssembler>
{
	public CustomMachineAssemblyRecipeGenerator(ResourceLocation registryPath)
	{
		super(registryPath, IFileDecoder::gson);
	}
	
	@Override
	public Optional<RecipeMachineAssembler> decodeRecipe(ResourceLocation recipeId, JsonElement jsonElement, MinecraftServer server, ICondition.IContext context)
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
	
	public static RecipeMachineAssembler fromJson(ResourceLocation recipeId, JsonObject root, ICondition.IContext context)
	{
		var tier = TechTier.values()[GsonHelper.getAsInt(root, "tier", 0)];
		
		Map<String, Ingredient> map = keyFromJson(GsonHelper.getAsJsonObject(root, "key"));
		String[] patter = shrink(patternFromJson(GsonHelper.getAsJsonArray(root, "pattern")));
		var mappings = map.entrySet().stream()
				.map(e -> Map.entry(e.getKey().charAt(0), e.getValue()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		ItemStack itemstack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(root, "result"));
		return new RecipeMachineAssembler(recipeId, tier, new ItemStackResult(itemstack), new RecipeShape(patter), mappings);
	}
	
	@Override
	public Optional<JsonElement> createTemplate()
	{
		JsonObject $ = new JsonObject();
		
		$.addProperty("tier__comment", "Tier 0 is BASIC, 1 is ADVANCED, 2 is QUANTUM");
		$.addProperty("tier", 0);
		
		{
			var pat = new JsonArray();
			pat.add("  1  ");
			pat.add(" 232 ");
			pat.add("23132");
			pat.add(" 232 ");
			pat.add("  1  ");
			$.add("pattern", pat);
		}
		
		{
			var keys = new JsonObject();
			keys.add("1", ingredientTemplate());
			keys.add("2", ingredientTemplate());
			keys.add("3", ingredientTemplate());
			$.add("key", keys);
		}
		
		$.add("result", itemStackTemplate());
		
		return Optional.of($);
	}
	
	private static int firstNonSpace(String p_44185_)
	{
		int i;
		for(i = 0; i < p_44185_.length() && p_44185_.charAt(i) == ' '; ++i)
		{
		}
		
		return i;
	}
	
	private static int lastNonSpace(String p_44201_)
	{
		int i;
		for(i = p_44201_.length() - 1; i >= 0 && p_44201_.charAt(i) == ' '; --i)
		{
		}
		
		return i;
	}
	
	static String[] shrink(String... p_44187_)
	{
		int i = Integer.MAX_VALUE;
		int j = 0;
		int k = 0;
		int l = 0;
		
		for(int i1 = 0; i1 < p_44187_.length; ++i1)
		{
			String s = p_44187_[i1];
			i = Math.min(i, firstNonSpace(s));
			int j1 = lastNonSpace(s);
			j = Math.max(j, j1);
			if(j1 < 0)
			{
				if(k == i1)
				{
					++k;
				}
				
				++l;
			} else
			{
				l = 0;
			}
		}
		
		if(p_44187_.length == l)
		{
			return new String[0];
		} else
		{
			String[] astring = new String[p_44187_.length - l - k];
			
			for(int k1 = 0; k1 < astring.length; ++k1)
			{
				astring[k1] = p_44187_[k1 + k].substring(i, j + 1);
			}
			
			return astring;
		}
	}
	
	static String[] patternFromJson(JsonArray p_44197_)
	{
		String[] astring = new String[p_44197_.size()];
		if(astring.length > 5)
		{
			throw new JsonSyntaxException("Invalid pattern: too many rows, " + 5 + " is maximum");
		} else if(astring.length == 0)
		{
			throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
		} else
		{
			for(int i = 0; i < astring.length; ++i)
			{
				String s = GsonHelper.convertToString(p_44197_.get(i), "pattern[" + i + "]");
				if(s.length() > 5)
				{
					throw new JsonSyntaxException("Invalid pattern: too many columns, " + 5 + " is maximum");
				}
				
				if(i > 0 && astring[0].length() != s.length())
				{
					throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
				}
				
				astring[i] = s;
			}
			
			return astring;
		}
	}
	
	static Map<String, Ingredient> keyFromJson(JsonObject p_44211_)
	{
		Map<String, Ingredient> map = Maps.newHashMap();
		
		for(Map.Entry<String, JsonElement> entry : p_44211_.entrySet())
		{
			if(entry.getKey().length() != 1)
			{
				throw new JsonSyntaxException("Invalid key entry: '" + (String) entry.getKey() + "' is an invalid symbol (must be 1 character only).");
			}
			
			if(" ".equals(entry.getKey()))
			{
				throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
			}
			
			map.put(entry.getKey(), Ingredient.fromJson(entry.getValue()));
		}
		
		map.put(" ", Ingredient.EMPTY);
		return map;
	}
	
	@Override
	public Optional<INetworkable<RecipeMachineAssembler>> getSerializer()
	{
		return Optional.of(this);
	}
	
	@Override
	public void toNetwork(FriendlyByteBuf buf, RecipeMachineAssembler obj)
	{
		buf.writeResourceLocation(obj.getRecipeName());
		
		buf.writeByte(obj.getMinTier().ordinal());
		buf.writeItemStack(obj.getRecipeOutput(), false);
		
		var ri = obj.getRecipeItems();
		
		buf.writeByte(obj.getWidth());
		buf.writeByte(obj.getHeight());
		buf.writeByte(ri.size());
		
		for(int i = 0; i < ri.size(); ++i)
			ri.get(i).toNetwork(buf);
	}
	
	@Override
	public RecipeMachineAssembler fromNetwork(FriendlyByteBuf buf)
	{
		var id = buf.readResourceLocation();
		var tier = TechTier.values()[buf.readByte()];
		var result = buf.readItem();
		int width = buf.readByte();
		int height = buf.readByte();
		int size = buf.readByte();
		var ing = NonNullList.withSize(size, Ingredient.EMPTY);
		for(int i = 0; i < size; ++i)
			ing.set(i, Ingredient.fromNetwork(buf));
		
		return new RecipeMachineAssembler(id, tier, new ItemStackResult(result), width, height, ing);
	}
}