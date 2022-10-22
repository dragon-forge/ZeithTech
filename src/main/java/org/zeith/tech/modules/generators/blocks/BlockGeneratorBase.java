package org.zeith.tech.modules.generators.blocks;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import org.jetbrains.annotations.Nullable;
import org.zeith.tech.modules.processing.blocks.base.machine.BlockBaseMachine;
import org.zeith.tech.modules.processing.blocks.base.machine.TileBaseMachine;

import java.util.List;

public abstract class BlockGeneratorBase<T extends TileBaseMachine<T>>
		extends BlockBaseMachine<T>
{
	protected int defaultGeneration;
	
	public BlockGeneratorBase(Class<T> tileType, Properties props)
	{
		super(tileType, props);
	}
	
	public BlockGeneratorBase(Class<T> tileType)
	{
		super(tileType);
	}
	
	public BlockGeneratorBase<T> withDefaultGeneration(int defaultGeneration)
	{
		this.defaultGeneration = defaultGeneration;
		return this;
	}
	
	public int getDefaultGeneration()
	{
		return defaultGeneration;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> components, TooltipFlag flags)
	{
		components.add(Component.translatable("info.zeithtech.fe.generation", Component.literal("%,d".formatted(defaultGeneration))).withStyle(ChatFormatting.GRAY));
		components.add(Component.translatable(getDescriptionId() + ".desc").withStyle(ChatFormatting.DARK_GRAY));
	}
}
