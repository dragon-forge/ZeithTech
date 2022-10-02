package org.zeith.tech.modules.shared.init;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags;
import org.zeith.hammerlib.event.recipe.RegisterRecipesEvent;
import org.zeith.tech.modules.transport.init.BlocksZT_Transport;

public interface RecipesZT
{
	static void provideRecipes(RegisterRecipesEvent event)
	{
		event.shaped().shape("wsw", "wsw", "wsw").map('w', BlocksZT_Transport.UNINSULATED_COPPER_WIRE).map('s', Tags.Items.RODS_WOODEN).result(new ItemStack(ItemsZT.COPPER_COIL)).register();
		event.shaped().shape("wsw", "wsw", "wsw").map('w', BlocksZT_Transport.UNINSULATED_GOLD_WIRE).map('s', Tags.Items.RODS_WOODEN).result(new ItemStack(ItemsZT.GOLD_COIL)).register();
	}
}