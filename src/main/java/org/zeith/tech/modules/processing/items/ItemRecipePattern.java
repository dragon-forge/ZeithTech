package org.zeith.tech.modules.processing.items;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.crafting.*;
import org.zeith.hammerlib.api.crafting.impl.ItemStackResult;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.hammerlib.util.java.tuples.Tuple2;
import org.zeith.hammerlib.util.java.tuples.Tuples;
import org.zeith.tech.api.item.IRecipePatternItem;
import org.zeith.tech.api.item.tooltip.TooltipStack;
import org.zeith.tech.modules.processing.client.renderer.item.ItemRecipePatternISTER;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ItemRecipePattern
		extends Item
		implements IRecipePatternItem
{
	public ItemRecipePattern(Properties props)
	{
		super(props);
	}
	
	@Override
	public int getMaxStackSize(ItemStack stack)
	{
		return getProvidedRecipe(stack) != null ? 1 : 64;
	}
	
	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer)
	{
		consumer.accept(new IClientItemExtensions()
		{
			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer()
			{
				return ItemRecipePatternISTER.INSTANCE;
			}
		});
	}
	
	@Override
	public NamespacedRecipeRegistry<?> getProvidedRecipeRegistry(ItemStack stack)
	{
		var patTag = stack.getTagElement("Pattern");
		if(patTag != null && !patTag.isEmpty())
		{
			var registry = new ResourceLocation(patTag.getString("Registry"));
			return Cast.cast(AbstractRecipeRegistry.getAllRegistries()
					.stream()
					.filter(r -> r instanceof NamespacedRecipeRegistry<?> && r.getRegistryId().equals(registry))
					.findFirst()
					.orElse(null));
		}
		
		return null;
	}
	
	@Override
	public INameableRecipe getProvidedRecipe(ItemStack stack)
	{
		var patTag = stack.getTagElement("Pattern");
		if(patTag != null && !patTag.isEmpty())
		{
			var recipe = new ResourceLocation(patTag.getString("Recipe"));
			var registry = getProvidedRecipeRegistry(stack);
			if(registry != null)
				return registry.getRecipes()
						.stream()
						.filter(r -> r.getRecipeName().equals(recipe))
						.findFirst()
						.orElse(null);
		}
		
		return null;
	}
	
	@Override
	public <T extends INameableRecipe> ItemStack createEncoded(NamespacedRecipeRegistry<T> registry, T recipe)
	{
		ItemStack stack = new ItemStack(this);
		var patTag = stack.getOrCreateTagElement("Pattern");
		patTag.putString("Registry", registry.getRegistryId().toString());
		patTag.putString("Recipe", recipe.getRecipeName().toString());
		return stack;
	}
	
	@Override
	public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items)
	{
		super.fillItemCategory(tab, items);
		
		if(allowedIn(tab))
		{
			List<Tuple2<NamespacedRecipeRegistry<?>, INameableRecipe>> recipes = AbstractRecipeRegistry.getAllRegistries()
					.stream()
					.mapMulti((BiConsumer<? super AbstractRecipeRegistry<?, ?, ?>, Consumer<Tuple2<NamespacedRecipeRegistry<?>, INameableRecipe>>>) (registry, cons) ->
					{
						if(registry instanceof NamespacedRecipeRegistry<?> named)
							for(INameableRecipe recipe : named.getRecipes())
								if(recipe.getResult() instanceof ItemStackResult)
									cons.accept(Tuples.immutable(named, recipe));
					})
					.toList();
			
			for(var recipe : recipes)
				items.add(createEncoded(Cast.cast(recipe.a()), recipe.b()));
		}
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> lines, TooltipFlag flags)
	{
		var reg = getProvidedRecipeRegistry(stack);
		var rec = getProvidedRecipe(stack);
		
		if(reg != null)
			lines.add(Component.translatable(Util.makeDescriptionId("recipe_type", reg.getRegistryId())).append(Component.literal(":")).withStyle(ChatFormatting.GRAY));
		if(rec != null && rec.getResult() instanceof ItemStackResult res)
			lines.add(Component.literal("- ").append(res.getBaseOutput().getHoverName()).withStyle(ChatFormatting.GRAY));
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
	{
		var held = player.getItemInHand(hand);
		
		if(held.getTagElement("Pattern") != null && player.isShiftKeyDown())
		{
			held.removeTagKey("Pattern");
			return new InteractionResultHolder<>(InteractionResult.SUCCESS, held);
		}
		
		return super.use(level, player, hand);
	}
	
	@Override
	public Optional<TooltipComponent> getTooltipImage(ItemStack stack)
	{
		if(stack.getItem() instanceof IRecipePatternItem pat)
		{
			var recipe = pat.getProvidedRecipe(stack);
			if(recipe != null && recipe.getResult() instanceof ItemStackResult res)
			{
				return Optional.of(new TooltipStack(res.getBaseOutput()));
			}
		}
		
		return super.getTooltipImage(stack);
	}
}
