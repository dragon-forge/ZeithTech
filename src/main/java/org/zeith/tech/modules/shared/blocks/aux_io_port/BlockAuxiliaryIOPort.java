package org.zeith.tech.modules.shared.blocks.aux_io_port;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import org.zeith.hammerlib.core.adapter.BlockHarvestAdapter;
import org.zeith.tech.modules.shared.blocks.BaseEntityBlockZT;

public class BlockAuxiliaryIOPort
		extends BaseEntityBlockZT
{
	public static final BooleanProperty ALT = BooleanProperty.create("alt");
	
	public BlockAuxiliaryIOPort()
	{
		super(Block.Properties
						.of(Material.METAL)
						.requiresCorrectToolForDrops()
						.sound(SoundType.METAL)
						.strength(1.5F),
				BlockHarvestAdapter.MineableType.PICKAXE,
				Tiers.IRON);
		dropsSelf();
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b)
	{
		b.add(ALT);
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new TileAuxiliaryIOPort(pos, state);
	}
}