package org.zeith.tech.core.datagen;

import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.*;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.hammerlib.util.java.ReflectionUtil;
import org.zeith.tech.core.datagen.loot.LootTableGeneratorZT;
import org.zeith.tech.core.datagen.worldgen.ConfiguredFeaturesZT;
import org.zeith.tech.modules.world.worldgen.WorldPlacementsZT;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGeneratorsZT
{
	@SubscribeEvent
	public static void onGatherData(GatherDataEvent dataEvent)
	{
		onGatherData(dataEvent.getGenerator(), dataEvent.getExistingFileHelper(), dataEvent.getGenerator().getVanillaPack(true));
	}
	
	public static void onGatherData(DataGenerator generator, ExistingFileHelper existingFileHelper, DataGenerator.PackGenerator pack)
	{
		RegistryAccess.Frozen registryAccess = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
		CompletableFuture<HolderLookup.Provider> registries = createAppEngProvider(registryAccess);
		
		generator.addProvider(true, new AdvancementGeneratorZT(generator));
		generator.addProvider(true, new LootTableGeneratorZT(generator));
		
		pack.addProvider(bindRegistries(ConfiguredFeaturesZT::new, registries));
	}
	
	private static <T extends DataProvider> DataProvider.Factory<T> bindRegistries(BiFunction<PackOutput, CompletableFuture<HolderLookup.Provider>, T> factory, CompletableFuture<HolderLookup.Provider> factories)
	{
		return (packOutput) -> (T) factory.apply(packOutput, factories);
	}
	
	private static CompletableFuture<HolderLookup.Provider> createAppEngProvider(RegistryAccess registryAccess)
	{
		List<LookupExtension<?>> extensions = List.of(
				new LookupExtension<>(Registries.PLACED_FEATURE, WorldPlacementsZT::init)
		);
		
		RegistrySetBuilder builder = new RegistrySetBuilder();
		
		RegistrySetBuilder b = ReflectionUtil.<RegistrySetBuilder> getStaticFinalField(VanillaRegistries.class, "BUILDER").orElseThrow();
		
		for(var entry : b.entries)
			addEntry(builder,
					entry,
					extensions.stream().filter(e -> e.key() == entry.key()).findFirst().orElse(null)
			);
		
		return CompletableFuture.completedFuture(builder.build(registryAccess));
	}
	
	private static <T> void addEntry(RegistrySetBuilder builder, RegistrySetBuilder.RegistryStub<T> entry, @Nullable LookupExtension<?> extension)
	{
		LookupExtension<T> typedExtension = extension != null ? extension.tryCast(entry.key()) : null;
		if(typedExtension == null)
		{
			builder.add(entry.key(), entry.lifecycle(), entry.bootstrap());
		} else
		{
			builder.add(entry.key(), entry.lifecycle(), (bootstapContext) ->
			{
				entry.bootstrap().run(bootstapContext);
				typedExtension.extender().accept(bootstapContext);
			});
		}
	}
	
	record LookupExtension<T>(ResourceKey<Registry<T>> key, Consumer<BootstapContext<T>> extender)
	{
		LookupExtension(ResourceKey<Registry<T>> key, Consumer<BootstapContext<T>> extender)
		{
			this.key = key;
			this.extender = extender;
		}
		
		@Nullable
		public <U> LookupExtension<U> tryCast(ResourceKey<? extends Registry<U>> registryKey)
		{
			return Objects.equals(registryKey, this.key) ? Cast.cast(this) : null;
		}
	}
}