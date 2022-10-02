package org.zeith.tech;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zeith.hammerlib.HammerLib;
import org.zeith.hammerlib.client.adapter.ChatMessageAdapter;
import org.zeith.hammerlib.core.adapter.LanguageAdapter;
import org.zeith.hammerlib.core.adapter.ModSourceAdapter;
import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.api.modules.IZeithTechModules;
import org.zeith.tech.modules.ZeithTechModulesImpl;
import org.zeith.tech.modules.processing.init.BlocksZT_Processing;
import org.zeith.tech.modules.processing.init.RecipeRegistriesZT_Processing;
import org.zeith.tech.modules.shared.init.TagsZT;

@Mod(ZeithTech.MOD_ID)
public class ZeithTech
		extends ZeithTechAPI
{
	private final ZeithTechModulesImpl MODULES;
	
	public static final String MOD_ID = "zeithtech";
	
	public static final Logger LOG = LogManager.getLogger("ZeithTech");
	
	public static final CreativeModeTab TAB = new CreativeModeTab(MOD_ID)
	{
		@Override
		public ItemStack makeIcon()
		{
			return new ItemStack(BlocksZT_Processing.FUEL_GENERATOR_BASIC);
		}
	};
	
	public ZeithTech()
	{
		TagsZT.init();
		LanguageAdapter.registerMod(MOD_ID);
		apply(this);
		ForgeMod.enableMilkFluid();
		
		var bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(RecipeRegistriesZT_Processing::setup);
		
		var illegalSourceNotice = ModSourceAdapter.getModSource(HammerLib.class)
				.filter(ModSourceAdapter.ModSource::wasDownloadedIllegally)
				.orElse(null);
		
		if(illegalSourceNotice != null)
		{
			String officialUrl = "https://www.curseforge.com/minecraft/mc-mods/hammer-lib";
			
			LOG.fatal("====================================================");
			LOG.fatal("WARNING: ZeithTech was downloaded from " + illegalSourceNotice.referrerDomain() +
					", which has been marked as illegal site over at stopmodreposts.org.");
			LOG.fatal("Please download the mod from " + officialUrl);
			LOG.fatal("====================================================");
			
			var illegalUri = Component.literal(illegalSourceNotice.referrerDomain())
					.withStyle(s -> s.withColor(ChatFormatting.RED));
			var smrUri = Component.literal("stopmodreposts.org")
					.withStyle(s -> s.withColor(ChatFormatting.BLUE)
							.withUnderlined(true)
							.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://stopmodreposts.org/"))
							.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to open webpage."))));
			var curseforgeUri = Component.literal("curseforge.com")
					.withStyle(s -> s.withColor(ChatFormatting.BLUE)
							.withUnderlined(true)
							.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, officialUrl))
							.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to open webpage."))));
			ChatMessageAdapter.sendOnFirstWorldLoad(Component.literal("WARNING: ZeithTech was downloaded from ")
					.append(illegalUri)
					.append(", which has been marked as illegal site over at ")
					.append(smrUri)
					.append(". Please download the mod from ")
					.append(curseforgeUri)
					.append(".")
			);
		}
		
		this.MODULES = new ZeithTechModulesImpl();
		this.MODULES.enable();
	}
	
	@Override
	public IZeithTechModules getModules()
	{
		return MODULES;
	}
	
	@Override
	public CreativeModeTab getCreativeTab()
	{
		return TAB;
	}
}