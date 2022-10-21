package org.zeith.tech.api.item.multitool;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import org.zeith.tech.api.item.IAccumulatorItem;

import java.util.Optional;
import java.util.stream.Stream;

public interface IMultiToolItem
		extends IMultiToolPart
{
	ResourceLocation getMultiToolBaseModel(ItemStack multiToolStack);
	
	boolean setMotor(ItemStack multiToolStack, ItemStack motorStack);
	
	boolean setHead(ItemStack multiToolStack, ItemStack headStack);
	
	boolean setAccumulator(ItemStack multiToolStack, ItemStack accumStack);
	
	Optional<Tuple<IMultiToolMotor, ItemStack>> getMotor(ItemStack multiToolStack);
	
	Optional<Tuple<IMultiToolHead, ItemStack>> getHead(ItemStack multiToolStack);
	
	Optional<Tuple<IAccumulatorItem, ItemStack>> getAccumulator(ItemStack multiToolStack);
	
	default Stream<Tuple<? extends IMultiToolPart, ItemStack>> getParts(ItemStack multiToolStack, boolean includeSelf)
	{
		return Stream.concat(
				(includeSelf ? Optional.of(new Tuple<>(this, multiToolStack)) : Optional.<Tuple<IMultiToolPart, ItemStack>> empty()).stream(),
				Stream.concat(
						getMotor(multiToolStack).stream(),
						getHead(multiToolStack).stream()
				)
		);
	}
	
	@Override
	default ResourceLocation getMultiToolPartModel(ItemStack partStack, ItemStack multiToolStack)
	{
		return getMultiToolBaseModel(multiToolStack);
	}
}