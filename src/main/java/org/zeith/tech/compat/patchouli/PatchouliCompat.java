package org.zeith.tech.compat.patchouli;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import org.zeith.tech.api.block.multiblock.base.MultiBlockRegistry;
import org.zeith.tech.compat.BaseCompat;
import org.zeith.tech.compat.patchouli.client.ClientPatchouliCompat;
import org.zeith.tech.utils.LegacyEventBus;
import vazkii.patchouli.api.PatchouliAPI;

public class PatchouliCompat
		extends BaseCompat
{
	@Override
	public void setup(LegacyEventBus bus)
	{
		bus.addListener(FMLLoadCompleteEvent.class, this::loadComplete);
		
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> new ClientPatchouliCompat()::run);
	}
	
	private void loadComplete(FMLLoadCompleteEvent e)
	{
		MultiBlockRegistry.keys().forEach(this::registerMultiBlock);
	}
	
	private void registerMultiBlock(ResourceLocation id)
	{
		var mb = MultiBlockRegistry.get(id);
		if(mb != null)
			PatchouliAPI.get()
					.registerMultiblock(id, PatchouliStateMatcher.convertMultiblock(mb));
	}
}