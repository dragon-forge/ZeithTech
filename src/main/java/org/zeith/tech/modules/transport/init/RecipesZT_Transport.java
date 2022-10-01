package org.zeith.tech.modules.transport.init;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags;
import org.zeith.hammerlib.event.recipe.RegisterRecipesEvent;
import org.zeith.tech.modules.processing.init.ItemsZT_Processing;
import org.zeith.tech.modules.shared.init.ItemsZT;
import org.zeith.tech.modules.shared.init.TagsZT;

public interface RecipesZT_Transport
{
	static void provideRecipes(RegisterRecipesEvent event)
	{
		event.shapeless().add(BlocksZT_Transport.UNINSULATED_COPPER_WIRE).add(BlocksZT_Transport.UNINSULATED_COPPER_WIRE).add(BlocksZT_Transport.UNINSULATED_COPPER_WIRE).add(ItemsZT.LATEX).add(ItemsZT.LATEX).result(new ItemStack(BlocksZT_Transport.INSULATED_COPPER_WIRE, 3)).register();
		event.shapeless().add(BlocksZT_Transport.UNINSULATED_ALUMINUM_WIRE).add(BlocksZT_Transport.UNINSULATED_ALUMINUM_WIRE).add(BlocksZT_Transport.UNINSULATED_ALUMINUM_WIRE).add(ItemsZT.LATEX).add(ItemsZT.LATEX).result(new ItemStack(BlocksZT_Transport.INSULATED_ALUMINUM_WIRE, 3)).register();
		event.shapeless().add(BlocksZT_Transport.UNINSULATED_GOLD_WIRE).add(BlocksZT_Transport.UNINSULATED_GOLD_WIRE).add(BlocksZT_Transport.UNINSULATED_GOLD_WIRE).add(ItemsZT.LATEX).add(ItemsZT.LATEX).result(new ItemStack(BlocksZT_Transport.INSULATED_GOLD_WIRE, 3)).register();
		
		event.shaped().shape("pgp").map('p', TagsZT.Items.PLATES_COPPER).map('g', Tags.Items.GLASS).result(new ItemStack(BlocksZT_Transport.COPPER_ITEM_PIPE, 3)).register();
		
		event.shapeless().add(ItemsZT_Processing.WIRE_CUTTER).add(TagsZT.Items.PLATES_COPPER).result(new ItemStack(BlocksZT_Transport.UNINSULATED_COPPER_WIRE, 3)).register();
		event.shapeless().add(ItemsZT_Processing.WIRE_CUTTER).add(TagsZT.Items.PLATES_ALUMINUM).result(new ItemStack(BlocksZT_Transport.UNINSULATED_ALUMINUM_WIRE, 3)).register();
		event.shapeless().add(ItemsZT_Processing.WIRE_CUTTER).add(TagsZT.Items.PLATES_GOLD).result(new ItemStack(BlocksZT_Transport.UNINSULATED_GOLD_WIRE, 3)).register();
	}
}