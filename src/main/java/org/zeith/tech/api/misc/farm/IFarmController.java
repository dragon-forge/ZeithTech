package org.zeith.tech.api.misc.farm;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.tech.api.misc.SoundConfiguration;
import org.zeith.tech.modules.processing.blocks.farm.actions.BreakBlockAction;

import java.util.*;

public interface IFarmController
{
	@Nullable
	FarmAlgorithm getAlgorithm();
	
	Level getFarmLevel();
	
	FakePlayer getAsPlayer(ServerLevel level);
	
	default Optional<FakePlayer> getAsPlayer()
	{
		return Cast.optionally(getFarmLevel(), ServerLevel.class)
				.map(this::getAsPlayer);
	}
	
	BlockPos getFarmPosition();
	
	FluidTank getWaterInventory();
	
	boolean hasPlatform(BlockPos platform);
	
	IItemHandlerModifiable getInventory(EnumFarmItemCategory category);
	
	default FarmItemConsumer createItemConsumer(EnumFarmItemCategory category, Ingredient item, int amount)
	{
		return new FarmItemConsumer(category, item, amount);
	}
	
	default FarmItemConsumer createItemConsumer(EnumFarmItemCategory category, ItemStack item)
	{
		if(item.isEmpty()) return createItemConsumer(category, Ingredient.EMPTY, 0);
		return createItemConsumer(category, Ingredient.of(item.copy().split(1)), item.getCount());
	}
	
	void queueBlockPlacement(FarmItemConsumer itemConsumer, BlockPos pos, BlockState toPlace, int waterUsage, int priority);
	
	default void queueBlockHarvest(BlockPos pos, int priority)
	{
		queueMultipleBlockHarvests(List.of(new BreakBlockAction(pos, priority)));
	}
	
	void queueMultipleBlockHarvests(Collection<BreakBlockAction> action);
	
	void queueBlockTransformation(BlockPos pos, BlockState source, BlockState dest, List<ItemStack> drops, SoundConfiguration sound, int waterUsage, int priority);
	
	default void queueBlockTransformation(BlockPos pos, BlockState source, BlockState dest, int waterUsage, int priority)
	{
		queueBlockTransformation(pos, source, dest, List.of(), null, waterUsage, priority);
	}
	
	default BlockPlaceContext createPlaceContext(ServerLevel level, BlockPos pos, ItemStack item, Direction dir)
	{
		return new BlockPlaceContext(getAsPlayer(level), InteractionHand.MAIN_HAND, item, new BlockHitResult(Vec3.atCenterOf(pos), dir, pos, false));
	}
}