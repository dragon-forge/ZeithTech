package org.zeith.tech.api.utils;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.IReverseTag;
import org.zeith.tech.utils.fluid.FluidHelper;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public record FluidIngredient(CompareMode mode, List<FluidStack> asFluidStack, List<TagKey<Fluid>> asTags, int fluidAmount)
		implements Predicate<FluidStack>
{
	public static final Codec<FluidIngredient> CODEC = RecordCodecBuilder.create(instance ->
			instance.group(
					CompareMode.CODEC.fieldOf("mode").forGetter(FluidIngredient::mode),
					FluidStack.CODEC.listOf().fieldOf("fluids").forGetter(FluidIngredient::asFluidStack),
					TagKey.codec(ForgeRegistries.Keys.FLUIDS).listOf().fieldOf("tags").forGetter(FluidIngredient::asTags),
					Codec.INT.fieldOf("amount").forGetter(FluidIngredient::fluidAmount)
			).apply(instance, FluidIngredient::new)
	);
	
	public static FluidIngredient fromJson(JsonElement json)
	{
		return JsonOps.INSTANCE.withDecoder(CODEC).apply(json).result().orElseThrow().getFirst();
	}
	
	public static FluidIngredient EMPTY = new FluidIngredient(CompareMode.VALUES, List.of(), List.of(), 0);
	
	public static FluidIngredient ofTags(int amount, List<TagKey<Fluid>> tags)
	{
		return new FluidIngredient(CompareMode.TAGS, List.of(), tags, amount).resolve();
	}
	
	public static FluidIngredient ofFluids(int amount, List<FluidStack> fluids)
	{
		return new FluidIngredient(CompareMode.TAGS, fluids, List.of(), amount).resolve();
	}
	
	public FluidIngredient(CompareMode mode, List<FluidStack> asFluidStack, List<TagKey<Fluid>> asTags, int fluidAmount)
	{
		this.mode = mode;
		this.asFluidStack = asFluidStack.stream().map(fs -> FluidHelper.withAmount(fs, 1)).toList();
		this.asTags = asTags;
		this.fluidAmount = fluidAmount;
	}
	
	public JsonElement toJson()
	{
		return JsonOps.INSTANCE.withEncoder(CODEC).apply(this).result().orElseThrow();
	}
	
	private FluidIngredient resolve()
	{
		return isEmpty() ? EMPTY : this;
	}
	
	public boolean isEmpty()
	{
		return this == EMPTY || fluidAmount <= 0 || (asFluidStack.isEmpty() && asTags().isEmpty());
	}
	
	public boolean fluidsMatch(FluidStack fluidStack)
	{
		if(isEmpty())
			return fluidStack.isEmpty();
		
		return switch(mode)
				{
					case VALUES -> asFluidStack.stream().anyMatch(fluidStack::isFluidEqual);
					case TAGS -> ForgeRegistries.FLUIDS.tags().getReverseTag(fluidStack.getFluid())
							.stream()
							.flatMap(IReverseTag::getTagKeys)
							.anyMatch(asTags::contains);
				};
	}
	
	@Override
	public boolean test(FluidStack fluidStack)
	{
		if(fluidStack.getAmount() < fluidAmount)
			return false;
		return fluidsMatch(fluidStack);
	}
	
	public FluidStack[] getValues()
	{
		return switch(mode)
				{
					case TAGS -> Optional.ofNullable(ForgeRegistries.FLUIDS.tags())
							.stream()
							.flatMap(manager -> asTags.stream().map(manager::getTag))
							.flatMap(tag -> tag.stream().map(f -> new FluidStack(f, fluidAmount)))
							.toArray(FluidStack[]::new);
					case VALUES -> asFluidStack.stream()
							.map(fs -> FluidHelper.withAmount(fs, fluidAmount))
							.toArray(FluidStack[]::new);
				};
	}
	
	public enum CompareMode
	{
		TAGS,
		VALUES;
		
		public static final Codec<CompareMode> CODEC = RecordCodecBuilder.create(
				instance -> instance.group(
						Codec.STRING.fieldOf("type").forGetter(CompareMode::name)
				).apply(instance, CompareMode::valueOf)
		);
	}
}