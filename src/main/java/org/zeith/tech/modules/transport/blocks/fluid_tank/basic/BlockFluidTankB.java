package org.zeith.tech.modules.transport.blocks.fluid_tank.basic;

import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.blocks.ICustomBlockItem;
import org.zeith.hammerlib.core.adapter.BlockHarvestAdapter;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.tech.api.block.ZeithTechStateProperties;
import org.zeith.tech.api.item.ItemHandlerFluidTank;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.core.client.renderer.item.BlockItemWithAltISTER;
import org.zeith.tech.modules.processing.blocks.base.machine.BlockBaseMachine;
import org.zeith.tech.modules.shared.BaseZT;
import org.zeith.tech.modules.transport.init.TilesZT_Transport;

import java.util.List;
import java.util.function.Consumer;

public class BlockFluidTankB
		extends BlockBaseMachine<TileFluidTankB>
		implements ICustomBlockItem
{
	public static final VoxelShape SHAPE = box(2, 0, 2, 14, 16, 14);
	
	public BlockFluidTankB(Properties props, BlockHarvestAdapter.MineableType toolType, Tier miningTier)
	{
		super(TileFluidTankB.class, props.lightLevel(s -> s.getValue(ZeithTechStateProperties.LIGHT_LEVEL)));
		BlockHarvestAdapter.bindTool(toolType, miningTier, this);
		
		registerDefaultState(defaultBlockState().setValue(BlockStateProperties.UP, false).setValue(BlockStateProperties.DOWN, false));
	}
	
	@Override
	public boolean hasAnalogOutputSignal(BlockState state)
	{
		return true;
	}
	
	@Override
	public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos)
	{
		if(level.getBlockEntity(pos) instanceof TileFluidTankB tankB)
			return Math.round(tankB.storage.getFluidAmount() * 15F / tankB.storage.getCapacity());
		return 0;
	}
	
	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx)
	{
		var accessor = ctx.getLevel();
		var pos = ctx.getClickedPos();
		
		return defaultBlockState()
				.setValue(BlockStateProperties.UP, accessor.getBlockState(pos.above()).is(this))
				.setValue(BlockStateProperties.DOWN, accessor.getBlockState(pos.below()).is(this));
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(ZeithTechStateProperties.LIGHT_LEVEL, BlockStateProperties.UP, BlockStateProperties.DOWN);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context)
	{
		return SHAPE;
	}
	
	@Override
	public BlockState updateShape(BlockState state, Direction ignore0, BlockState ignore1, LevelAccessor accessor, BlockPos pos, BlockPos ignore2)
	{
		state = state
				.setValue(BlockStateProperties.UP, accessor.getBlockState(pos.above()).is(this))
				.setValue(BlockStateProperties.DOWN, accessor.getBlockState(pos.below()).is(this));
		
		return state;
	}
	
	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player)
	{
		return Cast.optionally(level.getBlockEntity(pos), TileFluidTankB.class).map(TileFluidTankB::generateItem).orElseGet(() -> new ItemStack(this));
	}
	
	@NotNull
	@Override
	public TileFluidTankB newBlockEntity(BlockPos pos, BlockState state)
	{
		return new TileFluidTankB(pos, state);
	}
	
	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if(stack.hasTag())
		{
			TileFluidTankB tank;
			
			if(level.getBlockEntity(pos) instanceof TileFluidTankB tile) tank = tile;
			else
			{
				tank = newBlockEntity(pos, state);
				level.setBlockEntity(tank);
			}
			
			tank.loadFromItem(stack);
		}
	}
	
	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
	{
		NonNullList<ItemStack> stacks = NonNullList.create();
		BlockEntity tile = builder.getParameter(LootContextParams.BLOCK_ENTITY);
		if(tile instanceof TileFluidTankB te)
			stacks.add(te.generateItem(this));
		else
			stacks.add(new ItemStack(this));
		return stacks;
	}
	
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult ray)
	{
		if(world.getBlockEntity(pos) instanceof TileFluidTankB tank)
			if(FluidUtil.interactWithFluidHandler(player, InteractionHand.MAIN_HAND, tank.storage))
			{
				if(tank.isOnServer())
					tank.sync();
				return InteractionResult.SUCCESS;
			}
		
		return super.use(state, world, pos, player, hand, ray);
	}
	
	@Override
	public BlockItem createBlockItem()
	{
		return ZeithTech.TAB.add(new BlockItem(this, BaseZT.itemProps())
		{
			@Override
			public void initializeClient(Consumer<IClientItemExtensions> consumer)
			{
				BlockItemWithAltISTER.INSTANCE
						.bind(BlockFluidTankB.this, TilesZT_Transport.BASIC_FLUID_TANK)
						.ifPresent(consumer);
			}
			
			@Override
			public void appendHoverText(ItemStack stack, @Nullable Level lvl, List<Component> tooltip, TooltipFlag flags)
			{
				var tile = TilesZT_Transport.BASIC_FLUID_TANK.create(BlockPos.ZERO, defaultBlockState());
				if(tile == null) return;
				tile.loadFromItem(stack);
				var fluid = tile.storage.getFluid();
				
				if(!fluid.isEmpty())
				{
					tooltip.add(Component.empty().append(fluid.getDisplayName()).append(": ").append(Component.literal(I18n.get("info.zeithtech.fluid_capped", tile.storage.getFluidAmount(), tile.storage.getCapacity()))).withStyle(ChatFormatting.GRAY));
					
					if(flags.isAdvanced())
						tooltip.add(Component.literal(ForgeRegistries.FLUID_TYPES.get().getKey(fluid.getFluid().getFluidType()).toString())
								.withStyle(ChatFormatting.DARK_GRAY));
				}
			}
			
			@Override
			public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt)
			{
				var tile = TilesZT_Transport.BASIC_FLUID_TANK.create(BlockPos.ZERO, defaultBlockState());
				if(tile == null) return null;
				
				return new ItemHandlerFluidTank(stack, tile.storage.getCapacity(), item ->
				{
					tile.loadFromItem(item);
					return tile.storage.getFluid();
				}, (item, fluid) ->
				{
					tile.storage.setFluid(fluid);
					tile.saveToItem(item);
				});
			}
		});
	}
}