package org.zeith.tech.modules.processing.blocks.sawmill.basic;

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
import org.zeith.tech.api.recipes.processing.RecipeSawmill;
import org.zeith.tech.modules.processing.blocks.base.unary_machine.basic.ContainerUnaryRecipeMachineB;
import org.zeith.tech.modules.processing.blocks.base.unary_machine.basic.TileUnaryRecipeMachineB;
import org.zeith.tech.modules.processing.init.*;
import org.zeith.tech.modules.shared.ui.SlotInput;
import org.zeith.tech.modules.shared.ui.SlotOutput;
import org.zeith.tech.utils.InventoryHelper;
import org.zeith.tech.utils.SidedInventory;

import java.util.stream.IntStream;

public class TileSawmillB
		extends TileUnaryRecipeMachineB<TileSawmillB, RecipeSawmill>
{
	public static final ResourceLocation SAWMILL_GUI_TEXTURE = ZeithTechAPI.id("textures/processing/gui/sawmill/basic.png");
	
	public TileSawmillB(BlockPos pos, BlockState state)
	{
		this(TilesZT_Processing.BASIC_SAWMILL, pos, state);
	}
	
	public TileSawmillB(BlockEntityType<TileSawmillB> type, BlockPos pos, BlockState state)
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
		return SoundsZT_Processing.BASIC_SAWMILL;
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
	protected ResourceLocation getGuiCustomTexture()
	{
		return SAWMILL_GUI_TEXTURE;
	}
	
	@Override
	protected int getConsumptionPerTick()
	{
		return 20;
	}
	
	@Override
	protected IUnaryRecipe.IUnaryRecipeProvider<RecipeSawmill> createRecipeProvider()
	{
		return IUnaryRecipe.IUnaryRecipeProvider.fromRecipeRegistry(RecipeRegistriesZT_Processing.SAWMILL);
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
	protected boolean storeRecipeResult(RecipeSawmill recipe, boolean simulate)
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
	public ContainerUnaryRecipeMachineB<TileSawmillB> openContainer(Player player, int windowId)
	{
		return new ContainerSawmill(this, player, windowId);
	}
	
	public static class ContainerSawmill
			extends ContainerUnaryRecipeMachineB<TileSawmillB>
	{
		public ContainerSawmill(TileSawmillB tile, Player player, int windowId)
		{
			super(tile, player, windowId);
		}
		
		@Override
		protected void addMachineSlots(TileSawmillB tile)
		{
			this.addSlot(new SlotInput(tile.inventory, 0, 37, 35));
			this.addSlot(new SlotOutput(tile.inventory, 1, 97, 35));
			this.addSlot(new SlotOutput(tile.inventory, 2, 123, 35));
		}
		
		@Override
		@OnlyIn(Dist.CLIENT)
		public GuiSawmillB openScreen(Inventory inv, Component label)
		{
			return new GuiSawmillB(this, inv, label);
		}
	}
}
