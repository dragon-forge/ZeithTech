package org.zeith.tech.modules.processing.blocks.waste_processor;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.*;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.blocks.ICustomBlockItem;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.core.client.renderer.item.BlockItemWithAltISTER;
import org.zeith.tech.modules.processing.blocks.base.machine.BlockBaseMachine;
import org.zeith.tech.modules.processing.init.TilesZT_Processing;

import java.util.function.Consumer;

public class BlockWasteProcessor
		extends BlockBaseMachine<TileWasteProcessor>
		implements ICustomBlockItem
{
	public static final VoxelShape SHAPE = Shapes.join(
			box(0, 0, 0, 16, 16, 16),
			box(1, 3, 1, 15, 15.9, 15),
			BooleanOp.ONLY_FIRST
	);
	
	public BlockWasteProcessor()
	{
		super(TileWasteProcessor.class);
	}
	
	@Override
	public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_)
	{
		return SHAPE;
	}
	
	@Override
	public @Nullable TileWasteProcessor newBlockEntity(BlockPos pos, BlockState state)
	{
		return new TileWasteProcessor(pos, state);
	}
	
	@Override
	public BlockItem createBlockItem()
	{
		return new BlockItem(this, new Item.Properties().tab(ZeithTech.TAB))
		{
			@Override
			public void initializeClient(Consumer<IClientItemExtensions> consumer)
			{
				BlockItemWithAltISTER.INSTANCE
						.bind(BlockWasteProcessor.this, TilesZT_Processing.WASTE_PROCESSOR)
						.ifPresent(consumer);
			}
		};
	}
}
