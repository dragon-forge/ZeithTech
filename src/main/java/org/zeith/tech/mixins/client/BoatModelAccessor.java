package org.zeith.tech.mixins.client;

import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BoatModel.class)
public interface BoatModelAccessor
{
	@Accessor
	ModelPart getLeftPaddle();
	
	@Accessor
	ModelPart getRightPaddle();
}
