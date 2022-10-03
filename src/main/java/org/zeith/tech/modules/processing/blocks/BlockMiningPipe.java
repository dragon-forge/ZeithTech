package org.zeith.tech.modules.processing.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.shapes.*;
import org.zeith.hammerlib.core.adapter.BlockHarvestAdapter;
import org.zeith.tech.modules.shared.blocks.SimpleBlockZT;
import org.zeith.tech.modules.shared.init.TagsZT;

import java.util.List;

public class BlockMiningPipe
		extends SimpleBlockZT
{
	public static final VoxelShape PIPE_SHAPE =
			Shapes.join(box(5, 0, 5, 11, 16, 11),
					box(6, 0, 6, 10, 16, 10),
					BooleanOp.ONLY_FIRST);
	
	public BlockMiningPipe(Properties props)
	{
		super(props);
		BlockHarvestAdapter.bindTool(BlockHarvestAdapter.MineableType.PICKAXE, Tiers.STONE, this);
		addItemTag(TagsZT.Items.MINING_PIPE);
		addBlockTag(TagsZT.Blocks.MINING_PIPE);
		dropsSelf();
	}
	
	final RandomSource rand = RandomSource.create();
	
	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
	{
		if(rand.nextInt(10) > 0)
			return List.of(new ItemStack(this));
		else
			return super.getDrops(state, builder);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter lvl, BlockPos pos, CollisionContext ctx)
	{
		return PIPE_SHAPE;
	}
}