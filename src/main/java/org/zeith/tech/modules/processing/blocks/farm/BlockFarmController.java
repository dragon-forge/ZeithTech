package org.zeith.tech.modules.processing.blocks.farm;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.LootContext;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.blocks.ICreativeTabBlock;
import org.zeith.hammerlib.core.adapter.BlockHarvestAdapter;
import org.zeith.tech.api.block.IMultiBlockPartWrapListener;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.modules.shared.BaseZT;
import org.zeith.tech.modules.shared.blocks.SimpleBlockZT;
import org.zeith.tech.modules.shared.blocks.multiblock_part.TileMultiBlockPart;

import java.util.List;

public class BlockFarmController
		extends SimpleBlockZT
		implements ICreativeTabBlock, IMultiBlockPartWrapListener
{
	public BlockFarmController()
	{
		super(BaseZT.BASE_MACHINE_PROPS, BlockHarvestAdapter.MineableType.PICKAXE, Tiers.STONE);
		registerDefaultState(defaultBlockState().setValue(BlockStateProperties.ENABLED, false));
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
		builder.add(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.ENABLED);
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
	
	public static int getLightColor(BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int layer)
	{
		if(state.getValue(BlockStateProperties.ENABLED) && layer == -102
				&& pos != null && level != null
				&& level.getBlockEntity(pos) instanceof TileMultiBlockPart p)
		{
			var opt = p.findMultiBlock();
			if(opt.isPresent() && opt.orElseThrow() instanceof TileFarm farm)
			{
				return farm.algorithmInventory.getItem(0).getBarColor();
			}
		}
		return -1;
	}
	
	@Override
	public BlockState formToPart(TileMultiBlockPart part, Level level, BlockPos pos, BlockState originState)
	{
		return originState.setValue(BlockStateProperties.ENABLED, true);
	}
	
	@Override
	public BlockState deformFromPart(TileMultiBlockPart part, Level level, BlockPos pos, BlockState formedState)
	{
		return formedState.setValue(BlockStateProperties.ENABLED, false);
	}
}