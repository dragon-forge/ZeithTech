package org.zeith.tech.core.proxy;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import org.zeith.tech.api.item.tooltip.*;
import org.zeith.tech.core.client.renderer.tooltip.*;

import java.util.ArrayList;
import java.util.List;

public class ClientCoreProxyZT
		extends CommonCoreProxyZT
{
	private final List<ResourceLocation> REGISTERED_TEXTURES = new ArrayList<>();
	
	@Override
	public void construct(IEventBus modBus)
	{
		super.construct(modBus);
		modBus.addListener(this::registerClientTooltips);
		modBus.addListener(this::addExtraTextures);
	}
	
	private void registerClientTooltips(RegisterClientTooltipComponentFactoriesEvent e)
	{
		e.register(TooltipStack.class, ClientTooltipStack::new);
		e.register(TooltipEnergyBar.class, ClientTooltipEnergy::new);
		e.register(TooltipImage.class, ClientTooltipImage::new);
	}
	
	@Override
	public void registerItemSprite(ResourceLocation path)
	{
		if(!REGISTERED_TEXTURES.contains(path))
			REGISTERED_TEXTURES.add(path);
	}
	
	private void addExtraTextures(TextureStitchEvent.Pre e)
	{
		if(e.getAtlas().location().equals(InventoryMenu.BLOCK_ATLAS))
			REGISTERED_TEXTURES.forEach(e::addSprite);
	}
}