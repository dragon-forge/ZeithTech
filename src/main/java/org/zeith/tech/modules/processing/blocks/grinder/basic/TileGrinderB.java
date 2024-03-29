package org.zeith.tech.modules.processing.blocks.grinder.basic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.api.enums.*;
import org.zeith.tech.api.recipes.base.IUnaryRecipe;
import org.zeith.tech.api.recipes.processing.RecipeGrinding;
import org.zeith.tech.api.utils.InventoryHelper;
import org.zeith.tech.modules.processing.blocks.base.unary_machine.basic.ContainerUnaryRecipeMachineB;
import org.zeith.tech.modules.processing.blocks.base.unary_machine.basic.TileUnaryRecipeMachineB;
import org.zeith.tech.modules.processing.init.RecipeRegistriesZT_Processing;
import org.zeith.tech.modules.processing.init.SoundsZT_Processing;
import org.zeith.tech.modules.shared.ui.SlotInput;
import org.zeith.tech.modules.shared.ui.SlotOutput;
import org.zeith.tech.utils.SidedInventory;

import java.util.stream.IntStream;

public class TileGrinderB
		extends TileUnaryRecipeMachineB<TileGrinderB, RecipeGrinding>
{
	public static final ResourceLocation GRINDER_GUI_TEXTURE = ZeithTechAPI.id("textures/processing/gui/grinder/basic.png");
	
	public TileGrinderB(BlockEntityType<TileGrinderB> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
		
		var ecfg = sidedConfig.getSideConfigs(SidedConfigTyped.ENERGY);
		ecfg.setRelative(RelativeDirection.FRONT, SideConfig.PULL);
		ecfg.setRelative(RelativeDirection.BACK, SideConfig.PULL);
		ecfg.setRelative(RelativeDirection.LEFT, SideConfig.PULL);
		ecfg.setRelative(RelativeDirection.RIGHT, SideConfig.PULL);
	}
	
	@Override
	protected SoundEvent getWorkingSound()
	{
		return SoundsZT_Processing.BASIC_GRINDER;
	}
	
	@Override
	protected ResourceLocation getGuiCustomTexture()
	{
		return GRINDER_GUI_TEXTURE;
	}
	
	@Override
	protected int getConsumptionPerTick()
	{
		return 40;
	}
	
	@Override
	protected IUnaryRecipe.IUnaryRecipeProvider<RecipeGrinding> createRecipeProvider()
	{
		return IUnaryRecipe.IUnaryRecipeProvider.fromRecipeRegistry(RecipeRegistriesZT_Processing.GRINDING);
	}
	
	@Override
	protected SidedInventory createSidedInventory()
	{
		var inv = new SidedInventory(3, sidedConfig.createItemAccess(new int[] { 0 }, new int[] {
				1,
				2
		}));
		inv.isStackValid = (slot, stack) -> level != null && slot == 0 && recipeProvider.findMatching(this, stack).isPresent();
		return inv;
	}
	
	@Override
	public void update()
	{
		super.update();
		
		if(isEnabled() && isOnClient() && !isInterrupted())
		{
			var item = inputItemDisplay.get();
			
			if(atTickRate(2) && !item.isEmpty())
			{
				var rand = level.getRandom();
				
				Vec3 topPos = Vec3.atLowerCornerOf(worldPosition).add(0.5, 1, 0.5);
				level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, item),
						topPos.x + rand.nextGaussian() * 0.15D, topPos.y, topPos.z + rand.nextGaussian() * 0.15D,
						rand.nextGaussian() * 0.05D, rand.nextDouble() * 0.2D, rand.nextGaussian() * 0.05D);
			}
		}
	}
	
	@Override
	protected boolean storeRecipeResult(RecipeGrinding recipe, boolean simulate)
	{
		boolean main = store(recipe.assemble(this), simulate);
		
		var extra = recipe.getExtra().orElse(null);
		if(extra != null)
		{
			if(simulate && !storeExtra(extra.getMinimalResult(), true))
				return false;
			
			if(!simulate && level.getRandom().nextFloat() <= extra.chance())
				storeExtra(extra.assemble(level.getRandom()), false);
		}
		
		return main;
	}
	
	public boolean storeExtra(ItemStack stacks, boolean simulate)
	{
		return InventoryHelper.storeStack(inventory, IntStream.range(2, 3), stacks, simulate);
	}
	
	@Override
	public ContainerUnaryRecipeMachineB<TileGrinderB> openContainer(Player player, int windowId)
	{
		return new ContainerGrinder(this, player, windowId);
	}
	
	public static class ContainerGrinder
			extends ContainerUnaryRecipeMachineB<TileGrinderB>
	{
		public ContainerGrinder(TileGrinderB tile, Player player, int windowId)
		{
			super(tile, player, windowId);
		}
		
		@Override
		protected void addMachineSlots(TileGrinderB tile)
		{
			this.addSlot(new SlotInput(tile.inventory, 0, 37, 35));
			this.addSlot(new SlotOutput(tile.inventory, 1, 97, 35));
			this.addSlot(new SlotOutput(tile.inventory, 2, 123, 35));
		}
		
		@Override
		@OnlyIn(Dist.CLIENT)
		public GuiGrinderB openScreen(Inventory inv, Component label)
		{
			return new GuiGrinderB(this, inv, label);
		}
	}
}
