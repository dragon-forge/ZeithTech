package org.zeith.tech.proxy;

import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.FoliageColor;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.zeith.tech.client.renderer.entity.BoatZTRenderer;
import org.zeith.tech.common.entity.BoatZT;
import org.zeith.tech.common.entity.ChestBoatZT;
import org.zeith.tech.init.BlocksZT;

import java.util.function.Supplier;

public class ClientProxy
		extends CommonProxy
{
	@Override
	public void subEvents(IEventBus modBus)
	{
		modBus.addListener(this::registerBlockColors);
		modBus.addListener(this::registerItemColors);
		modBus.addListener(this::clientSetup);
		modBus.addListener(this::registerLayerDefinition);
	}
	
	private void clientSetup(FMLClientSetupEvent e)
	{
		EntityRenderers.register(BoatZT.BOAT, BoatZTRenderer.make(false));
		EntityRenderers.register(ChestBoatZT.CHEST_BOAT, BoatZTRenderer.make(true));
		Sheets.addWoodType(BlocksZT.HEVEA);
	}
	
	private void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions e)
	{
		Supplier<LayerDefinition> boathNoChest = () -> BoatModel.createBodyModel(false);
		Supplier<LayerDefinition> boathWithChest = () -> BoatModel.createBodyModel(true);
		
		for(BoatZT.Type type : BoatZT.Type.values())
		{
			e.registerLayerDefinition(BoatZTRenderer.createBoatModelName(type), boathNoChest);
			e.registerLayerDefinition(BoatZTRenderer.createChestBoatModelName(type), boathWithChest);
		}
	}
	
	private void registerBlockColors(RegisterColorHandlersEvent.Block e)
	{
		e.register((state, blockAndTintGetter, pos, layer) -> blockAndTintGetter != null && pos != null ? BiomeColors.getAverageFoliageColor(blockAndTintGetter, pos) : FoliageColor.getDefaultColor(),
				BlocksZT.HEVEA_LEAVES
		);
	}
	
	private void registerItemColors(RegisterColorHandlersEvent.Item e)
	{
		e.register((stack, i) -> e.getBlockColors().getColor(((BlockItem) stack.getItem()).getBlock().defaultBlockState(), null, null, i),
				BlocksZT.HEVEA_LEAVES
		);
	}
}