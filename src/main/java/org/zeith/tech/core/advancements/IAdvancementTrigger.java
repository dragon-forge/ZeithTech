package org.zeith.tech.core.advancements;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

@FunctionalInterface
public interface IAdvancementTrigger
{
	default void trigger(Player player)
	{
		if(player instanceof ServerPlayer sp)
			trigger(sp);
	}
	
	void trigger(ServerPlayer player);
}