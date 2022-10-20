package org.zeith.tech.core.tabs;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.modules.shared.init.ItemsZT;

public class CreativeModeTabZT
		extends CreativeModeTab
{
	public CreativeModeTabZT()
	{
		super(ZeithTech.MOD_ID);
	}
	
	@Override
	public @NotNull ItemStack makeIcon()
	{
		return new ItemStack(ItemsZT.IRON_HAMMER);
	}
}