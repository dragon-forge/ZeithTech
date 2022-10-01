package org.zeith.tech.modules.shared;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import org.zeith.hammerlib.core.adapter.TagAdapter;
import org.zeith.tech.ZeithTech;
import org.zeith.tech.modules.world.blocks.BlockHeveaLeaves;
import org.zeith.tech.modules.world.blocks.BlockHeveaLog;

import java.util.function.UnaryOperator;

public class BaseZT
{
	public static Item newItem()
	{
		return new Item(itemProps());
	}
	
	public static Item newItem(Item.Properties props)
	{
		return new Item(props);
	}
	
	public static Item newItem(UnaryOperator<Item.Properties> props)
	{
		return new Item(props.apply(itemProps()));
	}
	
	public static Item newItem(TagKey<Item> tag)
	{
		var item = newItem();
		TagAdapter.bindStaticTag(tag, item);
		return item;
	}
	
	public static Item.Properties itemProps()
	{
		return new Item.Properties().tab(ZeithTech.TAB);
	}
	
	public static BlockHeveaLog heveaLog(MaterialColor p_50789_, MaterialColor p_50790_, boolean leaking)
	{
		var props = BlockBehaviour.Properties.of(Material.WOOD, state -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? p_50789_ : p_50790_).strength(2.0F).sound(SoundType.WOOD);
		
		return leaking ? new BlockHeveaLog.Leaking(props) : new BlockHeveaLog(props);
	}
	
	public static BlockHeveaLeaves leaves(SoundType sounds)
	{
		return new BlockHeveaLeaves(BlockBehaviour.Properties.of(Material.LEAVES).strength(0.2F).randomTicks().sound(sounds).noOcclusion().isValidSpawn(BaseZT::ocelotOrParrot).isSuffocating(BaseZT::never).isViewBlocking(BaseZT::never));
	}
	
	public static Boolean ocelotOrParrot(BlockState p_50822_, BlockGetter p_50823_, BlockPos p_50824_, EntityType<?> p_50825_)
	{
		return p_50825_ == EntityType.OCELOT || p_50825_ == EntityType.PARROT;
	}
	
	public static boolean never(BlockState p_50822_, BlockGetter p_50823_, BlockPos p_50824_, EntityType<?> p_50825_)
	{
		return false;
	}
	
	public static boolean never(BlockState p_50806_, BlockGetter p_50807_, BlockPos p_50808_)
	{
		return false;
	}
}