package org.zeith.tech.modules.transport.recipes;

import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.StrictNBTIngredient;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.tech.modules.shared.init.ItemsZT;
import org.zeith.tech.utils.ItemStackHelper;

@SimplyRegister
public class RecipeTurnFacadesBackIntoBlock
		extends ShapelessRecipe
{
	@RegistryName("facade_to_block")
	RecipeSerializer<RecipeTurnFacadesBackIntoBlock> SHAPELESS_RECIPE = new Serializer();
	
	public RecipeTurnFacadesBackIntoBlock(ResourceLocation id, String group)
	{
		super(
				id,
				group,
				new ItemStack(Items.STONE),
				NonNullList.withSize(6,
						StrictNBTIngredient.of(ItemsZT.FACADE.forItem(new ItemStack(Items.STONE), false))
				)
		);
	}
	
	@Override
	public RecipeSerializer<?> getSerializer()
	{
		return SHAPELESS_RECIPE;
	}
	
	@Override
	public boolean matches(CraftingContainer container, Level level)
	{
		ItemStack facade = ItemStack.EMPTY;
		int facadeCount = 0;
		
		for(int i = 0; i < container.getContainerSize(); ++i)
		{
			var it = container.getItem(i);
			if(facade.isEmpty())
			{
				if(it.is(ItemsZT.FACADE))
				{
					facade = it;
					++facadeCount;
				} else if(!it.isEmpty()) return false;
			} else if(ItemStackHelper.matchesIgnoreCount(it, facade))
			{
				++facadeCount;
			} else if(!it.isEmpty())
				return false;
		}
		
		return !facade.isEmpty() && facadeCount == 6;
	}
	
	@Override
	public ItemStack assemble(CraftingContainer container)
	{
		ItemStack facade = ItemStack.EMPTY;
		int facadeCount = 0;
		
		for(int i = 0; i < container.getContainerSize(); ++i)
		{
			var it = container.getItem(i);
			if(facade.isEmpty())
			{
				if(it.is(ItemsZT.FACADE))
				{
					facade = it;
					++facadeCount;
				} else if(!it.isEmpty()) return ItemStack.EMPTY;
			} else if(ItemStackHelper.matchesIgnoreCount(it, facade))
			{
				++facadeCount;
			} else if(!it.isEmpty())
				return ItemStack.EMPTY;
		}
		
		return !facade.isEmpty() && facadeCount == 6 ? ItemsZT.FACADE.getFacadeBlockStack(facade) : ItemStack.EMPTY;
	}
	
	public static class Serializer
			implements RecipeSerializer<RecipeTurnFacadesBackIntoBlock>
	{
		@Override
		public RecipeTurnFacadesBackIntoBlock fromJson(ResourceLocation id, JsonObject obj)
		{
			String group = GsonHelper.getAsString(obj, "group", "");
			return new RecipeTurnFacadesBackIntoBlock(id, group);
		}
		
		@Override
		public RecipeTurnFacadesBackIntoBlock fromNetwork(ResourceLocation id, FriendlyByteBuf buf)
		{
			String s = buf.readUtf();
			return new RecipeTurnFacadesBackIntoBlock(id, s);
		}
		
		@Override
		public void toNetwork(FriendlyByteBuf buf, RecipeTurnFacadesBackIntoBlock recipe)
		{
			buf.writeUtf(recipe.getGroup());
		}
	}
}