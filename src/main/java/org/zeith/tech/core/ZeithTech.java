package org.zeith.tech.core;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.*;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zeith.hammerlib.client.adapter.ChatMessageAdapter;
import org.zeith.hammerlib.core.adapter.LanguageAdapter;
import org.zeith.hammerlib.core.adapter.ModSourceAdapter;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.api.audio.IAudioSystem;
import org.zeith.tech.api.modules.IZeithTechModules;
import org.zeith.tech.api.recipes.IRecipeRegistries;
import org.zeith.tech.compat.BaseCompat;
import org.zeith.tech.compat.Compats;
import org.zeith.tech.core.audio.ClientAudioSystem;
import org.zeith.tech.core.audio.CommonAudioSystem;
import org.zeith.tech.core.mixins.TiersAccessor;
import org.zeith.tech.core.tabs.CreativeModeTabZT;
import org.zeith.tech.core.tabs.CreativeModeTabZTF;
import org.zeith.tech.modules.generators.init.AdvancementTriggersZT;
import org.zeith.tech.modules.shared.SharedModule;
import org.zeith.tech.modules.shared.init.TagsZT;
import org.zeith.tech.modules.transport.items.ItemFacade;
import org.zeith.tech.utils.LegacyEventBus;

import java.util.*;
import java.util.function.Consumer;

@Mod(ZeithTech.MOD_ID)
public class ZeithTech
		extends ZeithTechAPI
{
	private final ModulesImpl modules;
	private final RecipeRegistries registries;
	private final CommonAudioSystem audioSystem;
	
	public static final String MOD_ID = "zeithtech";
	
	public static final Logger LOG = LogManager.getLogger("ZeithTech");
	
	public static final CreativeModeTab TAB = new CreativeModeTabZT();
	public static final CreativeModeTab FACADES_TAB = new CreativeModeTabZTF();
	
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
		
		var illegalSourceNotice = ModSourceAdapter.getModSource(ZeithTech.class)
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
		
		compats.addAll(Compats.gatherAll());
		
		forCompats(c -> c.setup(busses));
	}
	
	private void setup(FMLCommonSetupEvent e)
	{
		this.modules.enable();
		AdvancementTriggersZT.setup();
		
		TiersAccessor netherite = Cast.cast(Tiers.NETHERITE);
		netherite.setUses(netherite.getUses() + 512);
		netherite.setEnchantmentValue(netherite.getEnchantmentValue() + 5);
		netherite.setDamage(netherite.getDamage() + 2F);
		netherite.setSpeed(netherite.getSpeed() + 3F);
		netherite.setLevel(5);
		
		TierSortingRegistry.registerTier(TUNGSTEN_TIER, ZeithTechAPI.id("tungsten"), List.of(Tiers.DIAMOND), List.of(Tiers.NETHERITE));
		
		LOG.info("Setup complete.");
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
	
	@Override
	public float getPartialTick()
	{
		return SharedModule.PROXY.getPartialTick();
	}
	
	public static final List<BaseCompat> compats = new ArrayList<>();
	
	public static void forCompats(Consumer<BaseCompat> handler)
	{
		compats.forEach(handler);
	}
	
	@Override
	public Optional<BlockState> getFacadeFromItem(ItemStack stack)
	{
		Optional<BlockState> state = Optional.empty();
		if(!stack.isEmpty() && stack.getItem() instanceof ItemFacade facade)
			state = Optional.ofNullable(facade.getFacadeBlockState(stack));
		
		if(state.isPresent()) return state;
		
		return compats
				.stream()
				.flatMap(c -> c.getFacadeFromItem(stack).stream())
				.findFirst();
	}
}