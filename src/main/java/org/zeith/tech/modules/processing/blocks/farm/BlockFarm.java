package org.zeith.tech.modules.processing.blocks.farm;

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
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.blocks.INoItemBlock;
import org.zeith.hammerlib.api.fml.IRegisterListener;
import org.zeith.hammerlib.util.mcf.RotationHelper;
import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.api.block.multiblock.BlockStatePredicate;
import org.zeith.tech.api.block.multiblock.base.*;
import org.zeith.tech.api.utils.LazyValue;
import org.zeith.tech.modules.processing.blocks.base.machine.BlockBaseMachine;
import org.zeith.tech.modules.processing.init.BlocksZT_Processing;
import org.zeith.tech.modules.shared.blocks.multiblock_part.TileMultiBlockPart;
import org.zeith.tech.modules.shared.init.BlocksZT;
import org.zeith.tech.modules.shared.init.TagsZT;

public class BlockFarm
		extends BlockBaseMachine<TileFarm>
		implements IRegisterListener, INoItemBlock
{
	private static MultiBlockFormer<?> FARM_STRUCTURE;
	
	public static final VoxelShape SHAPE = box(4, 4, 4, 12, 12, 12);
	
	public BlockFarm()
	{
		super(TileFarm.class, Block.Properties
				.of(Material.STONE)
				.requiresCorrectToolForDrops()
				.sound(SoundType.STONE)
				.strength(5F)
		);
	}
	
	public static MultiBlockFormer<?> getFarmStructure()
	{
		return FARM_STRUCTURE;
	}
	
	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new TileFarm(pos, state);
	}
	
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult ray)
	{
		if(world.getBlockEntity(pos) instanceof TileFarm tile && !tile.isValid)
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
	public void onPostRegistered()
	{
		var ports = BlockStatePredicate.blockList(BlocksZT.FARM_FLUID_PORT, BlocksZT.FARM_ITEM_PORT, BlocksZT.FARM_ENERGY_PORT);
		
		FARM_STRUCTURE = MultiBlockRegistry.register(ZeithTechAPI.id("farm"), new MultiBlockFormer<>(false,
				new MultiBlockMetadata<>(FarmMultiblockData::new, FarmMultiblockData::isValid, (level, origin, direction, data) ->
				{
					level.setBlockAndUpdate(origin,
							defaultBlockState()
									.setValue(BlockStateProperties.HORIZONTAL_FACING, direction)
					);
				}),
				new MultiBlockFormer.MultiblockPart[] {
						MultiBlockFormer.ofBlock(-1, -1, -1, BlocksZT.REINFORCED_PLANKS).or(ports),
						MultiBlockFormer.ofBlock(-1, -1, 0, BlocksZT.REINFORCED_PLANKS).or(ports),
						MultiBlockFormer.ofBlock(-1, -1, 1, BlocksZT.REINFORCED_PLANKS).or(ports),
						MultiBlockFormer.ofBlock(0, -1, -1, BlocksZT.REINFORCED_PLANKS).or(ports),
						MultiBlockFormer.ofBlock(0, -1, 0, BlocksZT.REINFORCED_PLANKS).or(ports),
						MultiBlockFormer.ofBlock(0, -1, 1, BlocksZT.REINFORCED_PLANKS).or(ports),
						MultiBlockFormer.ofBlock(1, -1, -1, BlocksZT.REINFORCED_PLANKS).or(ports),
						MultiBlockFormer.ofBlock(1, -1, 0, BlocksZT.REINFORCED_PLANKS).or(ports),
						MultiBlockFormer.ofBlock(1, -1, 1, BlocksZT.REINFORCED_PLANKS).or(ports)
				},
				new MultiBlockFormer.MultiblockPart[] {
						MultiBlockFormer.ofBlockTagInvisible(-1, 0, -1, TagsZT.Blocks.STORAGE_BLOCKS_TIN),
						MultiBlockFormer.ofBlockTagInvisible(-1, 0, 0, TagsZT.Blocks.STORAGE_BLOCKS_TIN),
						MultiBlockFormer.ofBlockTagInvisible(-1, 0, 1, TagsZT.Blocks.STORAGE_BLOCKS_TIN),
						
						new MultiBlockFormer.MultiblockPart(new MultiBlockFormer.VisibleBlockPos(0, 0, -1, false), new BlockStatePredicate((state, getter, pos) -> state.is(BlocksZT_Processing.FARM_CONTROLLER), LazyValue.of(() -> new BlockState[] { BlocksZT_Processing.FARM_CONTROLLER.defaultBlockState() })))
						{
							@Override
							public boolean test(BlockPos origin, RotationHelper.PivotRotation rotation, Level level, BlockPos pos)
							{
								return super.test(origin, rotation, level, pos)
										&& TileMultiBlockPart.getPartState(level, pos).getValue(BlockStateProperties.HORIZONTAL_FACING) == rotation.toHorizontal()
										;
							}
						},
						
						MultiBlockFormer.air(0, 0, 0).or(BlockStatePredicate.block(this)),
						MultiBlockFormer.ofBlockTagInvisible(0, 0, 1, TagsZT.Blocks.STORAGE_BLOCKS_TIN),
						MultiBlockFormer.ofBlockTagInvisible(1, 0, -1, TagsZT.Blocks.STORAGE_BLOCKS_TIN),
						MultiBlockFormer.ofBlockTagInvisible(1, 0, 0, TagsZT.Blocks.STORAGE_BLOCKS_TIN),
						MultiBlockFormer.ofBlockTagInvisible(1, 0, 1, TagsZT.Blocks.STORAGE_BLOCKS_TIN)
				},
				new MultiBlockFormer.MultiblockPart[] {
						MultiBlockFormer.ofBlockTagInvisible(-1, 1, -1, Tags.Blocks.STORAGE_BLOCKS_IRON),
						MultiBlockFormer.ofBlockTagInvisible(-1, 1, 0, Tags.Blocks.STORAGE_BLOCKS_IRON),
						MultiBlockFormer.ofBlockTagInvisible(-1, 1, 1, Tags.Blocks.STORAGE_BLOCKS_IRON),
						MultiBlockFormer.ofBlockTagInvisible(0, 1, -1, Tags.Blocks.STORAGE_BLOCKS_IRON),
						MultiBlockFormer.ofBlockInvisible(0, 1, 0, BlocksZT.BASIC_FLUID_TANK),
						MultiBlockFormer.ofBlockTagInvisible(0, 1, 1, Tags.Blocks.STORAGE_BLOCKS_IRON),
						MultiBlockFormer.ofBlockTagInvisible(1, 1, -1, Tags.Blocks.STORAGE_BLOCKS_IRON),
						MultiBlockFormer.ofBlockTagInvisible(1, 1, 0, Tags.Blocks.STORAGE_BLOCKS_IRON),
						MultiBlockFormer.ofBlockTagInvisible(1, 1, 1, Tags.Blocks.STORAGE_BLOCKS_IRON)
				},
				new MultiBlockFormer.MultiblockPart[] {
						MultiBlockFormer.ofBlockInvisible(0, 2, 0, BlocksZT.IRON_FLUID_PIPE)
				}
		));
	}
	
	@Override
	public boolean canBeHydrated(BlockState state, BlockGetter getter, BlockPos pos, FluidState fluid, BlockPos fluidPos)
	{
		return super.canBeHydrated(state, getter, pos, fluid, fluidPos);
	}
}
