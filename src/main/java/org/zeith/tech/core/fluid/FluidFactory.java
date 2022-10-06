package org.zeith.tech.core.fluid;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.*;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.utils.LegacyEventBus;

import java.util.List;
import java.util.function.*;

public class FluidFactory
{
	public final ResourceLocation fluidId;
	
	public final FluidType type;
	public final Item bucket;
	
	public final ForgeFlowingFluid.Properties fluidProps;
	public final ForgeFlowingFluid.Source source;
	public final ForgeFlowingFluid.Flowing flowing;
	
	public final LiquidBlock block;
	
	public FluidFactory(ResourceLocation fluidId, Supplier<FluidType> typeGenerator, Function<Supplier<FlowingFluid>, Item> bucket, Consumer<ForgeFlowingFluid.Properties> propertyModifier)
	{
		this.fluidId = fluidId;
		this.type = typeGenerator.get();
		
		this.fluidProps = new ForgeFlowingFluid.Properties(this::getType, this::getSource, this::getFlowing).block(this::getBlock).bucket(this::getBucket);
		if(propertyModifier != null) propertyModifier.accept(this.fluidProps);
		
		this.source = new ForgeFlowingFluid.Source(this.fluidProps);
		this.flowing = new ForgeFlowingFluid.Flowing(this.fluidProps);
		
		this.bucket = bucket != null ? bucket.apply(this::getSource) : null;
		this.block = new LiquidBlock(this::getFlowing, BlockBehaviour.Properties.of(Material.WATER).noCollission().strength(100.0F).noLootTable());
	}
	
	public FluidFactory addFluidTag(TagKey<Fluid> tag)
	{
		ZeithTech.bindStaticTag(tag, this.source, this.flowing);
		return this;
	}
	
	public FluidFactory addFluidTags(List<TagKey<Fluid>> fluidTags)
	{
		for(var tag : fluidTags)
			ZeithTech.bindStaticTag(tag, this.source, this.flowing);
		return this;
	}
	
	public static FluidFactory createWithBucket(ResourceLocation fluidId, Supplier<FluidType> typeGenerator, Consumer<ForgeFlowingFluid.Properties> propertyModifier)
	{
		return new FluidFactory(fluidId, typeGenerator, fluid -> new BucketItem(fluid, (new Item.Properties()).craftRemainder(Items.BUCKET).stacksTo(1).tab(ZeithTech.TAB)), propertyModifier);
	}
	
	public static FluidFactory createWithBucket(ResourceLocation fluidId, Supplier<FluidType> typeGenerator)
	{
		return new FluidFactory(fluidId, typeGenerator, fluid -> new BucketItem(fluid, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(ZeithTech.TAB)), null);
	}
	
	public static FluidFactory createNoBucket(ResourceLocation fluidId, Supplier<FluidType> typeGenerator, Consumer<ForgeFlowingFluid.Properties> propertyModifier)
	{
		return new FluidFactory(fluidId, typeGenerator, null, propertyModifier);
	}
	
	public static FluidFactory createNoBucket(ResourceLocation fluidId, Supplier<FluidType> typeGenerator)
	{
		return new FluidFactory(fluidId, typeGenerator, null, null);
	}
	
	public FluidType getType()
	{
		return type;
	}
	
	public ForgeFlowingFluid.Source getSource()
	{
		return source;
	}
	
	public BlockState getSourceBlockState()
	{
		return source.getSource(false).createLegacyBlock();
	}
	
	public ForgeFlowingFluid.Flowing getFlowing()
	{
		return flowing;
	}
	
	public Item getBucket()
	{
		return bucket;
	}
	
	public LiquidBlock getBlock()
	{
		return block;
	}
	
	public ResourceLocation subId(String thing)
	{
		return new ResourceLocation(fluidId.getNamespace(), fluidId.getPath() + "_" + thing);
	}
	
	public void register(LegacyEventBus modBus)
	{
		modBus.addListener(RegisterEvent.class, e ->
		{
			e.register(ForgeRegistries.Keys.FLUID_TYPES, fluidId, () -> type);
			e.register(ForgeRegistries.Keys.FLUIDS, subId("still"), () -> source);
			e.register(ForgeRegistries.Keys.FLUIDS, subId("flowing"), () -> flowing);
			e.register(ForgeRegistries.Keys.ITEMS, subId("bucket"), () -> bucket);
			e.register(ForgeRegistries.Keys.BLOCKS, fluidId, () -> block);
		});
	}
	
	public boolean is(Fluid fluid)
	{
		return fluid == source;
	}
}