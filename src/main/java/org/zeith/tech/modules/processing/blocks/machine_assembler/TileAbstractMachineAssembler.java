package org.zeith.tech.modules.processing.blocks.machine_assembler;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.zeith.hammerlib.api.crafting.ICraftingExecutor;
import org.zeith.hammerlib.api.inv.SimpleInventory;
import org.zeith.hammerlib.api.io.NBTSerializable;
import org.zeith.hammerlib.net.properties.PropertyInt;
import org.zeith.hammerlib.net.properties.PropertyItemStack;
import org.zeith.hammerlib.util.java.DirectStorage;
import org.zeith.tech.api.recipes.processing.RecipeMachineAssembler;
import org.zeith.tech.api.tile.ITieredTile;
import org.zeith.tech.modules.processing.blocks.base.machine.TileBaseMachine;
import org.zeith.tech.utils.ItemStackHelper;

public abstract class TileAbstractMachineAssembler<T extends TileAbstractMachineAssembler<T>>
		extends TileBaseMachine<T>
		implements ICraftingExecutor, ITieredTile
{
	@NBTSerializable("Items")
	public final SimpleInventory craftingInventory = new SimpleInventory(25);
	
	@NBTSerializable("ResultInv")
	public final SimpleInventory resultInventory = new SimpleInventory(1);
	
	@NBTSerializable("CraftProgress")
	protected int _progress;
	protected int prevProgress;
	
	@NBTSerializable("CraftTime")
	protected int _craftTime = 100;
	
	@NBTSerializable("CraftResult")
	protected ItemStack _craftResult = ItemStack.EMPTY;
	
	@NBTSerializable("ActiveRecipe")
	protected ResourceLocation _activeRecipeId;
	
	public final PropertyItemStack craftResult = new PropertyItemStack(DirectStorage.create(v -> _craftResult = v, () -> _craftResult));
	
	// GUI-synced parameters.
	public final PropertyInt craftingProgress = new PropertyInt(DirectStorage.create(v -> _progress = v, () -> _progress));
	public final PropertyInt craftTime = new PropertyInt(DirectStorage.create(v -> _craftTime = v, () -> _craftTime));
	
	public TileAbstractMachineAssembler(BlockEntityType<T> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
		this.dispatcher.registerProperty("result", craftResult);
		resultInventory.isStackValid = (slot, stack) -> false;
	}
	
	@Override
	public boolean triggerEvent(int event, int data)
	{
		if(event == 1 && data == 1)
		{
			if(isOnClient())
				for(ItemStack stack : craftingInventory)
					if(!stack.isEmpty())
						stack.shrink(1);
			return true;
		}
		
		return super.triggerEvent(event, data);
	}
	
	public float getProgress(float partial)
	{
		return Mth.lerp(partial, prevProgress, _progress);
	}
	
	public boolean isValidRecipe(RecipeMachineAssembler recipe)
	{
		return (resultInventory.getItem(0).isEmpty() || ItemStackHelper.matchesIgnoreCount(resultInventory.getItem(0), recipe.getRecipeOutput(this)))
				&& recipe.matches(craftingInventory, getTechTier());
	}
	
	public boolean hasInputSlot(int idx)
	{
		return true;
	}
}
