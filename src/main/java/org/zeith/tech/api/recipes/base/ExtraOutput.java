package org.zeith.tech.api.recipes.base;

import com.google.gson.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ShapedRecipe;

import java.util.List;
import java.util.stream.IntStream;

public class ExtraOutput
{
	protected final ItemStack output;
	protected final float chance;
	
	public ExtraOutput(ItemStack output, float chance)
	{
		this.output = output;
		this.chance = chance;
	}
	
	public void toNetwork(FriendlyByteBuf buf)
	{
		buf.writeShort(0);
		buf.writeItemStack(output, false);
		buf.writeFloat(chance);
	}
	
	public static void toNetwork(FriendlyByteBuf buf, ExtraOutput instance)
	{
		instance.toNetwork(buf);
	}
	
	public static ExtraOutput fromNetwork(FriendlyByteBuf buf)
	{
		var type = buf.readShort();
		
		return switch(type)
				{
					default ->
					{
						var out = buf.readItem();
						var chance = buf.readFloat();
						yield new ExtraOutput(out, chance);
					}
					case 1 ->
					{
						var out = buf.readItem();
						var chance = buf.readFloat();
						var minInclusive = buf.readInt();
						var maxInclusive = buf.readInt();
						yield new ExtraOutput.Ranged(out, minInclusive, maxInclusive, chance);
					}
				};
	}
	
	public static List<ExtraOutput> parse(JsonElement ex)
	{
		if(ex.isJsonArray()) return parse(ex.getAsJsonArray());
		return List.of(parse(ex.getAsJsonObject()));
	}
	
	public static List<ExtraOutput> parse(JsonArray extras)
	{
		return IntStream.range(0, extras.size())
				.mapToObj(extras::get)
				.map(ExtraOutput::parse)
				.flatMap(List::stream)
				.toList();
	}
	
	public static ExtraOutput parse(JsonObject ex)
	{
		ExtraOutput extra = null;
		genExtra:
		if(ex != null)
		{
			var extraItem = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(ex, "item"));
			var chance = GsonHelper.getAsFloat(ex, "chance", 1F);
			
			if(ex.has("max"))
			{
				var min = GsonHelper.getAsInt(ex, "min", 1);
				var max = GsonHelper.getAsInt(ex, "max");
				extra = new ExtraOutput.Ranged(extraItem, min, max, chance);
				break genExtra;
			}
			
			extra = new ExtraOutput(extraItem, chance);
		}
		return extra;
	}
	
	public ItemStack getMaximalResult()
	{
		return output.copy();
	}
	
	public ItemStack getMinimalResult()
	{
		return output.copy();
	}
	
	public ItemStack assemble(RandomSource random)
	{
		return output.copy();
	}
	
	public float chance()
	{
		return chance;
	}
	
	public List<ItemStack> getJeiItems()
	{
		return List.of(getMaximalResult());
	}
	
	public static class Ranged
			extends ExtraOutput
	{
		protected final int minInclusive, maxInclusive;
		
		public Ranged(ItemStack output, int minInclusive, int maxInclusive, float chance)
		{
			super(output, chance);
			this.minInclusive = minInclusive;
			this.maxInclusive = maxInclusive;
		}
		
		public Ranged(ItemStack output, int minInclusive, int maxInclusive)
		{
			this(output, minInclusive, maxInclusive, 1F);
		}
		
		@Override
		public void toNetwork(FriendlyByteBuf buf)
		{
			buf.writeShort(1);
			buf.writeItemStack(output, false);
			buf.writeFloat(chance);
			buf.writeInt(minInclusive);
			buf.writeInt(maxInclusive);
		}
		
		@Override
		public ItemStack getMaximalResult()
		{
			return forCount(maxInclusive);
		}
		
		@Override
		public ItemStack getMinimalResult()
		{
			return forCount(minInclusive);
		}
		
		protected ItemStack forCount(int i)
		{
			var out = super.getMaximalResult();
			out.setCount(out.getCount() * i);
			return out;
		}
		
		@Override
		public List<ItemStack> getJeiItems()
		{
			return IntStream.range(minInclusive, maxInclusive + 1)
					.mapToObj(this::forCount)
					.toList();
		}
		
		@Override
		public ItemStack assemble(RandomSource random)
		{
			var out = super.getMaximalResult();
			out.setCount(out.getCount() * random.nextIntBetweenInclusive(minInclusive, maxInclusive));
			return out;
		}
	}
}