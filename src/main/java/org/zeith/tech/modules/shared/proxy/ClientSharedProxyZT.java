package org.zeith.tech.modules.shared.proxy;

import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.IEventBus;
import org.zeith.tech.api.misc.AuxiliaryPortDefinition;
import org.zeith.tech.api.tile.slots.SlotType;
import org.zeith.tech.modules.shared.client.resources.model.ModelAuxiliaryIOPort;
import org.zeith.tech.modules.shared.init.BlocksZT;

public class ClientSharedProxyZT
		extends CommonSharedProxyZT
{
	@Override
	public void subEvents(IEventBus modBus)
	{
		modBus.addListener(this::registerBlockColors);
		modBus.addListener(this::modelBake);
		modBus.addListener(this::textureStitch);
	}
	
	private void modelBake(ModelEvent.BakingCompleted e)
	{
		for(BlockState state : BlocksZT.AUXILIARY_IO_PORT.getStateDefinition().getPossibleStates())
			e.getModels().put(BlockModelShaper.stateToModelLocation(state),
					new ModelAuxiliaryIOPort(e.getModelManager()));
	}
	
	public void textureStitch(TextureStitchEvent.Pre e)
	{
		if(e.getAtlas().location().equals(InventoryMenu.BLOCK_ATLAS))
		{
			SlotType.types()
					.map(SlotType::getTextures)
					.flatMap(AuxiliaryPortDefinition::textures)
					.distinct()
					.forEach(e::addSprite);
		}
	}
	
	private void registerBlockColors(RegisterColorHandlersEvent.Block e)
	{
		e.register((state, blockAndTintGetter, pos, layer) -> blockAndTintGetter != null && pos != null && layer != 0 ? layer : 0xFFFFFFFF,
				BlocksZT.AUXILIARY_IO_PORT
		);
	}
}