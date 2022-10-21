package org.zeith.tech.modules.shared.items.multitool;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;
import org.zeith.tech.api.item.IAccumulatorItem;
import org.zeith.tech.api.item.multitool.*;
import org.zeith.tech.api.item.tooltip.TooltipEnergyBar;
import org.zeith.tech.api.item.tooltip.TooltipMulti;
import org.zeith.tech.modules.shared.proxy.ClientSharedProxyZT;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class ItemMultiTool
		extends DiggerItem
		implements IMultiToolItem
{
	public final ResourceLocation model;
	
	public ItemMultiTool(Properties props, ResourceLocation model)
	{
		super(0F, 0F, Tiers.IRON, BlockTags.MINEABLE_WITH_PICKAXE, props.stacksTo(1).durability(0));
		this.model = model;
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
	{
		if(player.isShiftKeyDown())
		{
			var menu = new SimpleMenuProvider((windowId, inv, p0) -> new ContainerMultiTool(windowId, inv), getDescription());
			player.openMenu(menu);
			return InteractionResultHolder.consume(player.getItemInHand(hand));
		}
		
		return super.use(level, player, hand);
	}
	
	@Override
	public float getAttackDamage()
	{
		return 0;
	}
	
	@Override
	public UseAnim getUseAnimation(ItemStack stack)
	{
		return UseAnim.CUSTOM;
	}
	
	@Override
	public boolean setMotor(ItemStack multiToolStack, ItemStack motorStack)
	{
		if(!motorStack.isEmpty() && !(motorStack.getItem() instanceof IMultiToolMotor))
			return false;
		multiToolStack.getOrCreateTagElement("Parts").put("Motor", motorStack.serializeNBT());
		return true;
	}
	
	@Override
	public boolean setHead(ItemStack multiToolStack, ItemStack headStack)
	{
		if(!headStack.isEmpty() && !(headStack.getItem() instanceof IMultiToolHead))
			return false;
		multiToolStack.getOrCreateTagElement("Parts").put("Head", headStack.serializeNBT());
		return true;
	}
	
	@Override
	public boolean setAccumulator(ItemStack multiToolStack, ItemStack accumStack)
	{
		if(!accumStack.isEmpty() && !(accumStack.getItem() instanceof IAccumulatorItem))
			return false;
		multiToolStack.getOrCreateTagElement("Parts").put("Battery", accumStack.serializeNBT());
		return true;
	}
	
	@Override
	public Optional<Tuple<IMultiToolMotor, ItemStack>> getMotor(ItemStack multiToolStack)
	{
		var tag = multiToolStack.getTagElement("Parts");
		if(tag != null && tag.contains("Motor"))
		{
			var stack = ItemStack.of(tag.getCompound("Motor"));
			if(!stack.isEmpty() && stack.getItem() instanceof IMultiToolMotor m)
				return Optional.of(new Tuple<>(m, stack));
		}
		return Optional.empty();
	}
	
	@Override
	public Optional<Tuple<IMultiToolHead, ItemStack>> getHead(ItemStack multiToolStack)
	{
		var tag = multiToolStack.getTagElement("Parts");
		if(tag != null && tag.contains("Head"))
		{
			var stack = ItemStack.of(tag.getCompound("Head"));
			if(!stack.isEmpty() && stack.getItem() instanceof IMultiToolHead h)
				return Optional.of(new Tuple<>(h, stack));
		}
		return Optional.empty();
	}
	
	@Override
	public Optional<Tuple<IAccumulatorItem, ItemStack>> getAccumulator(ItemStack multiToolStack)
	{
		var tag = multiToolStack.getTagElement("Parts");
		if(tag != null && tag.contains("Battery"))
		{
			var stack = ItemStack.of(tag.getCompound("Battery"));
			if(!stack.isEmpty() && stack.getItem() instanceof IAccumulatorItem acc)
				return Optional.of(new Tuple<>(acc, stack));
		}
		return Optional.empty();
	}
	
	@Override
	public ResourceLocation getMultiToolBaseModel(ItemStack multiToolStack)
	{
		return model;
	}
	
	@Override
	public List<ResourceLocation> getAllPossibleMultiToolPartModels()
	{
		return List.of(model);
	}
	
	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean inHand)
	{
		getHead(stack).ifPresentOrElse(head ->
		{
			var headStack = head.getB();
			if(headStack.hasTag() && headStack.getTag().contains("Enchantments"))
			{
				stack.addTagElement("Enchantments", headStack.getTag().get("Enchantments"));
			} else stack.removeTagKey("Enchantments");
		}, () ->
		{
			stack.removeTagKey("Enchantments");
		});
		
		super.inventoryTick(stack, level, entity, slot, inHand);
	}
	
	public int getConsumption(ItemStack stack, BlockState state)
	{
		var motor = getMotor(stack);
		int fe = 200;
		if(motor.isPresent())
		{
			var e = motor.orElseThrow();
			fe *= e.getA().getMotorEnergyMultiplier(e.getB(), stack);
		}
		return fe;
	}
	
	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state)
	{
		var accum = getAccumulator(stack);
		if(accum.isEmpty()) return 0F;
		var head = getHead(stack);
		if(head.isEmpty()) return 0F;
		
		// It's just there... Nobody really cares about it :(
		var motor = getMotor(stack);
		if(motor.isEmpty()) return 0F;
		
		int neededFE = getConsumption(stack, state);
		
		var accumE = accum.orElseThrow();
		var headE = head.orElseThrow();
		var motorE = motor.orElseThrow();
		
		float mul = motorE.getA().getMotorSpeedMultiplier(motorE.getB(), stack);
		
		return accumE.getA().getEnergy(accumE.getB()) >= neededFE
				? headE.getA().getHeadMiningSpeed(headE.getB(), stack, state) * mul
				: 0;
	}
	
	@Override
	public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity digger)
	{
		var accum = getAccumulator(stack);
		if(accum.isEmpty()) return false;
		var head = getHead(stack);
		if(head.isEmpty()) return false;
		var motor = getMotor(stack);
		if(motor.isEmpty()) return false;
		
		int neededFE = getConsumption(stack, state);
		var accumE = accum.orElseThrow();
		var headE = head.orElseThrow();
		
		if(!level.isClientSide && state.getDestroySpeed(level, pos) != 0.0F)
		{
			if(accumE.getA().takeEnergyWhole(accumE.getB(), neededFE, false))
				setAccumulator(stack, accumE.getB());
			else
				return false;
		}
		
		return true;
	}
	
	@Override
	public boolean isCorrectToolForDrops(ItemStack stack, BlockState state)
	{
		var accum = getAccumulator(stack);
		if(accum.isEmpty()) return false;
		var head = getHead(stack);
		if(head.isEmpty()) return false;
		
		// It's just there... Nobody really cares about it :(
		var motor = getMotor(stack);
		if(motor.isEmpty()) return false;
		
		int neededFE = getConsumption(stack, state);
		
		var accumE = accum.orElseThrow();
		var headE = head.orElseThrow();
		
		return accumE.getA().getEnergy(accumE.getB()) >= neededFE
				&& headE.getA().isCorrectHeadForDrops(headE.getB(), stack, state);
	}
	
	@Override
	public boolean isEnchantable(ItemStack p_41456_)
	{
		return false;
	}
	
	@Override
	public boolean canBeDepleted()
	{
		return false;
	}
	
	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken)
	{
		return 0;
	}
	
	@Override
	public boolean isBarVisible(ItemStack stack)
	{
		return getAccumulator(stack).isPresent();
	}
	
	@Override
	public int getBarWidth(ItemStack stack)
	{
		return getAccumulator(stack)
				.map(t -> Math.round(t.getA().getEnergy(t.getB()) * 13F / t.getA().getMaxEnergy(t.getB())))
				.orElse(0);
	}
	
	@Override
	public int getBarColor(ItemStack stack)
	{
		float stackMaxDamage = this.getMaxDamage(stack);
		float f = Math.max(0.0F, (stackMaxDamage - (float) stack.getDamageValue()) / stackMaxDamage);
		return Mth.hsvToRgb(173 / 360F, 1.0F, 0.333F + f * 0.666F);
	}
	
	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer)
	{
		consumer.accept(new IClientItemExtensions()
		{
			@Override
			public HumanoidModel.@Nullable ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack)
			{
				return ClientSharedProxyZT.MULTI_TOOL_ARM_POSE;
			}
		});
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn)
	{
		var accum = getAccumulator(stack);
		if(accum.isPresent())
		{
			var it = accum.orElseThrow().getB();
			it.getItem().appendHoverText(it, worldIn, tooltip, flagIn);
		}
	}
	
	@Override
	public Optional<TooltipComponent> getTooltipImage(ItemStack stack)
	{
		return TooltipMulti.create(
				getAccumulator(stack).stream().map(t -> new TooltipEnergyBar(t.getA().getEnergy(t.getB()), t.getA().getMaxEnergy(t.getB())))
		);
	}
}