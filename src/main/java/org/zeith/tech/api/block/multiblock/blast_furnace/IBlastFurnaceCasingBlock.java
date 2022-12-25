package org.zeith.tech.api.block.multiblock.blast_furnace;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.zeith.tech.api.block.multiblock.base.IMultiBlockElement;

/**
 * An interface for blocks that can be used as casing for a blast furnace multi-block structure.
 */
public interface IBlastFurnaceCasingBlock
		extends IMultiBlockElement
{
	/**
	 * Returns the tier of the blast furnace that this block is capable of.
	 *
	 * @param level
	 * 		The level in which the block is located.
	 * @param pos
	 * 		The position of the block.
	 * @param state
	 * 		The state of the block.
	 *
	 * @return The tier of the blast furnace.
	 */
	BlastFurnaceTier getBlastFurnaceTier(Level level, BlockPos pos, BlockState state);
	
	/**
	 * Returns the amount of heat loss through this block.
	 *
	 * @param level
	 * 		The level in which the block is located.
	 * @param pos
	 * 		The position of the block.
	 * @param state
	 * 		The state of the block.
	 *
	 * @return The amount of heat loss through this block.
	 */
	float getTemperatureLoss(Level level, BlockPos pos, BlockState state);
	
	/**
	 * Returns the reflectivity coefficient of this block.
	 *
	 * @param level
	 * 		The level in which the block is located.
	 * @param pos
	 * 		The position of the block.
	 * @param state
	 * 		The state of the block.
	 *
	 * @return The reflectivity coefficient of this block.
	 */
	float getTemperatureReflectivityCoef(Level level, BlockPos pos, BlockState state);
	
	/**
	 * Returns the block state that will be applied.
	 *
	 * @param level
	 * 		The level in which the block is located.
	 * @param pos
	 * 		The position of the block.
	 * @param state
	 * 		The state of the block.
	 * @param rng
	 * 		The random number generator to use.
	 *
	 * @return The modified block state.
	 */
	default BlockState crackRandomly(ServerLevel level, BlockPos pos, BlockState state, RandomSource rng)
	{
		return rng.nextInt(getBreakRarity()) == 0 ? getDamagedState(state) : state;
	}
	
	/**
	 * Returns the damaged version of the given block state.
	 *
	 * @param state
	 * 		The block state to modify.
	 *
	 * @return The damaged version of the block state.
	 */
	default BlockState getDamagedState(BlockState state)
	{
		return state;
	}
	
	/**
	 * Returns the sound to play when the block is damaged.
	 *
	 * @return The sound to play when the block is damaged.
	 */
	default SoundEvent getCasingDamageSound()
	{
		return SoundEvents.TURTLE_EGG_CRACK;
	}
	
	/**
	 * Returns the rarity at which the block will break. Higher values make the cracking take longer.
	 *
	 * @return The rarity at which the block will break.
	 */
	default int getBreakRarity()
	{
		return 10;
	}
	
	/**
	 * An enumeration of blast furnace tiers.
	 */
	enum BlastFurnaceTier
	{
		/**
		 * The basic tier of blast furnace.
		 */
		BASIC,
		
		/**
		 * The electric tier of blast furnace.
		 */
		ELECTRIC;
	}
}