package org.zeith.tech.modules.transport.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.zeith.hammerlib.HammerLib;
import org.zeith.hammerlib.event.recipe.BuildTagsEvent;
import org.zeith.tech.modules.shared.init.TagsZT;

public class ItemFacade
		extends Item
{
	public ItemFacade(Properties props)
	{
		super(props);
		
		HammerLib.EVENT_BUS.addListener(this::applyTags);
	}
	
	private void applyTags(BuildTagsEvent e)
	{
		if(e.reg.getRegistryKey() == ForgeRegistries.Keys.BLOCKS)
			e.addAllToTag(TagsZT.Blocks.FACADE_WHITELIST,
					ForgeRegistries.BLOCKS.getValues()
							.stream()
							.filter(this::allowBlockAsFacade)
							.toList()
			);
	}
	
	private boolean allowBlockAsFacade(Block block)
	{
		if(block instanceof AbstractGlassBlock)
			return true;
		
		if(block instanceof LeavesBlock)
			return true;
		
		return false;
	}
	
	@Override
	public Component getName(ItemStack is)
	{
		try
		{
			final ItemStack in = this.getFacadeBlockStack(is);
			if(!in.isEmpty()) return Component.translatable(this.getDescriptionId(is), in.getHoverName());
		} catch(Throwable ignored)
		{
		}
		return super.getName(is);
	}
	
	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items)
	{
	}
	
	public ItemStack forItem(ItemStack itemStack, boolean returnItem)
	{
		Block block;
		if(itemStack.isEmpty() || itemStack.hasTag() || (block = Block.byItem(itemStack.getItem())) == Blocks.AIR)
			return ItemStack.EMPTY;
		
		// We only support the default state for facades. Sorry.
		var blockState = block.defaultBlockState();
		var forcedByTag = blockState.is(TagsZT.Blocks.FACADE_WHITELIST);
		var isModel = blockState.getRenderShape() == RenderShape.MODEL;
		var isBlockEntity = blockState.hasBlockEntity();
		var isFullCube = blockState.isRedstoneConductor(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
		
		if(isModel && (!isBlockEntity || forcedByTag) && (isFullCube || forcedByTag))
		{
			if(returnItem) return itemStack;
			return forItemRaw(itemStack);
		}
		
		return ItemStack.EMPTY;
	}
	
	public ItemStack forItemRaw(ItemStack itemStack)
	{
		var is = new ItemStack(this);
		is.addTagElement("FacadeItem", StringTag.valueOf(ForgeRegistries.ITEMS.getKey(itemStack.getItem()).toString()));
		return is;
	}
	
	public ItemStack getFacadeBlockStack(ItemStack is)
	{
		CompoundTag nbt = is.getTag();
		if(nbt == null) return ItemStack.EMPTY;
		ResourceLocation itemId = new ResourceLocation(nbt.getString("FacadeItem"));
		return new ItemStack(ForgeRegistries.ITEMS.getValue(itemId));
	}
	
	public BlockState getFacadeBlockState(ItemStack is)
	{
		ItemStack baseItemStack = getFacadeBlockStack(is);
		if(baseItemStack.isEmpty()) return null;
		Block block = Block.byItem(baseItemStack.getItem());
		if(block == Blocks.AIR) return null;
		return block.defaultBlockState();
	}
}