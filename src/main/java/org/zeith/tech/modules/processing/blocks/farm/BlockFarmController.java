package org.zeith.tech.modules.processing.blocks.farm;

import net.minecraft.core.Direction;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.LootContext;
import org.zeith.hammerlib.api.blocks.ICreativeTabBlock;
import org.zeith.hammerlib.core.adapter.BlockHarvestAdapter;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.modules.shared.BaseZT;
import org.zeith.tech.modules.shared.blocks.SimpleBlockZT;

import java.util.List;

public class BlockFarmController
		extends SimpleBlockZT
		implements ICreativeTabBlock
{
	public BlockFarmController()
	{
		super(BaseZT.BASE_MACHINE_PROPS, BlockHarvestAdapter.MineableType.PICKAXE, Tiers.STONE);
		dropsSelf();
	}
	
	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
	{
		return List.of(new ItemStack(this));
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(BlockStateProperties.HORIZONTAL_FACING);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx)
	{
		return defaultBlockState()
				.setValue(BlockStateProperties.HORIZONTAL_FACING, ctx.getPlayer() != null
						? ctx.getPlayer().getDirection().getOpposite()
						: Direction.NORTH);
	}
	
	@Override
	public CreativeModeTab getCreativeTab()
	{
		return ZeithTech.TAB;
	}
}