package org.zeith.tech.modules.world.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.tech.modules.world.blocks.tap.TileHeveaTreeTap;

import java.util.Set;

@SimplyRegister
public interface TilesZT_World
{
	@RegistryName("hevea_tree_tap")
	BlockEntityType<TileHeveaTreeTap> HEVEA_TREE_TAP = new BlockEntityType<>(TileHeveaTreeTap::new, Set.of(BlocksZT_World.TREE_TAP), null);
}