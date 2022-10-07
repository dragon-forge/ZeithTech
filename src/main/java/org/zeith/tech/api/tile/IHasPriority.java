package org.zeith.tech.api.tile;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;

public interface IHasPriority
{
	int getPriorityForFace(Direction face, Capability<?> cap);
}