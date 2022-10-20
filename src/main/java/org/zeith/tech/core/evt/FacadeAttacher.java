package org.zeith.tech.core.evt;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.zeith.tech.api.ZeithTechCapabilities;

@Mod.EventBusSubscriber
public class FacadeAttacher
{
	@SubscribeEvent
	public static void rightClickWithFacade(PlayerInteractEvent.RightClickBlock e)
	{
		var be = e.getLevel().getBlockEntity(e.getPos());
		if(be != null)
		{
			be.getCapability(ZeithTechCapabilities.FACADES).ifPresent(facades ->
			{
				var res = facades.placeFacade(new UseOnContext(e.getLevel(), e.getEntity(), e.getHand(), e.getItemStack(), e.getHitVec()));
				if(res != InteractionResult.PASS)
				{
					e.setCancellationResult(res);
					e.setCanceled(true);
				}
			});
		}
	}
}