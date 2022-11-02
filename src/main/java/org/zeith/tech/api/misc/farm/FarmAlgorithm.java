package org.zeith.tech.api.misc.farm;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.zeith.hammerlib.client.utils.UV;
import org.zeith.tech.api.ZeithTechAPI;

import java.util.Objects;

public abstract class FarmAlgorithm
{
	private final LazyOptional<ResourceLocation> cachedRegistryName = LazyOptional.of(() -> Objects.requireNonNull(ZeithTechAPI.get().getFarmAlgorithms().getKey(this)));
	
	private final boolean upsideDown;
	
	public FarmAlgorithm(FarmAlgorithm.Properties properties)
	{
		this.upsideDown = properties.upsideDown;
	}
	
	public boolean isUpsideDown()
	{
		return upsideDown;
	}
	
	public final ResourceLocation getRegistryName()
	{
		return cachedRegistryName.orElse(null);
	}
	
	private String descriptionId;
	
	public MutableComponent getDisplayName()
	{
		if(descriptionId == null)
			descriptionId = Util.makeDescriptionId("farm_algorithm", getRegistryName());
		return Component.translatable(descriptionId);
	}
	
	public UV getIcon()
	{
		var reg = getRegistryName();
		return new UV(
				new ResourceLocation(reg.getNamespace(), "textures/gui/farm_algorithms/" + reg.getPath() + ".png"),
				0, 0,
				256, 256
		);
	}
	
	public int getColor()
	{
		return getRegistryName().hashCode();
	}
	
	/**
	 * Provide an ingredient that should be matched in order to create a farm chip with this algorithm.
	 */
	@NotNull
	public abstract Ingredient getProgrammingItem();
	
	/**
	 * Requests this algorithm to figure out, where any given item may be inserted.
	 * If the item does not fit into any category, return {@link EnumFarmItemCategory#UNKNOWN}.
	 *
	 * @return The known item category for any given item, or {@link EnumFarmItemCategory#UNKNOWN} otherwise.
	 */
	@NotNull
	public abstract EnumFarmItemCategory categorizeItem(IFarmController controller, ItemStack stack);
	
	/**
	 * Perform a single update for on given farm at the platform position.
	 * This logic should perform PLANT and HARVEST operations.
	 * Harvesting should be done by calling {@link IFarmController#queueBlockHarvest(BlockPos, int)}.
	 * Placement should be done by calling {@link IFarmController#queueBlockPlacement(FarmItemConsumer, BlockPos, BlockState, int, int)}.
	 * Soil conversion should be done by calling {@link IFarmController#queueBlockTransformation(BlockPos, BlockState, BlockState, int, int)}.
	 * {@link FarmItemConsumer} is obtained using {@link IFarmController#createItemConsumer(EnumFarmItemCategory, ItemStack)} to consume an item.
	 *
	 * @return true if the farm should go on an update cool-down (and drain energy), before handing another update to next position; false otherwise.
	 */
	public abstract AlgorithmUpdateResult handleUpdate(IFarmController controller, ServerLevel level, BlockPos platform);
	
	/**
	 * Attempts to fertilize the plant on given platform position.
	 *
	 * @return true to consume the fertilizer and drain energy, false otherwise.
	 */
	public abstract boolean tryFertilize(IFarmController controller, ServerLevel level, BlockPos platform);
	
	public static class Properties
	{
		private boolean upsideDown = false;
		
		public Properties()
		{
		}
		
		public Properties upsideDown(boolean upsideDown)
		{
			this.upsideDown = upsideDown;
			return this;
		}
	}
}