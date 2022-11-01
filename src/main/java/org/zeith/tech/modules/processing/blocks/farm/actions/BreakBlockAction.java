package org.zeith.tech.modules.processing.blocks.farm.actions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public record BreakBlockAction(BlockPos pos, int priority)
		implements Comparable<BreakBlockAction>
{
	public static final Comparator<BreakBlockAction> COMPARATOR = Comparator.comparingInt(BreakBlockAction::priority)
			.thenComparing(a -> a.pos);
	
	public static final Codec<BreakBlockAction> CODEC = RecordCodecBuilder.create(instance ->
			instance.group(
					BlockPos.CODEC.fieldOf("pos").forGetter(BreakBlockAction::pos),
					Codec.INT.fieldOf("priority").forGetter(BreakBlockAction::priority)
			).apply(instance, BreakBlockAction::new)
	);
	
	@Override
	public int compareTo(@NotNull BreakBlockAction o)
	{
		return COMPARATOR.compare(this, o);
	}
}