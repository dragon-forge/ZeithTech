package org.zeith.tech.modules.processing.blocks.waste_processor;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.blocks.ICustomBlockItem;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.core.client.renderer.item.BlockItemWithAltISTER;
import org.zeith.tech.modules.processing.blocks.base.machine.BlockBaseMachine;
import org.zeith.tech.modules.processing.init.TilesZT_Processing;

import java.util.function.Consumer;

public class BlockWasteProcessor
		extends BlockBaseMachine<TileWasteProcessor>
		implements ICustomBlockItem
{
	public BlockWasteProcessor()
	{
		super(TileWasteProcessor.class);
	}
	
	@Override
	public @Nullable TileWasteProcessor newBlockEntity(BlockPos pos, BlockState state)
	{
		return new TileWasteProcessor(pos, state);
	}
	
	@Override
	public BlockItem createBlockItem()
	{
		return new BlockItem(this, new Item.Properties().tab(ZeithTech.TAB))
		{
			@Override
			public void initializeClient(Consumer<IClientItemExtensions> consumer)
			{
				BlockItemWithAltISTER.INSTANCE
						.bind(BlockWasteProcessor.this, TilesZT_Processing.WASTE_PROCESSOR)
						.ifPresent(consumer);
			}
		};
	}
}
