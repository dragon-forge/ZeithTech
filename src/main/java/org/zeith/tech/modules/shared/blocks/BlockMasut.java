package org.zeith.tech.modules.shared.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.core.adapter.BlockHarvestAdapter;
import org.zeith.hammerlib.net.Network;
import org.zeith.tech.modules.shared.net.PacketSpawnMasutEntityParticles;

public class BlockMasut
		extends SimpleBlockZT
{
	protected static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D);
	
	public BlockMasut(Properties props, BlockHarvestAdapter.MineableType toolType)
	{
		super(props, toolType);
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState p_60572_, BlockGetter p_60573_, BlockPos p_60574_, CollisionContext p_60575_)
	{
		return SHAPE;
	}
	
	@Override
	public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float height)
	{
		entity.playSound(SoundEvents.HONEY_BLOCK_SLIDE, 1.0F, 1.0F);
		
		if(!level.isClientSide)
		{
			if(entity instanceof ServerPlayer sp)
				Network.sendTo(new PacketSpawnMasutEntityParticles(entity.getId(), 10), sp);
			Network.sendToTracking(new PacketSpawnMasutEntityParticles(entity.getId(), 10), entity);
		}
		
		if(entity.causeFallDamage(height, 0.2F, DamageSource.FALL))
		{
			entity.playSound(this.soundType.getFallSound(), this.soundType.getVolume() * 0.5F, this.soundType.getPitch() * 0.75F);
		}
	}
	
	@Override
	public void entityInside(BlockState p_54003_, Level p_54004_, BlockPos p_54005_, Entity p_54006_)
	{
		if(this.isSlidingDown(p_54005_, p_54006_))
		{
			this.doSlideMovement(p_54006_);
			this.maybeDoSlideEffects(p_54004_, p_54006_);
		}
		
		super.entityInside(p_54003_, p_54004_, p_54005_, p_54006_);
	}
	
	private void doSlideMovement(Entity entity)
	{
		Vec3 vec3 = entity.getDeltaMovement();
		if(vec3.y < -0.13D)
		{
			double d0 = -0.05D / vec3.y;
			entity.setDeltaMovement(new Vec3(vec3.x * d0, -0.05D, vec3.z * d0));
		} else
		{
			entity.setDeltaMovement(new Vec3(vec3.x, -0.05D, vec3.z));
		}
		
		entity.resetFallDistance();
	}
	
	private static boolean doesEntityDoHoneyBlockSlideEffects(Entity entity)
	{
		return entity instanceof LivingEntity || entity instanceof AbstractMinecart || entity instanceof PrimedTnt || entity instanceof Boat;
	}
	
	@Override
	public @Nullable BlockPathTypes getBlockPathType(BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob)
	{
		return BlockPathTypes.STICKY_HONEY;
	}
	
	private void maybeDoSlideEffects(Level level, Entity entity)
	{
		if(doesEntityDoHoneyBlockSlideEffects(entity))
		{
			if(level.random.nextInt(5) == 0)
			{
				entity.playSound(SoundEvents.HONEY_BLOCK_SLIDE, 1.0F, 1.0F);
			}
			
			if(!level.isClientSide && level.random.nextInt(5) == 0)
			{
				if(entity instanceof ServerPlayer sp)
					Network.sendTo(new PacketSpawnMasutEntityParticles(entity.getId(), 5), sp);
				Network.sendToTracking(new PacketSpawnMasutEntityParticles(entity.getId(), 5), entity);
			}
		}
	}
	
	private boolean isSlidingDown(BlockPos pos, Entity entity)
	{
		if(entity.isOnGround())
		{
			return false;
		} else if(entity.getY() > (double) pos.getY() + 0.9375D - 1.0E-7D)
		{
			return false;
		} else if(entity.getDeltaMovement().y >= -0.08D)
		{
			return false;
		} else
		{
			double d0 = Math.abs((double) pos.getX() + 0.5D - entity.getX());
			double d1 = Math.abs((double) pos.getZ() + 0.5D - entity.getZ());
			double d2 = 0.4375D + (double) (entity.getBbWidth() / 2.0F);
			return d0 + 1.0E-7D > d2 || d1 + 1.0E-7D > d2;
		}
	}
	
	@Override
	public boolean canStickTo(BlockState state, BlockState other)
	{
		return !other.isStickyBlock() || state.getBlock() == other.getBlock();
	}
	
	@Override
	public boolean isStickyBlock(BlockState state)
	{
		return true;
	}
}