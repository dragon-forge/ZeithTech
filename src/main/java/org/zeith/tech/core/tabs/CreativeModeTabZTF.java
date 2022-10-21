package org.zeith.tech.core.tabs;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.modules.shared.init.BlocksZT;
import org.zeith.tech.modules.shared.init.ItemsZT;

import java.util.ArrayList;
import java.util.List;

public class CreativeModeTabZTF
		extends CreativeModeTab
{
	private List<ItemStack> subTypes = null;
	
	public CreativeModeTabZTF()
	{
		super(ZeithTech.MOD_ID + ".facades");
	}
	
	@Override
	public @NotNull ItemStack makeIcon()
	{
		return ItemsZT.FACADE.forItemRaw(new ItemStack(BlocksZT.HEVEA_PLANKS), 1);
	}
	
	@Override
	public void fillItemList(NonNullList<ItemStack> items)
	{
		if(this.subTypes != null)
		{
			items.addAll(subTypes);
			return;
		}
		
		this.subTypes = new ArrayList<>(1000);
		
		for(var b : ForgeRegistries.BLOCKS)
			try
			{
				var item = b.asItem();
				if(item != Items.AIR && item.getItemCategory() != null)
				{
					NonNullList<ItemStack> tmpList = NonNullList.create();
					b.fillItemCategory(item.getItemCategory(), tmpList);
					for(var l : tmpList)
					{
						var facade = ItemsZT.FACADE.forItem(l, false);
						if(!facade.isEmpty()) this.subTypes.add(facade);
					}
				}
			} catch(Throwable ignored)
			{
			}
		
		items.addAll(subTypes);
	}
}