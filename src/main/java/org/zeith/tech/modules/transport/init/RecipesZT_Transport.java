package org.zeith.tech.modules.transport.init;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags;
import org.zeith.hammerlib.event.recipe.RegisterRecipesEvent;
import org.zeith.tech.modules.shared.init.TagsZT;

public interface RecipesZT_Transport
{
	static void provideRecipes(RegisterRecipesEvent event)
	{
		event.shaped().shape("pgp").map('p', TagsZT.Items.PLATES_COPPER).map('g', Tags.Items.GLASS).result(new ItemStack(BlocksZT_Transport.COPPER_ITEM_PIPE, 3)).register();
	}
}