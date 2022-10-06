package org.zeith.tech.core;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.zeith.hammerlib.HammerLib;
import org.zeith.hammerlib.client.adapter.ChatMessageAdapter;
import org.zeith.hammerlib.core.adapter.LanguageAdapter;
import org.zeith.hammerlib.core.adapter.ModSourceAdapter;
import org.zeith.hammerlib.event.recipe.BuildTagsEvent;
import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.api.audio.IAudioSystem;
import org.zeith.tech.api.modules.IZeithTechModules;
import org.zeith.tech.api.recipes.IRecipeRegistries;
import org.zeith.tech.compat.BaseCompat;
import org.zeith.tech.core.audio.ClientAudioSystem;
import org.zeith.tech.core.audio.CommonAudioSystem;
import org.zeith.tech.modules.processing.init.BlocksZT_Processing;
import org.zeith.tech.modules.processing.init.RecipeRegistriesZT_Processing;
import org.zeith.tech.modules.shared.init.TagsZT;
import org.zeith.tech.utils.LegacyEventBus;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Mod(ZeithTech.MOD_ID)
public class ZeithTech
		extends ZeithTechAPI
{
	static final Map<TagKey<Fluid>, Set<Fluid>> fluidTags = new ConcurrentHashMap<>();
	private final ModulesImpl modules;
	private final RecipeRegistries registries;
	private final CommonAudioSystem audioSystem;
	
	public static final String MOD_ID = "zeithtech";
	
	public static final Logger LOG = LogManager.getLogger("ZeithTech");
	
	public static final CreativeModeTab TAB = new CreativeModeTab(MOD_ID)
	{
		@Override
		public @NotNull ItemStack makeIcon()
		{
			return new ItemStack(BlocksZT_Processing.BASIC_FUEL_GENERATOR);
		}
	};
	
	public final LegacyEventBus busses;
	
	public ZeithTech()
	{
		TagsZT.init();
		LanguageAdapter.registerMod(MOD_ID);
		apply(this);
		ForgeMod.enableMilkFluid();
		
		var bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(this::setup);
		
		this.busses = new LegacyEventBus(bus, MinecraftForge.EVENT_BUS);
		
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
		
		this.modules = new ModulesImpl();
		this.modules.construct(this.busses);
		
		this.registries = new RecipeRegistries();
		this.audioSystem = DistExecutor.unsafeRunForDist(() -> ClientAudioSystem::new, () -> CommonAudioSystem::new);
		
		HammerLib.EVENT_BUS.addListener(this::applyTags);
	}
	
	private void setup(FMLCommonSetupEvent e)
	{
		this.modules.enable();
		RecipeRegistriesZT_Processing.setup(e);
	}
	
	public void applyTags(BuildTagsEvent evt)
	{
		if(evt.reg == ForgeRegistries.FLUIDS)
			fluidTags.forEach(evt::addAllToTag);
	}
	
	public static void bindStaticTag(TagKey<Fluid> tag, Fluid... fluids)
	{
		Set<Fluid> set = fluidTags.computeIfAbsent(tag, b -> new HashSet<>());
		set.addAll(List.of(fluids));
	}
	
	@Override
	public IZeithTechModules getModules()
	{
		return modules;
	}
	
	@Override
	public IRecipeRegistries getRecipeRegistries()
	{
		return registries;
	}
	
	@Override
	public IAudioSystem getAudioSystem()
	{
		return audioSystem;
	}
	
	@Override
	public CreativeModeTab getCreativeTab()
	{
		return TAB;
	}
	
	public static final List<BaseCompat> compats = new ArrayList<>();
	
	public static void forCompats(Consumer<BaseCompat> handler)
	{
		compats.forEach(handler);
	}
}