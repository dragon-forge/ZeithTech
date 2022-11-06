package org.zeith.tech.modules.shared.init;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.hammerlib.core.adapter.BlockHarvestAdapter;
import org.zeith.hammerlib.core.init.TagsHL;
import org.zeith.tech.api.item.IBurnableItem;
import org.zeith.tech.modules.generators.init.BlocksZT_Generators;
import org.zeith.tech.modules.processing.init.BlocksZT_Processing;
import org.zeith.tech.modules.shared.BaseZT;
import org.zeith.tech.modules.shared.blocks.*;
import org.zeith.tech.modules.shared.blocks.aux_io_port.BlockAuxiliaryIOPort;
import org.zeith.tech.modules.shared.blocks.multiblock_part.BlockMultiBlockPart;
import org.zeith.tech.modules.transport.init.BlocksZT_Transport;
import org.zeith.tech.modules.world.init.BlocksZT_World;

import java.util.List;

@SimplyRegister
public interface BlocksZT
		extends BlocksZT_World, BlocksZT_Generators, BlocksZT_Transport, BlocksZT_Processing
{
	@RegistryName("auxiliary_io_port")
	BlockAuxiliaryIOPort AUXILIARY_IO_PORT = new BlockAuxiliaryIOPort();
	
	@RegistryName("multiblock_part")
	BlockMultiBlockPart MULTIBLOCK_PART = new BlockMultiBlockPart(Block.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(5F));
	
	@RegistryName("masut")
	Block MASUT = new BlockMasut(BlockBehaviour.Properties.of(Material.DIRT).sound(SoundType.MUDDY_MANGROVE_ROOTS).strength(0.5F), BlockHarvestAdapter.MineableType.SHOVEL).burnable(IBurnableItem.constantBurnTime(200 * 80)).dropsSelf();
	
	@RegistryName("pure_sand")
	SandBlockZT PURE_SAND = new SandBlockZT(0xEDFCFF, BlockBehaviour.Properties.of(Material.SAND, MaterialColor.SAND).strength(0.5F).sound(SoundType.SAND)).addBlockTags(List.of(BlockTags.MINEABLE_WITH_SHOVEL, BlockTags.SAND)).dropsSelf();
	
	@RegistryName("bioluminescent_block")
	SimpleBlockZT BIOLUMINESCENT_BLOCK = new SimpleBlockZT(BlockBehaviour.Properties.of(Material.GLASS, MaterialColor.SAND).strength(0.3F).sound(SoundType.GLASS).lightLevel(state -> 15))
			.addItemTag(TagsHL.Items.STORAGE_BLOCKS_GLOWSTONE);
	
	@RegistryName("plastic_casing")
	SimpleWaterLoggableBlockZT PLASTIC_CASING = new SimpleWaterLoggableBlockZT(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.STONE).noOcclusion().isViewBlocking(BaseZT::never).strength(0.5F).sound(SoundType.STONE)).dropsSelf();
}