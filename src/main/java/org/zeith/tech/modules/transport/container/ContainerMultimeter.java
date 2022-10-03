package org.zeith.tech.modules.transport.container;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeMenuType;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.hammerlib.api.inv.ComplexProgressHandler;
import org.zeith.hammerlib.api.inv.IScreenContainer;
import org.zeith.hammerlib.client.screen.MenuWithProgressBars;
import org.zeith.hammerlib.net.properties.*;
import org.zeith.tech.api.tile.energy.IEnergyMeasurable;
import org.zeith.tech.modules.transport.client.gui.GuiMultimeter;

import java.util.List;

@SimplyRegister
public class ContainerMultimeter
		extends MenuWithProgressBars
		implements IScreenContainer
{
	@RegistryName("multimeter")
	public static final MenuType<ContainerMultimeter> MULTIMETER_MT = IForgeMenuType.create((windowId, playerInv, extraData) ->
	{
		var lvl = playerInv.player.getLevel();
		var measurable = IEnergyMeasurable.get(lvl, extraData.readBlockPos())
				.resolve()
				.orElse(null);
		return new ContainerMultimeter(windowId, playerInv, new MultimeterData(measurable, lvl.isClientSide));
	});
	
	public final MultimeterData source;
	public final double[][] graphs = new double[3][100];
	public int age = 0;
	
	public ContainerMultimeter(int windowId, Inventory playerInv, MultimeterData measurable)
	{
		super(MULTIMETER_MT, windowId, ComplexProgressHandler.withProperties(measurable.getProperties()));
		this.source = measurable;
		
		int x;
		for(x = 0; x < 3; ++x)
			for(int y = 0; y < 9; ++y)
				this.addSlot(new Slot(playerInv, y + x * 9 + 9, 8 + y * 18, 84 + x * 18));
		
		for(x = 0; x < 9; ++x)
			this.addSlot(new Slot(playerInv, x, 8 + x * 18, 142));
	}
	
	@Override
	public void containerTick()
	{
		super.containerTick();
		
		if(source.hasData.getBoolean())
		{
			++age;
			
			for(int i = 0; i < graphs.length; i++)
			{
				double[] graph = graphs[i];
				
				System.arraycopy(graph, 0, graph, 1, graph.length - 1);
				graph[0] = source.getValue(i);
			}
		}
	}
	
	@Override
	public boolean stillValid(Player player)
	{
		return source.isUsable(player);
	}
	
	@Override
	public ItemStack quickMoveStack(Player player, int slot)
	{
		return ItemStack.EMPTY;
	}
	
	@Override
	public void removed(Player p_38940_)
	{
		source.isAlive = false;
		super.removed(p_38940_);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public Screen openScreen(Inventory inv, Component label)
	{
		return new GuiMultimeter(this, inv, label);
	}
	
	public static class MultimeterData
	{
		public final IEnergyMeasurable measurable;
		
		public boolean isAlive = true;
		
		public final PropertyFloat
				generation = new PropertyFloat(),
				consumption = new PropertyFloat(),
				transfer = new PropertyFloat();
		
		public final PropertyBool hasData = new PropertyBool();
		
		public MultimeterData(IEnergyMeasurable measurable, boolean isClientSide)
		{
			this.measurable = measurable;
			if(!isClientSide)
				measurable.addListener(new IEnergyMeasurable.EnergyMeasureListener(
								generation::setFloat,
								transfer::setFloat,
								consumption::setFloat,
								() ->
								{
									hasData.set(true);
									return isAlive;
								}
						)
				);
		}
		
		public List<IProperty<?>> getProperties()
		{
			return List.of(generation, consumption, transfer, hasData);
		}
		
		public float getValue(int idx)
		{
			return switch(idx)
					{
						default -> generation.getFloat();
						case 1 -> consumption.getFloat();
						case 2 -> transfer.getFloat();
						case 3 -> Math.abs(consumption.getFloat() - transfer.getFloat());
					};
		}
		
		public boolean isUsable(Player player)
		{
			BlockPos pos;
			return measurable != null
					&& (pos = measurable.getMeasurablePosition()) != null
					&& player.distanceToSqr(Vec3.atCenterOf(pos)) < 64;
		}
	}
}
