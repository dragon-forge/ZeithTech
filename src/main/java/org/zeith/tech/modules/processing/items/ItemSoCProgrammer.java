package org.zeith.tech.modules.processing.items;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.NotNull;
import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.api.misc.farm.FarmAlgorithm;
import org.zeith.tech.api.utils.InventoryHelper;
import org.zeith.tech.modules.shared.BaseZT;
import org.zeith.tech.modules.shared.init.ItemsZT;

import java.util.*;

public class ItemSoCProgrammer
		extends Item
{
	public ItemSoCProgrammer()
	{
		super(BaseZT.itemProps().stacksTo(1));
	}
	
	public static Optional<FarmAlgorithm> findAlgorithm(List<ItemStack> stacks)
	{
		for(var algo : ZeithTechAPI.get().getFarmAlgorithms())
			if(stacks.stream().anyMatch(algo.getProgrammingItem()))
				return Optional.of(algo);
		return Optional.empty();
	}
	
	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity)
	{
		if(entity instanceof ServerPlayer player && stack.getTag() != null && stack.getTag().contains("soc_programmer_target", Tag.TAG_STRING))
		{
			var algo = ZeithTechAPI.get().getFarmAlgorithms().getValue(new ResourceLocation(stack.getTag().getString("soc_programmer_target")));
			
			if(algo != null)
			{
				var inv = player.getInventory();
				int slot = inv.findSlotMatchingItem(new ItemStack(ItemsZT.FARM_SOC));
				if(slot >= 0)
				{
					var socStack = inv.getItem(slot);
					if(socStack.getItem() instanceof ItemFarmSoC soc)
					{
						soc.setAlgorithm(socStack, algo);
						inv.setItem(slot, socStack);
					}
				}
			}
		}
		
		return stack;
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
	{
		ItemStack stack = player.getItemInHand(hand);
		
		if(stack.getTag() != null && stack.getTag().contains("soc_programmer_target", Tag.TAG_STRING)
				&& player.getInventory().findSlotMatchingItem(new ItemStack(ItemsZT.FARM_SOC)) >= 0)
		{
			player.startUsingItem(hand);
			return InteractionResultHolder.consume(stack);
		}
		
		return InteractionResultHolder.fail(stack);
	}
	
	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean isHeld)
	{
		if(entity instanceof ServerPlayer player)
		{
			if(isHeld)
			{
				double d0 = player.getReachDistance();
				var hitResult = player.pick(d0, 1F, false);
				
				if(hitResult.getType() == HitResult.Type.BLOCK && hitResult instanceof BlockHitResult res)
				{
					var state = level.getBlockState(res.getBlockPos());
					var items = NonNullList.<ItemStack> create();
					
					items.add(state.getCloneItemStack(res, level, res.getBlockPos(), player));
					
					items.addAll(InventoryHelper.getBlockDropsAt(player.getLevel(), res.getBlockPos(), RandomSource.create(1L)));
					
					var hitLoc = res.getLocation();
					level.getEntitiesOfClass(ItemEntity.class, new AABB(hitLoc, hitLoc).inflate(0.125F))
							.stream()
							.sorted(Comparator.comparingDouble(ent -> ent.distanceToSqr(hitLoc)))
							.map(ItemEntity::getItem)
							.forEach(items::add);
					
					findAlgorithm(items).ifPresentOrElse(algo ->
							stack.addTagElement("soc_programmer_target", StringTag.valueOf(algo.getRegistryName().toString())), () ->
							stack.removeTagKey("soc_programmer_target")
					);
				} else
					stack.removeTagKey("soc_programmer_target");
			} else
				stack.removeTagKey("soc_programmer_target");
		}
		
		super.inventoryTick(stack, level, entity, slot, isHeld);
	}
	
	@Override
	public UseAnim getUseAnimation(ItemStack p_41452_)
	{
		return UseAnim.BOW;
	}
	
	@Override
	public int getUseDuration(ItemStack stack)
	{
		return 3 * 20;
	}
	
	public FarmAlgorithm getAlgorithm(ItemStack stack)
	{
		var tag = stack.getTag();
		if(tag != null && tag.contains("soc_programmer_target"))
			return ZeithTechAPI.get().getFarmAlgorithms()
					.getValue(new ResourceLocation(tag.getString("soc_programmer_target")));
		return null;
	}
	
	@Override
	public int getBarColor(@NotNull ItemStack stack)
	{
		return Optional.ofNullable(getAlgorithm(stack)).map(FarmAlgorithm::getColor).orElse(0xFFFFFF);
	}
}