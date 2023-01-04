package org.zeith.tech.modules.world.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.jetbrains.annotations.Nullable;
import org.zeith.tech.modules.world.worldgen.WorldFeaturesZT;

public class GrowerHeveaTree
		extends AbstractTreeGrower
{
	@Nullable
	@Override
	protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource p_222910_, boolean p_222911_)
	{
		return null;
	}
	
	private boolean hasFlowers(LevelAccessor level, BlockPos pos)
	{
		for(BlockPos blockpos : BlockPos.MutableBlockPos.betweenClosed(pos.below().north(2).west(2), pos.above().south(2).east(2)))
			if(level.getBlockState(blockpos).is(BlockTags.FLOWERS))
				return true;
		return false;
	}
	
	@Override
	public boolean growTree(ServerLevel level, ChunkGenerator gen, BlockPos pos, BlockState state, RandomSource rng)
	{
		Holder<ConfiguredFeature<?, ?>> holder = this.hasFlowers(level, pos) ? WorldFeaturesZT.HEVEA_TREE_WITH_BEES : WorldFeaturesZT.HEVEA_TREE;
		
		var event = net.minecraftforge.event.ForgeEventFactory.blockGrowFeature(level, rng, pos, holder);
		holder = event.getFeature();
		if(event.getResult() == net.minecraftforge.eventbus.api.Event.Result.DENY) return false;
		
		if(holder == null) return false;
		
		ConfiguredFeature<?, ?> configuredfeature = holder.value();
		BlockState blockstate = level.getFluidState(pos).createLegacyBlock();
		level.setBlock(pos, blockstate, 4);
		if(configuredfeature.place(level, gen, rng, pos))
		{
			if(level.getBlockState(pos) == blockstate)
				level.sendBlockUpdated(pos, state, blockstate, 2);
			return true;
		}
		
		level.setBlock(pos, state, 4);
		return false;
	}
}