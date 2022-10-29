package org.zeith.tech.compat.jei.category;

import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IStackHelper;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.*;
import mezz.jei.common.Internal;
import mezz.jei.common.network.IConnectionToServer;
import mezz.jei.common.network.packets.PacketRecipeTransfer;
import mezz.jei.common.transfer.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import org.jetbrains.annotations.Nullable;
import org.zeith.tech.api.recipes.processing.RecipeMachineAssembler;
import org.zeith.tech.modules.processing.blocks.machine_assembler.advanced.ContainerMachineAssemblerA;

import java.util.Collections;
import java.util.List;

/**
 * All slots with name, starting with "exclude_from_transfer" will be excluded from recipe transfer.
 */
public class JeiTransferHandlerWithNonTransferableSlots
		extends BasicRecipeTransferHandler<ContainerMachineAssemblerA, RecipeMachineAssembler>
{
	private final IConnectionToServer serverConnection;
	private final IStackHelper stackHelper;
	private final IRecipeTransferHandlerHelper handlerHelper;
	private final IRecipeTransferInfo<ContainerMachineAssemblerA, RecipeMachineAssembler> transferInfo;
	
	public JeiTransferHandlerWithNonTransferableSlots(
			IStackHelper stackHelper,
			IRecipeTransferHandlerHelper handlerHelper,
			IRecipeTransferInfo<ContainerMachineAssemblerA, RecipeMachineAssembler> transferInfo)
	{
		super(Internal.getServerConnection(), stackHelper, handlerHelper, transferInfo);
		this.serverConnection = Internal.getServerConnection();
		this.stackHelper = stackHelper;
		this.handlerHelper = handlerHelper;
		this.transferInfo = transferInfo;
	}
	
	public static <C extends AbstractContainerMenu, R> IRecipeTransferInfo<C, R> newTransferInfo(final Class<? extends C> containerClass, @Nullable final MenuType<C> menuType, final RecipeType<R> recipeType, final int recipeSlotStart, final int recipeSlotCount, final int inventorySlotStart, final int inventorySlotCount)
	{
		return new BasicRecipeTransferInfo<>(containerClass, menuType, recipeType, recipeSlotStart, recipeSlotCount, inventorySlotStart, inventorySlotCount);
	}
	
	@Override
	public @Nullable IRecipeTransferError transferRecipe(ContainerMachineAssemblerA container, RecipeMachineAssembler recipe, IRecipeSlotsView recipeSlotsView, Player player, boolean maxTransfer, boolean doTransfer)
	{
		if(!this.serverConnection.isJeiOnServer())
		{
			Component tooltipMessage = Component.translatable("jei.tooltip.error.recipe.transfer.no.server");
			return this.handlerHelper.createUserErrorWithTooltip(tooltipMessage);
		} else if(!this.transferInfo.canHandle(container, recipe))
		{
			IRecipeTransferError handlingError = this.transferInfo.getHandlingError(container, recipe);
			return handlingError != null ? handlingError : this.handlerHelper.createInternalError();
		} else
		{
			List<Slot> craftingSlots = Collections.unmodifiableList(this.transferInfo.getRecipeSlots(container, recipe));
			List<Slot> inventorySlots = Collections.unmodifiableList(this.transferInfo.getInventorySlots(container, recipe));
			if(!validateTransferInfo(this.transferInfo, container, craftingSlots, inventorySlots))
			{
				return this.handlerHelper.createInternalError();
			} else
			{
				List<IRecipeSlotView> inputItemSlotViews = recipeSlotsView.getSlotViews(RecipeIngredientRole.INPUT)
						.stream()
						.filter(view -> !view.getSlotName().map("exclude_from_transfer"::startsWith).orElse(false))
						.toList();
				
				if(!validateRecipeView(this.transferInfo, container, craftingSlots, inputItemSlotViews))
				{
					return this.handlerHelper.createInternalError();
				} else
				{
					InventoryState inventoryState = getInventoryState(craftingSlots, inventorySlots, player, container, this.transferInfo);
					if(inventoryState == null)
					{
						return this.handlerHelper.createInternalError();
					} else
					{
						int inputCount = inputItemSlotViews.size();
						if(!inventoryState.hasRoom(inputCount))
						{
							Component message = Component.translatable("jei.tooltip.error.recipe.transfer.inventory.full");
							return this.handlerHelper.createUserErrorWithTooltip(message);
						} else
						{
							RecipeTransferOperationsResult transferOperations = RecipeTransferUtil.getRecipeTransferOperations(this.stackHelper, inventoryState.availableItemStacks(), inputItemSlotViews, craftingSlots);
							if(transferOperations.missingItems.size() > 0)
							{
								Component message = Component.translatable("jei.tooltip.error.recipe.transfer.missing");
								return this.handlerHelper.createUserErrorForMissingSlots(message, transferOperations.missingItems);
							} else if(!RecipeTransferUtil.validateSlots(player, transferOperations.results, craftingSlots, inventorySlots))
							{
								return this.handlerHelper.createInternalError();
							} else
							{
								if(doTransfer)
								{
									boolean requireCompleteSets = this.transferInfo.requireCompleteSets(container, recipe);
									PacketRecipeTransfer packet = new PacketRecipeTransfer(transferOperations.results, craftingSlots, inventorySlots, maxTransfer, requireCompleteSets);
									this.serverConnection.sendPacketToServer(packet);
								}
								
								return null;
							}
						}
					}
				}
			}
		}
	}
}