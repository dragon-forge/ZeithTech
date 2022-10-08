package org.zeith.tech.modules.processing.blocks.fluid_pump;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
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
import org.zeith.tech.modules.processing.blocks.base.machine.BlockBaseMachine;
import org.zeith.tech.modules.processing.client.renderer.item.ISTERPump;

import java.util.function.Consumer;

public class BlockFluidPump
		extends BlockBaseMachine<TileFluidPump>
		implements ICustomBlockItem
{
	public static final VoxelShape SHAPE = Shapes.join(
			box(0, 0, 0, 16, 16, 16),
			Shapes.or(
					box(0F, 3F, 0F, 16F, 13F, 0.01F),
					box(16F, 3F, 15.99F, 16F, 13F, 16F),
					
					box(0F, 3F, 0F, 0.01F, 13F, 16F),
					box(15.99F, 3F, 0F, 16F, 13F, 16F)
			),
			BooleanOp.ONLY_FIRST
	);
	
	public BlockFluidPump()
	{
		super(TileFluidPump.class);
	}
	
	@Override
	public boolean isOcclusionShapeFullBlock(BlockState p_222959_, BlockGetter p_222960_, BlockPos p_222961_)
	{
		return false;
	}
	
	@Override
	public boolean isCollisionShapeFullBlock(BlockState p_181242_, BlockGetter p_181243_, BlockPos p_181244_)
	{
		return false;
	}
	
	@Override
	public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_)
	{
		return SHAPE;
	}
	
	@Override
	public @Nullable TileFluidPump newBlockEntity(BlockPos pos, BlockState state)
	{
		return new TileFluidPump(pos, state);
	}
	
	@Override
	public BlockItem createBlockItem()
	{
		return new BlockItem(this, new Item.Properties().tab(ZeithTech.TAB))
		{
			@Override
			public void initializeClient(Consumer<IClientItemExtensions> consumer)
			{
				consumer.accept(new IClientItemExtensions()
				{
					@Override
					public BlockEntityWithoutLevelRenderer getCustomRenderer()
					{
						return ISTERPump.INSTANCE;
					}
				});
			}
		};
	}
}
