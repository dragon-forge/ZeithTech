package org.zeith.tech.core.datagen.worldgen;

import com.google.gson.JsonElement;
import com.mojang.serialization.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.*;
import net.minecraft.resources.*;
import org.zeith.tech.core.ZeithTech;

import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class ConfiguredFeaturesZT
		implements DataProvider
{
	private final PackOutput output;
	private final CompletableFuture<HolderLookup.Provider> registries;
	
	public ConfiguredFeaturesZT(PackOutput output, CompletableFuture<HolderLookup.Provider> registries)
	{
		this.output = output;
		this.registries = registries;
	}
	
	@Override
	public CompletableFuture<?> run(CachedOutput writer)
	{
		return this.registries.thenComposeAsync((provider) ->
		{
			RegistryOps<JsonElement> dynamicOps = RegistryOps.create(JsonOps.INSTANCE, provider);
			
			CompletableFuture<?>[] futures = RegistryDataLoader.WORLDGEN_REGISTRIES.stream().map((info) ->
					this.writeRegistryEntries(writer, provider, dynamicOps, info)
			).toArray(CompletableFuture[]::new);
			
			return CompletableFuture.allOf(futures);
		});
	}
	
	private <T> CompletableFuture<Void> writeRegistryEntries(CachedOutput writer, HolderLookup.Provider provider, DynamicOps<JsonElement> ops, RegistryDataLoader.RegistryData<T> registryData)
	{
		ResourceKey<? extends Registry<T>> registryKey = registryData.key();
		HolderLookup.RegistryLookup<T> registry = provider.lookupOrThrow(registryKey);
		
		PackOutput.PathProvider pathResolver = this.output.createPathProvider(PackOutput.Target.DATA_PACK,
				registryKey.location().getPath()
		);
		
		CompletableFuture<?>[] futures = registry.listElements().flatMap((regEntry) ->
		{
			ResourceKey<T> key = regEntry.key();
			if(!key.location().getNamespace().equals(ZeithTech.MOD_ID))
			{
				return Stream.empty();
			} else
			{
				Path path = pathResolver.json(key.location());
				return writeToPath(path, writer, ops, registryData.elementCodec(), regEntry.value()).stream();
			}
		}).toArray(CompletableFuture[]::new);
		return CompletableFuture.allOf(futures);
	}
	
	private static <E> Optional<CompletableFuture<?>> writeToPath(Path path, CachedOutput cache, DynamicOps<JsonElement> json, Encoder<E> encoder, E value)
	{
		Optional<JsonElement> optional = encoder.encodeStart(json, value).resultOrPartial((error) ->
		{
			LOGGER.error("Couldn't serialize element {}: {}", path, error);
		});
		return optional.map((data) ->
		{
			return DataProvider.saveStable(cache, data, path);
		});
	}
	
	@Override
	public String getName()
	{
		return "ZeithTech ConfiguredFeatures";
	}
}