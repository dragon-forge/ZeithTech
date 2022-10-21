package org.zeith.tech.modules.shared.items;

import net.minecraft.world.item.Item;
import net.minecraftforge.common.Tags;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.tech.modules.shared.BaseZT;
import org.zeith.tech.modules.shared.init.TagsZT;

@SimplyRegister(prefix = "dusts/")
public interface DustsZT
{
	@RegistryName("coal")
	Item COAL_DUST = BaseZT.newItem(TagsZT.Items.DUSTS_COAL);
	
	@RegistryName("iron")
	Item IRON_DUST = BaseZT.newItem(TagsZT.Items.DUSTS_IRON);
	
	@RegistryName("bioluminescent")
	Item BIOLUMINESCENT_DUST = BaseZT.newItem(Tags.Items.DUSTS_GLOWSTONE);
}