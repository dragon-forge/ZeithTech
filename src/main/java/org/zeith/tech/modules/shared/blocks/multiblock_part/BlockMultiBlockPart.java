package org.zeith.tech.modules.shared.blocks.multiblock_part;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.*;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.blocks.INoItemBlock;
import org.zeith.hammerlib.api.forge.BlockAPI;
import org.zeith.hammerlib.core.adapter.BlockHarvestAdapter;
import org.zeith.tech.api.block.multiblock.base.IMultiBlockElement;
import org.zeith.tech.api.tile.multiblock.IMultiblockTile;

import java.util.List;

import static org.zeith.tech.api.block.ZeithTechStateProperties.VISIBLE;

public class BlockMultiBlockPart
		extends BaseEntityBlock
		implements INoItemBlock
{
	public BlockMultiBlockPart(Properties props)
	{
		super(props);
		BlockHarvestAdapter.bindTool(BlockHarvestAdapter.MineableType.PICKAXE, Tiers.WOOD, this);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext ctx)
	{
		if(getter.getBlockEntity(pos) instanceof TileMultiBlockPart part)
			return part.findMultiBlock()
					.map(mbt -> mbt.getShapeFor(pos))
					.orElseGet(() -> part.subState != null ? part.subState.getShape(getter, pos) : Shapes.block());
		return Shapes.block();
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(VISIBLE);
		super.createBlockStateDefinition(builder);
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new TileMultiBlockPart(pos, state);
	}
	
	@Override
	public RenderShape getRenderShape(BlockState state)
	{
		return state.getValue(VISIBLE) ? RenderShape.MODEL : RenderShape.INVISIBLE;
	}
	
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
	{
		if(level.getBlockEntity(pos) instanceof TileMultiBlockPart part)
		{
			if(part.subState != null && part.subState.getBlock() instanceof IMultiBlockElement elem)
			{
				var res = elem.useAsPart(state, level, pos, player, hand, hit, part);
				if(res.consumesAction()) return res;
			}
			
			if(part.origin != null && level.getBlockEntity(part.origin) instanceof IMultiblockTile mbt)
				return mbt.useFromPart(state, level, pos, player, hand, hit, part);
		}
		
		return InteractionResult.PASS;
	}
	
	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
		return BlockAPI.ticker();
	}
	
	@Override
	public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos)
	{
		if(level.getBlockEntity(pos) instanceof TileMultiBlockPart part && part.subState != null)
			return part.subState.getLightEmission(level, pos);
		return super.getLightEmission(state, level, pos);
	}
	
	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
	{
		BlockEntity tile = builder.getParameter(LootContextParams.BLOCK_ENTITY);
		if(tile instanceof TileMultiBlockPart part && part.subState != null)
			return part.subState.getDrops(builder);
		return List.of();
	}
	
	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState state2, boolean p_60519_)
	{
		var be = level.getBlockEntity(pos);
		super.onRemove(state, level, pos, state2, p_60519_);
		if(be instanceof TileMultiBlockPart part && !(level.getBlockEntity(pos) instanceof TileMultiBlockPart))
			part.findMultiBlock().ifPresent(IMultiblockTile::queueMultiBlockValidityCheck);
	}
	
	@Override
	public boolean canHarvestBlock(BlockState state, BlockGetter level, BlockPos pos, Player player)
	{
		if(level.getBlockEntity(pos) instanceof TileMultiBlockPart part && part.subState != null)
			return part.subState.canHarvestBlock(level, pos, player);
		return super.canHarvestBlock(state, level, pos, player);
	}
	
	@Override
	public boolean canEntityDestroy(BlockState state, BlockGetter level, BlockPos pos, Entity entity)
	{
		if(level.getBlockEntity(pos) instanceof TileMultiBlockPart part && part.subState != null)
			return part.subState.canEntityDestroy(level, pos, entity);
		return super.canEntityDestroy(state, level, pos, entity);
	}
	
	@Override
	public float getDestroyProgress(BlockState state, Player player, BlockGetter getter, BlockPos pos)
	{
		if(getter.getBlockEntity(pos) instanceof TileMultiBlockPart part && part.subState != null)
			return part.subState.getDestroyProgress(player, getter, pos);
		return super.getDestroyProgress(state, player, getter, pos);
	}
	
	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player)
	{
		if(level.getBlockEntity(pos) instanceof TileMultiBlockPart part && part.subState != null)
			return part.subState.getCloneItemStack(target, level, pos, player);
		return ItemStack.EMPTY;
	}
	
	@Override
	protected void spawnDestroyParticles(Level level, Player player, BlockPos pos, BlockState state)
	{
		if(level.getBlockEntity(pos) instanceof TileMultiBlockPart part && part.subState != null)
		{
			level.levelEvent(player, 2001, pos, getId(part.subState));
			return;
		}
		
		level.levelEvent(player, 2001, pos, getId(state));
	}
	
	@Override
	public boolean addLandingEffects(BlockState state1, ServerLevel level, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles)
	{
		if(level.getBlockEntity(pos) instanceof TileMultiBlockPart part && part.subState != null)
		{
			if(!part.subState.addLandingEffects(level, pos, state2, entity, numberOfParticles))
				level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, part.subState).setPos(pos), entity.getX(), entity.getY(), entity.getZ(), numberOfParticles, 0.0D, 0.0D, 0.0D, (double) 0.15F);
		}
		
		return true;
	}
	
	@Override
	public boolean addRunningEffects(BlockState state, Level level, BlockPos pos, Entity entity)
	{
		if(level.getBlockEntity(pos) instanceof TileMultiBlockPart part && part.subState != null)
		{
			Vec3 vec3 = entity.getDeltaMovement();
			var random = level.getRandom();
			level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, part.subState).setPos(pos), entity.getX() + (random.nextDouble() - 0.5D) * entity.getBbWidth(), entity.getY() + 0.1D, entity.getZ() + (random.nextDouble() - 0.5D) * entity.getBbWidth(), vec3.x * -4.0D, 1.5D, vec3.z * -4.0D);
		}
		
		return true;
	}
}
