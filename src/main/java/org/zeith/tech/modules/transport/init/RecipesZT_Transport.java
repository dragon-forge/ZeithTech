package org.zeith.tech.modules.transport.init;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags;
import org.zeith.hammerlib.event.recipe.RegisterRecipesEvent;
import org.zeith.tech.modules.processing.init.ItemsZT_Processing;
import org.zeith.tech.modules.shared.init.*;

public interface RecipesZT_Transport
{
	static void provideRecipes(RegisterRecipesEvent event)
	{
		event.shapeless().add(BlocksZT.UNINSULATED_COPPER_WIRE).add(BlocksZT.UNINSULATED_COPPER_WIRE).add(BlocksZT.UNINSULATED_COPPER_WIRE).add(ItemsZT.LATEX).add(ItemsZT.LATEX).result(new ItemStack(BlocksZT_Transport.INSULATED_COPPER_WIRE, 3)).register();
		event.shapeless().add(BlocksZT.UNINSULATED_ALUMINUM_WIRE).add(BlocksZT.UNINSULATED_ALUMINUM_WIRE).add(BlocksZT.UNINSULATED_ALUMINUM_WIRE).add(ItemsZT.LATEX).add(ItemsZT.LATEX).result(new ItemStack(BlocksZT_Transport.INSULATED_ALUMINUM_WIRE, 3)).register();
		event.shapeless().add(BlocksZT.UNINSULATED_GOLD_WIRE).add(BlocksZT.UNINSULATED_GOLD_WIRE).add(BlocksZT.UNINSULATED_GOLD_WIRE).add(ItemsZT.LATEX).add(ItemsZT.LATEX).result(new ItemStack(BlocksZT_Transport.INSULATED_GOLD_WIRE, 3)).register();
		event.shapeless().add(BlocksZT.UNINSULATED_SILVER_WIRE).add(BlocksZT.UNINSULATED_SILVER_WIRE).add(BlocksZT.UNINSULATED_SILVER_WIRE).add(ItemsZT.LATEX).add(ItemsZT.LATEX).result(new ItemStack(BlocksZT_Transport.INSULATED_SILVER_WIRE, 3)).register();
		
		event.shaped().shape("pgp").map('p', TagsZT.Items.PLATES_COPPER).map('g', Tags.Items.GLASS).result(new ItemStack(BlocksZT_Transport.COPPER_ITEM_PIPE, 3)).register();
		event.shaped().shape("rpb", "pwp", " p ").map('r', Tags.Items.DYES_RED).map('p', ItemsZT.PLASTIC).map('b', Tags.Items.DYES_BLACK).map('w', BlocksZT.INSULATED_COPPER_WIRE).result(ItemsZT_Transport.MULTIMETER).register();
		
		event.shaped().shape("mmm", "ggg", "mmm").map('m', ItemTags.PLANKS).map('g', Tags.Items.GLASS).result(new ItemStack(BlocksZT_Transport.WOODEN_FLUID_PIPE, 6)).register();
		event.shaped().shape("mmm", "ggg", "mmm").map('m', TagsZT.Items.PLATES_IRON).map('g', Tags.Items.GLASS).result(new ItemStack(BlocksZT_Transport.IRON_FLUID_PIPE, 6)).register();
		
		event.shapeless().add(ItemsZT_Processing.WIRE_CUTTER).add(TagsZT.Items.PLATES_COPPER).result(new ItemStack(BlocksZT_Transport.UNINSULATED_COPPER_WIRE, 3)).register();
		event.shapeless().add(ItemsZT_Processing.WIRE_CUTTER).add(TagsZT.Items.PLATES_ALUMINUM).result(new ItemStack(BlocksZT_Transport.UNINSULATED_ALUMINUM_WIRE, 3)).register();
		event.shapeless().add(ItemsZT_Processing.WIRE_CUTTER).add(TagsZT.Items.PLATES_GOLD).result(new ItemStack(BlocksZT_Transport.UNINSULATED_GOLD_WIRE, 3)).register();
		event.shapeless().add(ItemsZT_Processing.WIRE_CUTTER).add(TagsZT.Items.PLATES_SILVER).result(new ItemStack(BlocksZT_Transport.UNINSULATED_SILVER_WIRE, 3)).register();
		
		event.shaped().shape("pip", "gGg", "pip").map('p', TagsZT.Items.PLATES_ALUMINUM).map('i', TagsZT.Items.INGOTS_ALUMINUM).map('g', Tags.Items.GLASS_PANES).map('G', Tags.Items.GLASS).result(BlocksZT_Transport.BASIC_FLUID_TANK).register();
	}
}