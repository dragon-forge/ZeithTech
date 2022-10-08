package org.zeith.tech.modules.processing.blocks.fluid_centrifuge;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.blocks.ICustomBlockItem;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.core.client.renderer.item.BlockItemWithAltISTER;
import org.zeith.tech.modules.processing.blocks.base.machine.BlockBaseMachine;
import org.zeith.tech.modules.processing.init.TilesZT_Processing;

import java.util.function.Consumer;

public class BlockFluidCentrifuge
		extends BlockBaseMachine<TileFluidCentrifuge>
		implements ICustomBlockItem
{
	public static final VoxelShape SHAPE = Shapes.or(
			box(0, 0, 0, 16, 3, 16),
			box(0, 3, 5, 2, 11, 11),
			box(5, 3, 0, 11, 11, 2),
			box(5, 3, 14, 11, 11, 16),
			box(14, 3, 5, 16, 11, 11),
			box(6, 3, 6, 10, 15, 10),
			box(4, 6, 4, 12, 13, 12)
	);
	
	public BlockFluidCentrifuge()
	{
		super(TileFluidCentrifuge.class);
	}
	
	@Override
	public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_)
	{
		return SHAPE;
	}
	
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult ray)
	{
		return InteractionResult.PASS;
	}
	
	@Override
	public @Nullable TileFluidCentrifuge newBlockEntity(BlockPos pos, BlockState state)
	{
		return new TileFluidCentrifuge(pos, state);
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
						.bind(BlockFluidCentrifuge.this, TilesZT_Processing.FLUID_CENTRIFUGE)
						.ifPresent(consumer);
			}
		};
	}
}
