package org.zeith.tech.modules.processing.blocks.farm.actions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Objects;

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
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		BreakBlockAction that = (BreakBlockAction) o;
		return Objects.equals(pos, that.pos);
	}
}