package org.zeith.tech.modules.processing.blocks.blast_furnace;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.blocks.INoItemBlock;
import org.zeith.hammerlib.api.fml.IRegisterListener;
import org.zeith.tech.api.block.multiblock.base.MultiBlockFormer;
import org.zeith.tech.api.block.multiblock.base.MultiBlockRegistry;
import org.zeith.tech.modules.processing.blocks.base.machine.BlockBaseMachine;
import org.zeith.tech.modules.shared.init.BlocksZT;
import org.zeith.tech.modules.shared.init.TagsZT;

public class BlockBlastFurnaceB
		extends BlockBaseMachine<TileBlastFurnaceB>
		implements IRegisterListener, INoItemBlock
{
	public static MultiBlockFormer BASIC_BLAST_FURNACE;
	
	public static final VoxelShape SHAPE = box(4, 4, 4, 12, 12, 12);
	
	public BlockBlastFurnaceB()
	{
		super(TileBlastFurnaceB.class, Block.Properties
				.of(Material.STONE)
				.requiresCorrectToolForDrops()
				.sound(SoundType.STONE)
				.instabreak()
		);
	}
	
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult ray)
	{
		if(world.getBlockEntity(pos) instanceof TileBlastFurnaceB tile && !tile.isValid)
			return InteractionResult.PASS;
		return super.use(state, world, pos, player, hand, ray);
	}
	
	@Override
	public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_)
	{
		return SHAPE;
	}
	
	@Override
	public RenderShape getRenderShape(BlockState state)
	{
		return RenderShape.INVISIBLE;
	}
	
	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new TileBlastFurnaceB(pos, state);
	}
	
	@Override
	public void onPostRegistered()
	{
		BASIC_BLAST_FURNACE = MultiBlockRegistry.register(new MultiBlockFormer(true,
				(level, origin, direction) ->
				{
					level.setBlockAndUpdate(origin,
							defaultBlockState()
									.setValue(BlockStateProperties.HORIZONTAL_FACING, direction)
					);
				},
				new MultiBlockFormer.MultiblockPart[] {
						MultiBlockFormer.ofBlockTag(-1, -1, -1, TagsZT.Blocks.COMPOSITE_BRICKS),
						MultiBlockFormer.ofBlockTag(-1, -1, 0, TagsZT.Blocks.COMPOSITE_BRICKS),
						MultiBlockFormer.ofBlockTag(-1, -1, 1, TagsZT.Blocks.COMPOSITE_BRICKS),
						MultiBlockFormer.ofBlockTag(0, -1, -1, TagsZT.Blocks.COMPOSITE_BRICKS),
						MultiBlockFormer.ofBlock(0, -1, 0, BlocksZT.BLAST_FURNACE_BURNER),
						MultiBlockFormer.ofBlockTag(0, -1, 1, TagsZT.Blocks.COMPOSITE_BRICKS),
						MultiBlockFormer.ofBlockTag(1, -1, -1, TagsZT.Blocks.COMPOSITE_BRICKS),
						MultiBlockFormer.ofBlockTag(1, -1, 0, TagsZT.Blocks.COMPOSITE_BRICKS),
						MultiBlockFormer.ofBlockTag(1, -1, 1, TagsZT.Blocks.COMPOSITE_BRICKS)
				},
				new MultiBlockFormer.MultiblockPart[] {
						MultiBlockFormer.ofBlockTag(-1, 0, -1, TagsZT.Blocks.COMPOSITE_BRICKS),
						MultiBlockFormer.ofBlockTag(-1, 0, 0, TagsZT.Blocks.COMPOSITE_BRICKS),
						MultiBlockFormer.ofBlockTag(-1, 0, 1, TagsZT.Blocks.COMPOSITE_BRICKS),
						MultiBlockFormer.ofBlockTag(0, 0, -1, TagsZT.Blocks.COMPOSITE_BRICKS),
						MultiBlockFormer.air(0, 0, 0).or((state, getter, pos) -> state.is(this)),
						MultiBlockFormer.ofBlockTag(0, 0, 1, TagsZT.Blocks.COMPOSITE_BRICKS),
						MultiBlockFormer.ofBlockTag(1, 0, -1, TagsZT.Blocks.COMPOSITE_BRICKS),
						MultiBlockFormer.ofBlockTag(1, 0, 0, TagsZT.Blocks.COMPOSITE_BRICKS),
						MultiBlockFormer.ofBlockTag(1, 0, 1, TagsZT.Blocks.COMPOSITE_BRICKS)
				},
				new MultiBlockFormer.MultiblockPart[] {
						MultiBlockFormer.ofBlockTag(-1, 1, -1, TagsZT.Blocks.COMPOSITE_BRICKS),
						MultiBlockFormer.ofBlockTag(-1, 1, 0, TagsZT.Blocks.COMPOSITE_BRICKS),
						MultiBlockFormer.ofBlockTag(-1, 1, 1, TagsZT.Blocks.COMPOSITE_BRICKS),
						MultiBlockFormer.ofBlockTag(0, 1, -1, TagsZT.Blocks.COMPOSITE_BRICKS),
						MultiBlockFormer.ofBlockTag(0, 1, 0, TagsZT.Blocks.COMPOSITE_BRICKS),
						MultiBlockFormer.ofBlockTag(0, 1, 1, TagsZT.Blocks.COMPOSITE_BRICKS),
						MultiBlockFormer.ofBlockTag(1, 1, -1, TagsZT.Blocks.COMPOSITE_BRICKS),
						MultiBlockFormer.ofBlockTag(1, 1, 0, TagsZT.Blocks.COMPOSITE_BRICKS),
						MultiBlockFormer.ofBlockTag(1, 1, 1, TagsZT.Blocks.COMPOSITE_BRICKS)
				}
		));
	}
}