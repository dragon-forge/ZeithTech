package org.zeith.tech.modules.shared.items.multitool;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.items.IDynamicallyTaggedItem;
import org.zeith.tech.api.item.IAccumulatorItem;
import org.zeith.tech.api.item.multitool.*;
import org.zeith.tech.api.item.tooltip.TooltipEnergyBar;
import org.zeith.tech.api.item.tooltip.TooltipMulti;
import org.zeith.tech.modules.shared.proxy.ClientSharedProxyZT;
import org.zeith.tech.utils.ChatUtil;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class ItemMultiTool
		extends DiggerItem
		implements IMultiToolItem, IDynamicallyTaggedItem
{
	public final ResourceLocation model, emptyModel;
	
	public ItemMultiTool(Properties props, ResourceLocation model, ResourceLocation emptyModel)
	{
		super(0F, 0F, Tiers.IRON, BlockTags.MINEABLE_WITH_PICKAXE, props.stacksTo(1).durability(0));
		this.model = model;
		this.emptyModel = emptyModel;
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
	{
		var held = player.getItemInHand(hand);
		
		if(player.isShiftKeyDown() || shouldRender2D(held))
		{
			var menu = new SimpleMenuProvider((windowId, inv, p0) -> new ContainerMultiTool(windowId, inv), held.getHoverName());
			player.openMenu(menu);
			return InteractionResultHolder.consume(held);
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
		return List.of(model, emptyModel);
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
		
		if(inHand && entity instanceof LivingEntity living && !shouldRender2D(stack))
		{
			living.swingTime = 100;
			living.attackAnim = living.oAttackAnim = 0;
		}
		
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
		float f = Math.max(0.0F, getBarWidth(stack) / 13F);
		return Mth.hsvToRgb(173 / 360F, 1.0F, 0.666F + f * 0.333F);
	}
	
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt)
	{
		return new MultiToolCapabilities(stack, this);
	}
	
	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer)
	{
		consumer.accept(new IClientItemExtensions()
		{
			@Override
			public HumanoidModel.ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack)
			{
				if(shouldRender2D(itemStack)) return null;
				
				return ClientSharedProxyZT.MULTI_TOOL_ARM_POSE;
			}
		});
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn)
	{
		var itemColor = ChatUtil.setColor(TextColor.fromRgb(0xCCCCCC));
		var itemSubColor = ChatUtil.setColor(TextColor.fromRgb(0x888888));
		
		TooltipFlagMultiTool flag = new TooltipFlagMultiTool(flagIn, stack);
		
		var motor = getMotor(stack);
		if(motor.isPresent())
		{
			var it = motor.orElseThrow().getB();
			tooltip.add(it.getHoverName().copy().withStyle(itemColor));
			ChatUtil.prepend(tip -> it.getItem().appendHoverText(it, worldIn, tip, flag), tooltip, Component.literal("  ").withStyle(itemSubColor));
		}
		
		var accum = getAccumulator(stack);
		if(accum.isPresent())
		{
			var it = accum.orElseThrow().getB();
			tooltip.add(it.getHoverName().copy().withStyle(itemColor));
			ChatUtil.prepend(tip -> it.getItem().appendHoverText(it, worldIn, tip, flag), tooltip, Component.literal("  ").withStyle(itemSubColor));
		}
		
		var head = getHead(stack);
		if(head.isPresent())
		{
			var it = head.orElseThrow().getB();
			tooltip.add(it.getHoverName().copy().withStyle(itemColor));
			ChatUtil.prepend(tip -> it.getItem().appendHoverText(it, worldIn, tip, flag), tooltip, Component.literal("  ").withStyle(itemSubColor));
		}
		
		tooltip.add(motor.isEmpty() && head.isEmpty() && accum.isEmpty() ? Component.translatable("info.zeithtech.empty").withStyle(itemColor) : Component.literal(""));
	}
	
	@Override
	public Optional<TooltipComponent> getTooltipImage(ItemStack stack)
	{
		return TooltipMulti.create(
				getAccumulator(stack).stream().map(t -> new TooltipEnergyBar(t.getA().getEnergy(t.getB()), t.getA().getMaxEnergy(t.getB())))
		);
	}
	
	@Override
	public Stream<TagKey<Item>> getExtraItemTags(ItemStack stack)
	{
		return getHead(stack)
				.stream()
				.flatMap(t -> t.getA()
						.getHeadItemTags(t.getB(), stack)
						.stream()
				);
	}
	
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment)
	{
		return false;
	}
	
	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book)
	{
		return false;
	}
	
	protected static class MultiToolCapabilities
			implements ICapabilityProvider, IEnergyStorage
	{
		final ItemStack stack;
		final IMultiToolItem multiToolItem;
		
		public MultiToolCapabilities(ItemStack stack, IMultiToolItem multiToolItem)
		{
			this.stack = stack;
			this.multiToolItem = multiToolItem;
		}
		
		final LazyOptional<IEnergyStorage> energy = LazyOptional.of(() -> this);
		
		@Override
		public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
		{
			if(cap == ForgeCapabilities.ENERGY && multiToolItem.getAccumulator(stack).isPresent()) return energy.cast();
			return LazyOptional.empty();
		}
		
		@Override
		public int receiveEnergy(int maxReceive, boolean simulate)
		{
			var accumOpt = multiToolItem.getAccumulator(stack);
			if(accumOpt.isEmpty()) return 0;
			var accum = accumOpt.orElseThrow();
			maxReceive = accum.getA().storeEnergy(accum.getB(), maxReceive, simulate);
			if(!simulate) multiToolItem.setAccumulator(stack, accum.getB());
			return maxReceive;
		}
		
		@Override
		public int extractEnergy(int maxExtract, boolean simulate)
		{
			return 0;
		}
		
		@Override
		public int getEnergyStored()
		{
			var accumOpt = multiToolItem.getAccumulator(stack);
			if(accumOpt.isEmpty()) return 0;
			var accum = accumOpt.orElseThrow();
			return accum.getA().getEnergy(accum.getB());
		}
		
		@Override
		public int getMaxEnergyStored()
		{
			var accumOpt = multiToolItem.getAccumulator(stack);
			if(accumOpt.isEmpty()) return 0;
			var accum = accumOpt.orElseThrow();
			return accum.getA().getMaxEnergy(accum.getB());
		}
		
		@Override
		public boolean canExtract()
		{
			return false;
		}
		
		@Override
		public boolean canReceive()
		{
			return multiToolItem.getAccumulator(stack).isPresent();
		}
	}
}