package org.zeith.tech.modules.world.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.zeith.tech.modules.world.init.*;

public class BoatZT
		extends Boat
{
	private static final EntityDataAccessor<Integer> DATA_ID_TYPE = SynchedEntityData.defineId(BoatZT.class, EntityDataSerializers.INT);
	
	public BoatZT(EntityType<? extends BoatZT> p_38290_, Level p_38291_)
	{
		super(p_38290_, p_38291_);
		this.blocksBuilding = true;
	}
	
	public BoatZT(Level p_38293_, double p_38294_, double p_38295_, double p_38296_)
	{
		this(EntitiesZT_World.BOAT, p_38293_);
		this.setPos(p_38294_, p_38295_, p_38296_);
		this.xo = p_38294_;
		this.yo = p_38295_;
		this.zo = p_38296_;
	}
	
	@Override
	protected Component getTypeName()
	{
		return EntityType.BOAT.getDescription();
	}
	
	@Override
	protected void defineSynchedData()
	{
		super.defineSynchedData();
		this.entityData.define(DATA_ID_TYPE, Type.HEVEA.ordinal());
	}
	
	@Override
	public Item getDropItem()
	{
		return switch(this.getBoatTypeZT())
				{
					default -> ItemsZT_World.HEVEA_BOAT;
					case HEVEA -> ItemsZT_World.HEVEA_BOAT;
				};
	}
	
	@Override
	protected void checkFallDamage(double p_38307_, boolean p_38308_, BlockState p_38309_, BlockPos p_38310_)
	{
		this.lastYd = this.getDeltaMovement().y;
		if(!this.isPassenger())
		{
			if(p_38308_)
			{
				if(this.fallDistance > 3.0F)
				{
					if(this.status != Boat.Status.ON_LAND)
					{
						this.resetFallDistance();
						return;
					}
					
					this.causeFallDamage(this.fallDistance, 1.0F, DamageSource.FALL);
					if(!this.level.isClientSide && !this.isRemoved())
					{
						this.kill();
						if(this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS))
						{
							for(int i = 0; i < 3; ++i)
							{
								this.spawnAtLocation(this.getBoatTypeZT().getPlanks());
							}
							
							for(int j = 0; j < 2; ++j)
							{
								this.spawnAtLocation(Items.STICK);
							}
						}
					}
				}
				
				this.resetFallDistance();
			} else if(!this.canBoatInFluid(this.level.getFluidState(this.blockPosition().below())) && p_38307_ < 0.0D)
			{
				this.fallDistance -= (float) p_38307_;
			}
		}
	}
	
	@Override
	protected void addAdditionalSaveData(CompoundTag tag)
	{
		tag.putString("Type", this.getBoatTypeZT().getName());
	}
	
	@Override
	protected void readAdditionalSaveData(CompoundTag tag)
	{
		if(tag.contains("Type", 8))
		{
			this.setType(BoatZT.Type.byName(tag.getString("Type")));
		}
	}
	
	public void setType(BoatZT.Type type)
	{
		this.entityData.set(DATA_ID_TYPE, type.ordinal());
	}
	
	public Type getBoatTypeZT()
	{
		return Type.byId(this.entityData.get(DATA_ID_TYPE));
	}
	
	public enum Type
	{
		HEVEA(BlocksZT_World.HEVEA_PLANKS, "hevea");
		
		private final String name;
		private final Block planks;
		
		Type(Block p_38427_, String p_38428_)
		{
			this.name = p_38428_;
			this.planks = p_38427_;
		}
		
		public String getName()
		{
			return this.name;
		}
		
		public Block getPlanks()
		{
			return this.planks;
		}
		
		public String toString()
		{
			return this.name;
		}
		
		public static BoatZT.Type byId(int id)
		{
			BoatZT.Type[] values = values();
			
			if(id < 0 || id >= values.length)
				id = 0;
			
			return values[id];
		}
		
		public static BoatZT.Type byName(String name)
		{
			BoatZT.Type[] values = values();
			
			for(int i = 0; i < values.length; ++i)
				if(values[i].getName().equals(name))
					return values[i];
			
			return values[0];
		}
	}
}