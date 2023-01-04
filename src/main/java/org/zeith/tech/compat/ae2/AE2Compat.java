package org.zeith.tech.compat.ae2;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.zeith.hammerlib.compat.base.Ability;
import org.zeith.hammerlib.compat.base.BaseCompat;
import org.zeith.hammerlib.core.adapter.BlockEntityAdapter;
import org.zeith.hammerlib.core.adapter.TagAdapter;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.hammerlib.util.java.ReflectionUtil;
import org.zeith.tech.compat._base.BaseCompatZT;
import org.zeith.tech.compat._base.abils.IFacadeObtainer;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.modules.transport.init.TilesZT_Transport;
import org.zeith.tech.utils.LegacyEventBus;

import java.util.Optional;

@BaseCompat.LoadCompat(
		modid = "ae2",
		compatType = BaseCompatZT.class
)
public class AE2Compat
		extends BaseCompatZT
		implements IFacadeObtainer
{
	private final Class<?> P2PTunnelAttunement = ReflectionUtil.fetchClass("appeng.api.features.P2PTunnelAttunement");
	
	// AE2 changed the facade class path in 12.8.3 beta.
	private final Class<?> IFacadeItem = Cast.firstInstanceof(Class.class,
			ReflectionUtil.fetchClass("appeng.facade.IFacadeItem"),
			ReflectionUtil.fetchClass("appeng.api.implementations.items.IFacadeItem")
	).orElse(null);
	
	@Override
	public <R> Optional<R> getAbility(Ability<R> ability)
	{
		return ability.findIn(this);
	}
	
	@Override
	public void setup(LegacyEventBus bus)
	{
		ZeithTech.LOG.info("Activated AE2 compat for P2P tunnel attunement.");
		bus.addListener(FMLCommonSetupEvent.class, this::commonSetup);
	}
	
	private void commonSetup(FMLCommonSetupEvent e)
	{
		for(var blk : BlockEntityAdapter.getValidBlocks(TilesZT_Transport.ENERGY_WIRE))
			registerAttunementApi(blk, ForgeCapabilities.ENERGY);
		
		for(var blk : BlockEntityAdapter.getValidBlocks(TilesZT_Transport.FLUID_PIPE))
			registerAttunementApi(blk, ForgeCapabilities.FLUID_HANDLER);
		
		for(var blk : BlockEntityAdapter.getValidBlocks(TilesZT_Transport.ITEM_PIPE))
			registerAttunementApi(blk, ForgeCapabilities.ITEM_HANDLER);
	}
	
	public synchronized void registerAttunementApi(ItemLike tunnelPart, Capability<?> cap)
	{
		try
		{
			String tunnelType;
			
			if(cap == ForgeCapabilities.ITEM_HANDLER)
				tunnelType = "ITEM_TUNNEL";
			else if(cap == ForgeCapabilities.FLUID_HANDLER)
				tunnelType = "FLUID_TUNNEL";
			else if(cap == ForgeCapabilities.ENERGY)
				tunnelType = "ENERGY_TUNNEL";
			else
			{
				ZeithTech.LOG.warn("Unknown tunnel capability: " + cap.getName());
				return;
			}
			
			var item = P2PTunnelAttunement.getDeclaredField(tunnelType).get(null);
			
			TagKey<Item> tag = Cast.cast(P2PTunnelAttunement.getDeclaredMethod("getAttunementTag", ItemLike.class).invoke(null, item));
			
			TagAdapter.bind(tag, tunnelPart.asItem());
		} catch(ReflectiveOperationException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public Optional<BlockState> getFacadeFromItem(ItemStack stack)
	{
		if(!stack.isEmpty() && IFacadeItem != null && IFacadeItem.isInstance(stack.getItem()))
		{
			try
			{
				return Optional.ofNullable(BlockState.class.cast(IFacadeItem.getDeclaredMethod("getTextureBlockState", ItemStack.class).invoke(stack.getItem(), stack)));
			} catch(Throwable e)
			{
				e.printStackTrace();
			}
		}
		
		return Optional.empty();
	}
}