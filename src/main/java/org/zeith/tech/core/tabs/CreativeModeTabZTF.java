package org.zeith.tech.core.tabs;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.ForgeRegistries;
import org.zeith.hammerlib.api.items.CreativeTab;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.modules.shared.init.BlocksZT;
import org.zeith.tech.modules.shared.init.ItemsZT;

import java.util.ArrayList;
import java.util.List;

public class CreativeModeTabZTF
		extends CreativeTab
{
	private static List<ItemStack> subTypes = null;
	
	public CreativeModeTabZTF()
	{
		super(new ResourceLocation(ZeithTech.MOD_ID, "facades"),
				b -> b.icon(() -> ItemsZT.FACADE.forItemRaw(new ItemStack(BlocksZT.HEVEA_PLANKS), 1))
						.displayItems(CreativeModeTabZTF::fillItemList)
		);
	}
	
	public static void fillItemList(FeatureFlagSet features, CreativeModeTab.Output output, boolean hasPermisions)
	{
		if(subTypes != null)
		{
			output.acceptAll(subTypes);
			return;
		}
		
		subTypes = new ArrayList<>(1000);
		
		for(var b : ForgeRegistries.BLOCKS)
			try
			{
				var item = b.asItem();
				if(item != Items.AIR)
				{
					var facade = ItemsZT.FACADE.forItem(item.getDefaultInstance(), false);
					if(!facade.isEmpty()) subTypes.add(facade);
				}
			} catch(Throwable ignored)
			{
			}
		
		output.acceptAll(subTypes);
	}
}