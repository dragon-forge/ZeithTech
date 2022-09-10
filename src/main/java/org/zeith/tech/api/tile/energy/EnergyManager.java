package org.zeith.tech.api.tile.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.IEnergyStorage;
import org.zeith.hammerlib.util.charging.IChargeHandler;
import org.zeith.hammerlib.util.charging.ItemChargeHelper;
import org.zeith.hammerlib.util.charging.fe.FECharge;
import org.zeith.tech.api.enums.SidedConfigTyped;
import org.zeith.tech.api.tile.sided.ITileSidedConfig;

public class EnergyManager
		implements INBTSerializable<Tag>, IEnergyStorage
{
	public final EnergyStorage2 fe;
	public final int maxAccept, maxSend;
	
	public EnergyManager(int capacity, int maxAccept, int maxSend)
	{
		this.fe = new EnergyStorage2(capacity);
		this.maxAccept = maxAccept;
		this.maxSend = maxSend;
	}
	
	public void charge(ItemStack stack)
	{
		charge(maxSend, stack);
	}
	
	public void charge(int maxTransfer, ItemStack stack)
	{
		if(stack.isEmpty()) return;
		int sent = fe.extractEnergy(maxTransfer, true);
		sent = sent - ItemChargeHelper.charge(stack, new FECharge(sent), IChargeHandler.ChargeAction.EXECUTE).FE;
		fe.extractEnergy(sent, false);
	}
	
	public void update(Level level, BlockPos pos, ITileSidedConfig configs)
	{
		// Split energy based on side configuration.
		if(level.isClientSide || (maxSend <= 0 && maxAccept <= 0))
			return;
		
		var e = configs.getSideConfigs(SidedConfigTyped.ENERGY);
		if(e == null) return;
		
		Direction.stream().forEach(dir ->
		{
			switch(e.getAbsolute(dir))
			{
				case PULL ->
				{
					BlockEntity be;
					if(maxAccept > 0 && (be = level.getBlockEntity(pos.relative(dir))) != null)
						be.getCapability(ForgeCapabilities.ENERGY, dir.getOpposite())
								.filter(IEnergyStorage::canExtract)
								.ifPresent(ies ->
								{
									var max = Math.min(maxAccept, fe.getEnergyTillFull());
									fe.receiveEnergy(ies.extractEnergy(max, false), false);
								});
				}
				case PUSH ->
				{
					BlockEntity be;
					if(maxSend > 0 && (be = level.getBlockEntity(pos.relative(dir))) != null)
						be.getCapability(ForgeCapabilities.ENERGY, dir.getOpposite())
								.filter(IEnergyStorage::canReceive)
								.ifPresent(ies ->
								{
									var max = Math.min(maxSend, fe.getEnergyStored());
									fe.extractEnergy(ies.receiveEnergy(max, false), false);
								});
				}
			}
		});
	}
	
	@Override
	public int receiveEnergy(int maxReceive, boolean simulate)
	{
		return maxAccept > 0 ? this.fe.receiveEnergy(Math.min(maxAccept, maxReceive), simulate) : 0;
	}
	
	@Override
	public int extractEnergy(int maxExtract, boolean simulate)
	{
		return maxSend > 0 ? this.fe.extractEnergy(Math.min(maxSend, maxExtract), simulate) : 0;
	}
	
	public boolean storeEnergy(int store)
	{
		var canSave = this.fe.receiveEnergy(store, true);
		return canSave == store && this.fe.receiveEnergy(store, false) == canSave;
	}
	
	public boolean storeNonZeroEnergy(int store)
	{
		return this.fe.receiveEnergy(store, false) > 0;
	}
	
	public boolean takeEnergy(int needed)
	{
		var canGet = this.fe.extractEnergy(needed, true);
		return canGet == needed && this.fe.extractEnergy(needed, false) == canGet;
	}
	
	@Override
	public int getEnergyStored()
	{
		return fe.getEnergyStored();
	}
	
	@Override
	public int getMaxEnergyStored()
	{
		return fe.getMaxEnergyStored();
	}
	
	@Override
	public boolean canExtract()
	{
		return maxSend > 0;
	}
	
	@Override
	public boolean canReceive()
	{
		return maxAccept > 0;
	}
	
	public float getFillRate()
	{
		int max = getMaxEnergyStored();
		if(max == 0) return getEnergyStored() > 0 ? 1F : 0F;
		return getEnergyStored() / (float) max;
	}
	
	@Override
	public Tag serializeNBT()
	{
		return fe.serializeNBT();
	}
	
	@Override
	public void deserializeNBT(Tag nbt)
	{
		fe.deserializeNBT(nbt);
	}
}