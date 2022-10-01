package org.zeith.tech.mixins.client;

import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.ChestType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zeith.tech.ZeithTech;
import org.zeith.tech.modules.world.init.BlocksZT_World;

import java.util.function.Consumer;

@Mixin(Sheets.class)
public abstract class SheetsMixin
{
	@Shadow
	private static Material chooseMaterial(ChestType p_110772_, Material p_110773_, Material p_110774_, Material p_110775_)
	{
		return null;
	}
	
	private static final ResourceLocation CHEST_SHEET = new ResourceLocation("textures/atlas/chest.png");
	
	@Inject(method = "getAllMaterials", at = @At("TAIL"))
	private static void getAllMaterialsZT(Consumer<Material> mat, CallbackInfo ci)
	{
		mat.accept(HEVEA_CHEST_TRAP_LOCATION);
		mat.accept(HEVEA_CHEST_TRAP_LOCATION_LEFT);
		mat.accept(HEVEA_CHEST_TRAP_LOCATION_RIGHT);
		mat.accept(HEVEA_CHEST_LOCATION);
		mat.accept(HEVEA_CHEST_LOCATION_LEFT);
		mat.accept(HEVEA_CHEST_LOCATION_RIGHT);
	}
	
	@Inject(method = "chooseMaterial(Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/level/block/state/properties/ChestType;Z)Lnet/minecraft/client/resources/model/Material;", at = @At("HEAD"), cancellable = true)
	private static void chooseMaterialZT(BlockEntity blockEntity, ChestType chestType, boolean xmas, CallbackInfoReturnable<Material> cir)
	{
		if(!xmas)
		{
			var bs = blockEntity.getBlockState();
			
			if(bs.is(BlocksZT_World.HEVEA_CHEST))
				cir.setReturnValue(chooseMaterial(chestType, HEVEA_CHEST_LOCATION, HEVEA_CHEST_LOCATION_LEFT, HEVEA_CHEST_LOCATION_RIGHT));
			
			if(bs.is(BlocksZT_World.HEVEA_TRAPPED_CHEST))
				cir.setReturnValue(chooseMaterial(chestType, HEVEA_CHEST_TRAP_LOCATION, HEVEA_CHEST_TRAP_LOCATION_LEFT, HEVEA_CHEST_TRAP_LOCATION_RIGHT));
		}
	}
	
	private static final Material HEVEA_CHEST_TRAP_LOCATION = chestMaterialZT("hevea_trapped");
	private static final Material HEVEA_CHEST_TRAP_LOCATION_LEFT = chestMaterialZT("hevea_trapped_left");
	private static final Material HEVEA_CHEST_TRAP_LOCATION_RIGHT = chestMaterialZT("hevea_trapped_right");
	private static final Material HEVEA_CHEST_LOCATION = chestMaterialZT("hevea_normal");
	private static final Material HEVEA_CHEST_LOCATION_LEFT = chestMaterialZT("hevea_normal_left");
	private static final Material HEVEA_CHEST_LOCATION_RIGHT = chestMaterialZT("hevea_normal_right");
	
	private static Material chestMaterialZT(String alt)
	{
		return new Material(CHEST_SHEET, new ResourceLocation(ZeithTech.MOD_ID, "entity/chest/" + alt));
	}
}