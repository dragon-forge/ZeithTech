package org.zeith.tech.modules.world.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.blocks.ICustomBlockItem;
import org.zeith.hammerlib.api.fml.IRegisterListener;
import org.zeith.hammerlib.core.adapter.TagAdapter;
import org.zeith.tech.api.block.IToolModifyHandler;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.modules.world.init.BlocksZT_World;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class BlockHeveaLog
		extends RotatedPillarBlock
		implements IRegisterListener, ICustomBlockItem
{
	public static class Leaking
			extends BlockHeveaLog
	{
		public Leaking(Properties props)
		{
			super(props);
			registerDefaultState(defaultBlockState().setValue(LEAKING, false));
		}
	}
	
	public static final BooleanProperty DISLOCATED = BooleanProperty.create("dislocated");
	public static final BooleanProperty LEAKING = BooleanProperty.create("leaking");
	
	public static Supplier<IToolModifyHandler> HEVEA_LOG_STRIPPING = () -> ctx ->
	{
		if(ToolActions.AXE_STRIP == ctx.toolAction())
			return BlocksZT_World.STRIPPED_HEVEA_LOG.defaultBlockState()
					.setValue(DISLOCATED, ctx.state().getValue(DISLOCATED))
					.setValue(AXIS, ctx.state().getValue(AXIS));
		return null;
	};
	public static Supplier<IToolModifyHandler> HEVEA_WOOD_STRIPPING = () -> ctx ->
	{
		if(ToolActions.AXE_STRIP == ctx.toolAction())
			return BlocksZT_World.STRIPPED_HEVEA_WOOD.defaultBlockState()
					.setValue(DISLOCATED, ctx.state().getValue(DISLOCATED))
					.setValue(AXIS, ctx.state().getValue(AXIS));
		return null;
	};
	
	private final List<TagKey<Item>> itemTags = new ArrayList<>();
	protected boolean dropsSelf = false;
	protected final boolean hasLeaking = this instanceof Leaking;
	protected IToolModifyHandler toolModify = IToolModifyHandler.NONE;
	
	public BlockHeveaLog(BlockBehaviour.Properties props)
	{
		super(props);
		registerDefaultState(defaultBlockState().setValue(DISLOCATED, false));
	}
	
	@Override
	public boolean isRandomlyTicking(BlockState state)
	{
		return hasLeaking && !state.getValue(DISLOCATED) && !state.getValue(LEAKING);
	}
	
	@Override
	public void tick(BlockState state, ServerLevel lvl, BlockPos pos, RandomSource rng)
	{
		if(isRandomlyTicking(state) && rng.nextInt(33) == 0)
		{
			lvl.setBlockAndUpdate(pos, state.setValue(LEAKING, true));
		}
	}
	
	@Override
	public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate)
	{
		ItemStack itemStack = context.getItemInHand();
		if(!itemStack.canPerformAction(toolAction))
			return null;
		return toolModify.getToolModifiedState(new IToolModifyHandler.ToolModifyContext(state, context, toolAction, simulate));
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		super.createBlockStateDefinition(builder);
		if(this instanceof Leaking) builder.add(LEAKING);
		builder.add(DISLOCATED);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx)
	{
		return super.getStateForPlacement(ctx).setValue(DISLOCATED, true);
	}
	
	public BlockHeveaLog toolModify(IToolModifyHandler toolModify)
	{
		this.toolModify = toolModify;
		return this;
	}
	
	public BlockHeveaLog dropsSelf()
	{
		this.dropsSelf = true;
		return this;
	}
	
	public BlockHeveaLog addItemTags(List<TagKey<Item>> tags)
	{
		for(var tag : tags)
		{
			if(itemBlock != null)
				TagAdapter.bind(tag, itemBlock);
			itemTags.add(tag);
		}
		return this;
	}
	
	public BlockHeveaLog addBlockTags(List<TagKey<Block>> tags)
	{
		for(var tag : tags)
			TagAdapter.bind(tag, this);
		return this;
	}
	
	private BlockItem itemBlock;
	
	@Override
	public BlockItem createBlockItem()
	{
		var props = new Item.Properties();
		var gen = new BlockItem(this, props);
		itemBlock = gen;
		for(var tag : itemTags)
			TagAdapter.bind(tag, gen);
		return ZeithTech.TAB.add(gen);
	}
	
	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
	{
		if(dropsSelf)
			return List.of(new ItemStack(this));
		else
			return super.getDrops(state, builder);
	}
}