package org.zeith.tech.modules.world.init;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.tech.modules.world.entity.BoatZT;
import org.zeith.tech.modules.world.entity.ChestBoatZT;

@SimplyRegister
public interface EntitiesZT_World
{
	@RegistryName("boat")
	EntityType<BoatZT> BOAT = EntityType.Builder.<BoatZT> of(BoatZT::new, MobCategory.MISC).sized(1.375F, 0.5625F).clientTrackingRange(10).build("boat");
	
	@RegistryName("chest_boat")
	EntityType<ChestBoatZT> CHEST_BOAT = EntityType.Builder.<ChestBoatZT> of(ChestBoatZT::new, MobCategory.MISC).sized(1.375F, 0.5625F).clientTrackingRange(10).build("chest_boat");
}