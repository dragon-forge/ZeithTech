package org.zeith.tech.modules.processing.blocks.farm.actions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.zeith.tech.api.misc.SoundConfiguration;

import java.util.Comparator;
import java.util.List;

public record TransformBlockAction(BlockPos pos, BlockState source, BlockState dest, List<ItemStack> drops, SoundConfiguration sound, int waterUsage, int priority)
		implements Comparable<TransformBlockAction>
{
	public static final Comparator<TransformBlockAction> COMPARATOR = Comparator.comparingInt(TransformBlockAction::priority)
			.thenComparing(a -> a.pos);
	
	public static final Codec<TransformBlockAction> CODEC = RecordCodecBuilder.create(instance ->
			instance.group(
					BlockPos.CODEC.fieldOf("pos").forGetter(TransformBlockAction::pos),
					BlockState.CODEC.fieldOf("source").forGetter(TransformBlockAction::source),
					BlockState.CODEC.fieldOf("dest").forGetter(TransformBlockAction::dest),
					ItemStack.CODEC.listOf().fieldOf("drops").forGetter(TransformBlockAction::drops),
					SoundConfiguration.CODEC.fieldOf("sound").forGetter(TransformBlockAction::sound),
					Codec.INT.fieldOf("water").forGetter(TransformBlockAction::waterUsage),
					Codec.INT.fieldOf("priority").forGetter(TransformBlockAction::priority)
			).apply(instance, TransformBlockAction::new)
	);
	
	@Override
	public int compareTo(@NotNull TransformBlockAction o)
	{
		return COMPARATOR.compare(this, o);
	}
	
	public List<ItemStack> copyDrops()
	{
		return drops.stream().map(ItemStack::copy).toList();
	}
}