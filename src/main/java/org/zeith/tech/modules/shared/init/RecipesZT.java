package org.zeith.tech.modules.shared.init;

import net.minecraftforge.common.Tags;
import org.zeith.hammerlib.event.recipe.RegisterRecipesEvent;
import org.zeith.tech.modules.transport.init.BlocksZT_Transport;

public interface RecipesZT
{
	static void provideRecipes(RegisterRecipesEvent event)
	{
		event.shaped().shape("wsw", "wsw", "wsw").map('w', BlocksZT_Transport.UNINSULATED_COPPER_WIRE).map('s', Tags.Items.RODS_WOODEN).result(ItemsZT.COPPER_COIL).register();
		event.shaped().shape("wsw", "wsw", "wsw").map('w', BlocksZT_Transport.UNINSULATED_GOLD_WIRE).map('s', Tags.Items.RODS_WOODEN).result(ItemsZT.GOLD_COIL).register();
		event.shaped().shape("icc", "iii", "icc").map('i', Tags.Items.INGOTS_IRON).map('c', ItemsZT.COPPER_COIL).result(ItemsZT.MOTOR).register();
	}
}