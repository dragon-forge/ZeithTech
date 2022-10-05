package org.zeith.tech.api.tile.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.zeith.hammerlib.api.inv.SimpleInventory;
import org.zeith.hammerlib.util.charging.IChargeHandler;
import org.zeith.hammerlib.util.charging.ItemChargeHelper;
import org.zeith.hammerlib.util.charging.fe.FECharge;
import org.zeith.tech.api.enums.SidedConfigTyped;
import org.zeith.tech.api.tile.sided.ITileSidedConfig;

public class EnergyManager
		implements INBTSerializable<CompoundTag>, IEnergyStorage
{
	protected int lastIn, lastOut;
	protected float lastLoad;
	
	protected BlockPos lastPosition;
	public final EnergyMeasurableWrapper measurables = new EnergyMeasurableWrapper(() -> lastPosition, () -> lastLoad);
	
	public final LazyOptional<IEnergyMeasurable> measurableCap = LazyOptional.of(() -> measurables);
	
	public final SimpleInventory batteryInventory = new SimpleInventory(1);
	
	public final EnergyStorage2 fe;
	public final int maxAccept, maxSend;
	
	
	public EnergyManager(int capacity, int maxAccept, int maxSend)
	{
		this.fe = new EnergyStorage2(capacity);
		this.maxAccept = maxAccept;
		this.maxSend = maxSend;
	}
	
	public void chargeItem(ItemStack stack)
	{
		chargeItem(maxSend, stack);
	}
	
	public void chargeItem(int maxTransfer, ItemStack stack)
	{
		if(stack.isEmpty()) return;
		int sent = fe.extractEnergy(maxTransfer, true);
		sent -= ItemChargeHelper.charge(stack, new FECharge(sent), IChargeHandler.ChargeAction.EXECUTE).FE;
		fe.extractEnergy(sent, false);
		measurables.onEnergyTransfer(sent);
	}
	
	public void chargeMachineFromItem(ItemStack stack)
	{
		chargeMachineFromItem(maxAccept, stack);
	}
	
	public void chargeMachineFromItem(int maxTransfer, ItemStack stack)
	{
		if(stack.isEmpty()) return;
		
		stack.getCapability(ForgeCapabilities.ENERGY).ifPresent(energy ->
		{
			int sent = energy.extractEnergy(maxTransfer, true);
			energy.extractEnergy(fe.receiveEnergy(sent, false), false);
			measurables.onEnergyTransfer(sent);
		});
	}
	
	public void update(Level level, BlockPos pos, ITileSidedConfig configs)
	{
		lastPosition = pos;
		
		lastLoad = 0;
		if(maxAccept > 0) lastLoad = Math.max(lastLoad, lastIn / (float) maxAccept);
		if(maxSend > 0) lastLoad = Math.max(lastLoad, lastOut / (float) maxSend);
		lastLoad = Mth.clamp(lastLoad, 0F, 1F);
		lastIn = lastOut = 0;
		
		measurables.update();
		
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
									measurables.onEnergyTransfer(fe.receiveEnergy(ies.extractEnergy(max, false), false));
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
									measurables.onEnergyTransfer(fe.extractEnergy(ies.receiveEnergy(max, false), false));
								});
				}
			}
		});
	}
	
	@Override
	public int receiveEnergy(int maxReceive, boolean simulate)
	{
		var in = maxAccept > 0 ? this.fe.receiveEnergy(Math.min(maxAccept, maxReceive), simulate) : 0;
		if(!simulate)
		{
			measurables.onEnergyTransfer(in);
			lastIn += in;
		}
		return in;
	}
	
	@Override
	public int extractEnergy(int maxExtract, boolean simulate)
	{
		var out = maxSend > 0 ? this.fe.extractEnergy(Math.min(maxSend, maxExtract), simulate) : 0;
		if(!simulate)
		{
			measurables.onEnergyTransfer(out);
			lastOut += out;
		}
		return out;
	}
	
	public boolean generateEnergy(int gen)
	{
		var canSave = this.fe.receiveEnergy(gen, true);
		var did = canSave == gen && this.fe.receiveEnergy(gen, false) == canSave;
		if(did)
		{
			measurables.onEnergyGenerated(gen);
			lastIn += gen;
		}
		return did;
	}
	
	public boolean generateAnyEnergy(int gen)
	{
		var rec = this.fe.receiveEnergy(gen, false);
		measurables.onEnergyGenerated(rec);
		lastIn += rec;
		return rec > 0;
	}
	
	public boolean consumeEnergy(int req)
	{
		var canGet = this.fe.extractEnergy(req, true);
		var did = canGet == req && this.fe.extractEnergy(req, false) == canGet;
		if(did)
		{
			measurables.onEnergyConsumed(req);
			lastOut += req;
		}
		return did;
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
	public CompoundTag serializeNBT()
	{
		var nbt = new CompoundTag();
		nbt.put("FE", fe.serializeNBT());
		nbt.put("Items", batteryInventory.serializeNBT());
		return nbt;
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt)
	{
		fe.setEnergyStored(nbt.getInt("FE"));
		batteryInventory.deserializeNBT(nbt.getList("Items", Tag.TAG_COMPOUND));
	}
}