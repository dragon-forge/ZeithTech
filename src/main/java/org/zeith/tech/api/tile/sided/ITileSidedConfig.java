package org.zeith.tech.api.tile.sided;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import org.zeith.tech.api.enums.*;

@AutoRegisterCapability
public interface ITileSidedConfig
{
	ISpecificSidedConfig getSideConfigs(SidedConfigTyped type);
	
	default boolean canAccess(SidedConfigTyped type, Direction direction)
	{
		var cfg = getSideConfigs(type);
		return cfg != null && cfg.getAbsolute(direction) != SideConfig.DISABLE;
	}
	
	interface ISpecificSidedConfig
	{
		ISpecificSidedConfig setDefaults(SideConfig config);
		
		SideConfig getRelative(RelativeDirection direction);
		
		void setRelative(RelativeDirection direction, SideConfig config);
		
		SideConfig getAbsolute(Direction direction);
		
		void setAbsolute(Direction direction, SideConfig config);
	}
}