package org.zeith.tech.modules.shared.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.tech.api.item.multitool.IMultiToolItem;
import org.zeith.tech.api.item.tooltip.TooltipEnergyBar;
import org.zeith.tech.api.item.tooltip.TooltipStack;
import org.zeith.tech.modules.shared.client.renderer.tooltip.ClientTooltipEnergy;
import org.zeith.tech.modules.shared.client.renderer.tooltip.ClientTooltipStack;
import org.zeith.tech.modules.shared.client.resources.model.ModelMultiTool;
import org.zeith.tech.modules.shared.init.BlocksZT;
import org.zeith.tech.modules.shared.items.multitool.ContainerMultiTool;
import org.zeith.tech.modules.shared.items.multitool.GuiMultiTool;

public class ClientSharedProxyZT
		extends CommonSharedProxyZT
{
	public static final HumanoidModel.ArmPose MULTI_TOOL_ARM_POSE = HumanoidModel.ArmPose.create("holding_zeithtech_multi_tool", true, (model, entity, arm) ->
	{
		entity.swinging = false;
		entity.swingTime = 0;
		
		model.rightArm.xRot = Mth.DEG_TO_RAD * 270;
		model.leftArm.xRot = Mth.DEG_TO_RAD * 270;
		
		if(arm == HumanoidArm.RIGHT)
		{
			model.rightArm.yRot -= Mth.DEG_TO_RAD * 20;
			model.leftArm.yRot += Mth.DEG_TO_RAD * 25;
		} else
		{
			model.rightArm.yRot -= Mth.DEG_TO_RAD * 25;
			model.leftArm.yRot += Mth.DEG_TO_RAD * 20;
		}
	});
	
	@Override
	public void subEvents(IEventBus modBus)
	{
		modBus.addListener(this::registerBlockColors);
		modBus.addListener(this::registerGeometryLoaders);
		modBus.addListener(this::registerClientTooltips);
		modBus.addListener(this::clientSetup);
	}
	
	private void clientSetup(FMLClientSetupEvent e)
	{
		MenuScreens.register(ContainerMultiTool.MULTI_TOOL, GuiMultiTool::new);
		
		ForgeRegistries.ITEMS.getValues()
				.stream()
				.filter(IMultiToolItem.class::isInstance)
				.forEach(item -> registerMultiToolProperty(Cast.cast(item)));
	}
	
	private <T extends Item & IMultiToolItem> void registerMultiToolProperty(T item)
	{
		ItemProperties.register(item,
				new ResourceLocation("empty"), (ClampedItemPropertyFunction) (stack, level, entity, layer) -> item.shouldRender2D(stack) ? 1 : 0
		);
	}
	
	@Override
	public float getPartialTick()
	{
		return Minecraft.getInstance().getPartialTick();
	}
	
	private void registerBlockColors(RegisterColorHandlersEvent.Block e)
	{
		e.register((state, blockAndTintGetter, pos, layer) -> blockAndTintGetter != null && pos != null && layer != 0 ? layer : 0xFFFFFFFF,
				BlocksZT.AUXILIARY_IO_PORT
		);
	}
	
	private void registerGeometryLoaders(ModelEvent.RegisterGeometryLoaders e)
	{
		e.register("item/multi_tool", new ModelMultiTool.Loader());
	}
	
	private void registerClientTooltips(RegisterClientTooltipComponentFactoriesEvent e)
	{
		e.register(TooltipStack.class, ClientTooltipStack::new);
		e.register(TooltipEnergyBar.class, ClientTooltipEnergy::new);
	}
}