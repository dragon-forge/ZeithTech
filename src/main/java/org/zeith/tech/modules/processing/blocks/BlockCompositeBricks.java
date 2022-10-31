package org.zeith.tech.modules.processing.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ForgeRegistries;
import org.zeith.hammerlib.core.adapter.BlockHarvestAdapter;
import org.zeith.hammerlib.core.adapter.TagAdapter;
import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.api.block.multiblock.blast_furnace.IBlastFurnaceCasingBlock;
import org.zeith.tech.modules.processing.init.BlocksZT_Processing;
import org.zeith.tech.modules.shared.blocks.SimpleBlockZT;
import org.zeith.tech.modules.shared.blocks.multiblock_part.TileMultiBlockPart;
import org.zeith.tech.modules.shared.init.ItemsZT;
import org.zeith.tech.modules.shared.init.TagsZT;

public class BlockCompositeBricks
		extends SimpleBlockZT
		implements IBlastFurnaceCasingBlock
{
	protected final BlastFurnaceTier tier;
	protected final float tempLoss, reflectivity;
	
	protected final BlockState brokenVersion;
	
	public BlockCompositeBricks(BlockCompositeBricks brokenVersion, BlastFurnaceTier tier, float tempLoss, float reflectivity)
	{
		super(BlockBehaviour.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1.5F).sound(SoundType.STONE), BlockHarvestAdapter.MineableType.PICKAXE, Tiers.STONE);
		this.brokenVersion = brokenVersion != null ? brokenVersion.defaultBlockState() : defaultBlockState();
		this.tier = tier;
		this.tempLoss = tempLoss;
		this.reflectivity = reflectivity;
		TagAdapter.bind(TagsZT.Blocks.COMPOSITE_BRICKS, this);
		dropsSelf();
	}
	
	protected final LazyOptional<BlockState> repairedState = LazyOptional.of(() ->
	{
		for(Block block : ForgeRegistries.BLOCKS)
			if(block instanceof BlockCompositeBricks comp && comp.getDamagedState(comp.defaultBlockState()).getBlock() == this)
				return comp.defaultBlockState();
		return defaultBlockState();
	});
	
	public BlockState getRepairedState()
	{
		return this == BlocksZT_Processing.BROKEN_COMPOSITE_BRICKS ? BlocksZT_Processing.DAMAGED_COMPOSITE_BRICKS.defaultBlockState() : repairedState.orElse(defaultBlockState());
	}
	
	@Override
	public InteractionResult useAsPart(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, TileMultiBlockPart partTile)
	{
		var held = player.getItemInHand(hand);
		if(held.getItem() == ItemsZT.COMPOSITE_BRICK)
		{
			var repaired = getRepairedState();
			if(repaired.getBlock() != this)
			{
				partTile.setSubState(repaired);
				
				if(!player.getAbilities().instabuild)
					held.shrink(1);
				
				if(!level.isClientSide)
					ZeithTechAPI.get().getAudioSystem().playPositionedSound(level, pos, SoundEvents.POINTED_DRIPSTONE_HIT, 0.25F, 1F);
				
				return InteractionResult.SUCCESS;
			}
		}
		
		return InteractionResult.PASS;
	}
	
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
	{
		var held = player.getItemInHand(hand);
		if(held.getItem() == ItemsZT.COMPOSITE_BRICK)
		{
			var repaired = getRepairedState();
			if(repaired != null && repaired.getBlock() != this)
			{
				level.setBlockAndUpdate(pos, repaired);
				
				if(!player.getAbilities().instabuild)
					held.shrink(1);
				
				if(!level.isClientSide)
					ZeithTechAPI.get().getAudioSystem().playPositionedSound(level, pos, SoundEvents.POINTED_DRIPSTONE_HIT, 0.25F, 1F);
				
				return InteractionResult.SUCCESS;
			}
		}
		
		return super.use(state, level, pos, player, hand, hit);
	}
	
	@Override
	public BlockState getDamagedState(BlockState state)
	{
		return brokenVersion;
	}
	
	@Override
	public BlastFurnaceTier getBlastFurnaceTier(Level level, BlockPos pos, BlockState state)
	{
		return tier;
	}
	
	@Override
	public float getTemperatureLoss(Level level, BlockPos pos, BlockState state)
	{
		return tempLoss;
	}
	
	@Override
	public float getTemperatureReflectivityCoef(Level level, BlockPos pos, BlockState state)
	{
		return reflectivity;
	}
}
