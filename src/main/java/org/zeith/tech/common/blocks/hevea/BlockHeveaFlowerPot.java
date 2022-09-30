package org.zeith.tech.common.blocks.hevea;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerPotBlock;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.blocks.INoItemBlock;
import org.zeith.hammerlib.core.adapter.TagAdapter;

import java.util.function.Supplier;

public class BlockHeveaFlowerPot
		extends FlowerPotBlock
		implements INoItemBlock
{
	public BlockHeveaFlowerPot(@Nullable Supplier<FlowerPotBlock> emptyPot, Supplier<? extends Block> p_53528_, Properties properties)
	{
		super(emptyPot, p_53528_, properties);
		TagAdapter.bindStaticTag(BlockTags.FLOWER_POTS, this);
	}
}
