package org.zeith.tech.modules.transport.items;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import org.zeith.tech.api.block.IPipeCuttable;
import org.zeith.tech.api.utils.BlockUpdateEmitter;
import org.zeith.tech.modules.shared.BaseZT;

public class ItemPipeCutter
		extends Item
{
	public ItemPipeCutter()
	{
		super(BaseZT.itemProps().stacksTo(1));
	}
	
	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context)
	{
		var level = context.getLevel();
		var pos = context.getClickedPos();
		var state = level.getBlockState(pos);
		if(state.getBlock() instanceof IPipeCuttable cuttable && cuttable.performCut(state, context))
		{
			BlockUpdateEmitter.blockUpdated(context.getLevel(), context.getClickedPos());
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}
}