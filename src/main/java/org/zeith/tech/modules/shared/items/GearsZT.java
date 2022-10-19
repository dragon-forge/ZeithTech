package org.zeith.tech.modules.shared.items;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.UpgradeRecipe;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.hammerlib.core.RecipeHelper;
import org.zeith.hammerlib.event.recipe.RegisterRecipesEvent;
import org.zeith.tech.modules.shared.BaseZT;
import org.zeith.tech.modules.shared.init.TagsZT;

@SimplyRegister(prefix = "gears/")
public interface GearsZT
{
	@RegistryName("wooden")
	Item WOODEN_GEAR = BaseZT.newItem(TagsZT.Items.GEARS_WOODEN);
	
	@RegistryName("stone")
	Item STONE_GEAR = BaseZT.newItem(TagsZT.Items.GEARS_STONE);
	
	@RegistryName("copper")
	Item COPPER_GEAR = BaseZT.newItem(TagsZT.Items.GEARS_COPPER);
	
	@RegistryName("iron")
	Item IRON_GEAR = BaseZT.newItem(TagsZT.Items.GEARS_IRON);
	
	@RegistryName("tin")
	Item TIN_GEAR = BaseZT.newItem(TagsZT.Items.GEARS_TIN);
	
	@RegistryName("silver")
	Item SILVER_GEAR = BaseZT.newItem(TagsZT.Items.GEARS_SILVER);
	
	@RegistryName("gold")
	Item GOLD_GEAR = BaseZT.newItem(TagsZT.Items.GEARS_GOLD);
	
	@RegistryName("diamond")
	Item DIAMOND_GEAR = BaseZT.newItem(TagsZT.Items.GEARS_DIAMOND);
	
	@RegistryName("tungsten")
	Item TUNGSTEN_GEAR = BaseZT.newItem(TagsZT.Items.GEARS_TUNGSTEN);
	
	@RegistryName("netherite")
	Item NETHERITE_GEAR = BaseZT.newItem(TagsZT.Items.GEARS_NETHERITE);
	
	static void recipes(RegisterRecipesEvent e)
	{
		e.shaped().shape(" s ", "sps", " s ").map('s', Tags.Items.RODS_WOODEN).map('p', ItemTags.PLANKS).result(WOODEN_GEAR).register();
		e.shaped().shape(" s ", "sgs", " s ").map('s', Tags.Items.COBBLESTONE).map('g', WOODEN_GEAR).result(STONE_GEAR).register();
		e.shaped().shape(" s ", "sgs", " s ").map('s', TagsZT.Items.PLATES_COPPER).map('g', STONE_GEAR).result(COPPER_GEAR).register();
		e.shaped().shape(" s ", "sgs", " s ").map('s', TagsZT.Items.PLATES_IRON).map('g', COPPER_GEAR).result(IRON_GEAR).register();
		
		e.shaped().shape(" s ", "sgs", " s ").map('s', TagsZT.Items.PLATES_TIN).map('g', STONE_GEAR).result(TIN_GEAR).register();
		e.shaped().shape(" s ", "sgs", " s ").map('s', TagsZT.Items.PLATES_SILVER).map('g', IRON_GEAR).result(SILVER_GEAR).register();
		
		e.shaped().shape(" s ", "sgs", " s ").map('s', TagsZT.Items.PLATES_GOLD).map('g', IRON_GEAR).result(GOLD_GEAR).register();
		e.shaped().shape(" s ", "sgs", " s ").map('s', Tags.Items.GEMS_DIAMOND).map('g', GOLD_GEAR).result(DIAMOND_GEAR).register();
		e.shaped().shape(" s ", "sgs", " s ").map('s', TagsZT.Items.PLATES_TUNGSTEN).map('g', DIAMOND_GEAR).result(TUNGSTEN_GEAR).register();
		
		var id = ForgeRegistries.ITEMS.getKey(NETHERITE_GEAR);
		e.register(id, new UpgradeRecipe(id, Ingredient.of(TUNGSTEN_GEAR), RecipeHelper.fromTag(Tags.Items.INGOTS_NETHERITE), new ItemStack(NETHERITE_GEAR)));
	}
}