package org.zeith.tech.modules.shared.init;

import net.minecraftforge.common.Tags;
import org.zeith.hammerlib.event.recipe.RegisterRecipesEvent;
import org.zeith.hammerlib.event.recipe.ReloadRecipeRegistryEvent;
import org.zeith.tech.api.enums.TechTier;
import org.zeith.tech.api.recipes.processing.RecipeMachineAssembler;
import org.zeith.tech.modules.processing.init.RecipeRegistriesZT_Processing;
import org.zeith.tech.modules.transport.init.BlocksZT_Transport;

public interface RecipesZT
{
	static void provideRecipes(RegisterRecipesEvent event)
	{
		event.shaped().shape("wsw", "wsw", "wsw").map('w', BlocksZT_Transport.UNINSULATED_COPPER_WIRE).map('s', Tags.Items.RODS_WOODEN).result(ItemsZT.COPPER_COIL).register();
		event.shaped().shape("wsw", "wsw", "wsw").map('w', BlocksZT_Transport.UNINSULATED_GOLD_WIRE).map('s', Tags.Items.RODS_WOODEN).result(ItemsZT.GOLD_COIL).register();
		event.shaped().shape("icc", "iii", "icc").map('i', Tags.Items.INGOTS_IRON).map('c', ItemsZT.COPPER_COIL).result(ItemsZT.MOTOR).register();
		event.shaped().shape("ppp", "p p", "ppp").map('p', TagsZT.Items.PLATES_IRON).result(ItemsZT.CIRCULAR_SAW).register();
	}
	
	static void machineAssembler(ReloadRecipeRegistryEvent.AddRecipes<RecipeMachineAssembler> e)
	{
		if(e.is(RecipeRegistriesZT_Processing.MACHINE_ASSEBMLY))
		{
			var b = e.<RecipeMachineAssembler.Builder> builderFactory();
			
			b.get().minTier(TechTier.BASIC)
					.shape("  m  ", " ggg ", "pibip", " fef ", "  m  ")
					.map('m', ItemsZT.MOTOR)
					.map('g', TagsZT.Items.INGOTS_ALUMINUM)
					.map('p', TagsZT.Items.PLATES_ALUMINUM)
					.map('i', BlocksZT_Transport.COPPER_ITEM_PIPE)
					.map('b', TagsZT.Items.STORAGE_BLOCKS_SILVER)
					.map('f', BlocksZT_Transport.IRON_FLUID_PIPE)
					.map('e', BlocksZT_Transport.INSULATED_ALUMINUM_WIRE)
					.result(BlocksZT.AUXILIARY_IO_PORT)
					.register();
		}
	}
}