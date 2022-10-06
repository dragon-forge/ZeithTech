package org.zeith.tech.modules.world.worldgen.oil;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.Vec3;
import org.zeith.tech.modules.world.init.FluidsZT_World;
import org.zeith.tech.utils.XYZMap;

import java.util.function.Function;

public class OilLakeFeature
		extends Feature<OilLakeFeature.OilLakeConfiguration>
{
	private static final BlockState AIR = Blocks.CAVE_AIR.defaultBlockState();
	
	public OilLakeFeature(Codec<OilLakeConfiguration> codec)
	{
		super(codec);
	}
	
	public final UniformFloat ySquishSampler = UniformFloat.of(0.45F, 1.2F);
	
	@Override
	public boolean place(FeaturePlaceContext<OilLakeConfiguration> context)
	{
		RandomSource rng = context.random();
		
		var config = context.config();
		var size = config.type;
		
		int radius = switch(size)
				{
					default -> 4;
					case MEDIUM -> 7;
					case LARGE -> 12;
				};
		
		Function<BlockPos, FeaturePlaceContext<OilLakeConfiguration>> reposition = pos -> new FeaturePlaceContext<>(context.topFeature(), context.level(), context.chunkGenerator(), context.random(), pos, context.config());
		
		float rate = config.floodAmount().sample(rng);
		
		float squish = ySquishSampler.sample(rng);
		
		BlockPos origin = context.origin();
		
		boolean generated = placeNewPond(radius, rate, squish, rate > 1F, context);
		if(!generated)
			generated = placeNewPond(radius, rate, squish, rate > 1F, reposition.apply(origin = context.origin().offset(-radius * 2, 0, -radius * 2)));
		
		if(generated && size == LakeType.LARGE)
		{
			var center = origin.offset(radius, radius * squish, radius);
			
			int extraPonds = rng.nextInt(7) + 5;
			for(int i = 0; i < extraPonds; ++i)
			{
				int subRadius = radius;
				subRadius *= Mth.clamp(rng.nextFloat() + 0.25F, 0F, 0.75F);
				
				var pos = new Vec3(rng.nextGaussian(), rng.nextGaussian() - 1F, rng.nextGaussian()).normalize();
				
				pos = pos.scale(radius - 1);
				
				// Subtract the sub radius to make the pond's center position be close to the main lake.
				var finalPos = center.offset(
						pos.x - subRadius,
						pos.y - subRadius,
						pos.z - subRadius
				);
				
				placeNewPond(radius, rate, ySquishSampler.sample(rng) + ySquishSampler.sample(rng), false, reposition.apply(finalPos));
			}
		}
		
		return generated;
	}
	
	public boolean placeNewPond(int radius, float fillRate, float ySquish, boolean fountain, FeaturePlaceContext<OilLakeConfiguration> context)
	{
		BlockPos origin = context.origin();
		WorldGenLevel level = context.level();
		RandomSource rng = context.random();
		
		if(origin.getY() <= level.getMinBuildHeight() + radius / 2 + 2)
			return false;
		
		XYZMap<Boolean> replaces = new XYZMap<>(false);
		
		BlockState fluidState = FluidsZT_World.CRUDE_OIL.getSourceBlockState();
		
		int radiusSq = radius * radius;
		float ySquishInv = 1 / ySquish;
		
		// Calculate the base sphere with all positions that we place
		for(int x = -radius; x < radius; ++x)
			for(int y = -radius; y < radius; ++y)
				for(int z = -radius; z < radius; ++z)
					if(x * x + y * y * ySquishInv + z * z <= radiusSq)
						replaces.put(x, y, z, true);
		
		float squishedFill = fillRate * radius * ySquish;
		boolean full = fillRate >= 1F;
		
		// Ensure we can place/clear out all blocks
		for(int xOff = -radius; xOff < radius; ++xOff)
			for(int yOff = -radius; yOff < radius; ++yOff)
				for(int zOff = -radius; zOff < radius; ++zOff)
				{
					boolean gen = replaces.getOrDefault(xOff, yOff, zOff, false);
					if(gen)
					{
						var pos = origin.offset(xOff + radius, yOff + radius, zOff + radius);
						if(!canWrite(level, pos))
							return false;
						
						var state = level.getBlockState(pos);
						var material = state.getMaterial();
						
						if((!full && yOff >= squishedFill) && material.isLiquid())
							return false;
						
						if(yOff < squishedFill && !material.isSolid() && state != fluidState)
							return false;
					}
				}
		
		BlockPos fountainCords = origin.offset(radius, radius, radius);
		int fountainHeight = -1;
		
		if(fountain)
		{
			fountainHeight = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, fountainCords.getX(), fountainCords.getZ())
					- fountainCords.getY()
					+ 15 + rng.nextInt(3); // add extra fluid pouring from the ground
			
			for(int yOff = 0; yOff < fountainHeight; ++yOff)
			{
				var placePos = fountainCords.above(yOff);
				if(!canWrite(level, placePos) || !this.canReplaceBlock(level.getBlockState(placePos)))
					return false;
			}
		}
		
		// Place all
		for(int xOff = -radius; xOff < radius; ++xOff)
			for(int yOff = -radius; yOff < radius; ++yOff)
				for(int zOff = -radius; zOff < radius; ++zOff)
				{
					if(replaces.getOrDefault(xOff, yOff, zOff, false))
					{
						BlockPos pos = origin.offset(xOff + radius, yOff + radius, zOff + radius);
						if(this.canReplaceBlock(level.getBlockState(pos)))
						{
							boolean shouldClear = !full && yOff >= squishedFill;
							level.setBlock(pos, shouldClear ? AIR : fluidState, 2);
							if(shouldClear)
							{
								level.scheduleTick(pos, AIR.getBlock(), 0);
								this.markAboveForPostProcessing(level, pos);
							} else
								level.scheduleTick(pos, FluidsZT_World.CRUDE_OIL.getSource(), 1);
						}
					}
				}
		
		if(fountain)
			for(int yOff = 0; yOff < fountainHeight; ++yOff)
			{
				var placePos = fountainCords.above(yOff);
				if(level.getBlockState(placePos).is(BlockTags.LAVA_POOL_STONE_CANNOT_REPLACE))
					break;
				
				level.setBlock(placePos, fluidState, 3);
				level.scheduleTick(placePos, FluidsZT_World.CRUDE_OIL.getSource(), 1);
			}
		
		return true;
	}
	
	public boolean placeLegacySmallPond(FeaturePlaceContext<OilLakeConfiguration> context)
	{
		BlockPos origin = context.origin();
		WorldGenLevel level = context.level();
		RandomSource rng = context.random();
		
		if(origin.getY() <= level.getMinBuildHeight() + 4)
		{
			return false;
		} else
		{
			origin = origin.below(4);
			
			boolean[] replaces = new boolean[2048];
			
			int i = rng.nextInt(4) + 4;
			
			for(int j = 0; j < i; ++j)
			{
				double d0 = rng.nextDouble() * 6.0D + 3.0D;
				double d1 = rng.nextDouble() * 4.0D + 2.0D;
				double d2 = rng.nextDouble() * 6.0D + 3.0D;
				double d3 = rng.nextDouble() * (16.0D - d0 - 2.0D) + 1.0D + d0 / 2.0D;
				double d4 = rng.nextDouble() * (8.0D - d1 - 4.0D) + 2.0D + d1 / 2.0D;
				double d5 = rng.nextDouble() * (16.0D - d2 - 2.0D) + 1.0D + d2 / 2.0D;
				
				for(int xOff = 1; xOff < 15; ++xOff)
				{
					for(int zOff = 1; zOff < 15; ++zOff)
					{
						for(int yOff = 1; yOff < 7; ++yOff)
						{
							double d6 = ((double) xOff - d3) / (d0 / 2.0D);
							double d7 = ((double) yOff - d4) / (d1 / 2.0D);
							double d8 = ((double) zOff - d5) / (d2 / 2.0D);
							
							double d9 = d6 * d6 + d7 * d7 + d8 * d8;
							
							if(d9 < 1.0D)
								replaces[(xOff * 16 + zOff) * 8 + yOff] = true;
						}
					}
				}
			}
			
			BlockState fluidState = FluidsZT_World.CRUDE_OIL.getSourceBlockState();
			
			for(int xOff = 0; xOff < 16; ++xOff)
			{
				for(int zOff = 0; zOff < 16; ++zOff)
				{
					for(int yOff = 0; yOff < 8; ++yOff)
					{
						boolean gen = !replaces[(xOff * 16 + zOff) * 8 + yOff]
								&& (xOff < 15 && replaces[((xOff + 1) * 16 + zOff) * 8 + yOff]
								|| xOff > 0 && replaces[((xOff - 1) * 16 + zOff) * 8 + yOff]
								|| zOff < 15 && replaces[(xOff * 16 + zOff + 1) * 8 + yOff]
								|| zOff > 0 && replaces[(xOff * 16 + (zOff - 1)) * 8 + yOff]
								|| yOff < 7 && replaces[(xOff * 16 + zOff) * 8 + yOff + 1]
								|| yOff > 0 && replaces[(xOff * 16 + zOff) * 8 + (yOff - 1)]
						);
						
						if(gen)
						{
							Material material = level.getBlockState(origin.offset(xOff, yOff, zOff)).getMaterial();
							if(yOff >= 4 && material.isLiquid())
								return false;
							
							if(yOff < 4 && !material.isSolid() && level.getBlockState(origin.offset(xOff, yOff, zOff)) != fluidState)
								return false;
						}
					}
				}
			}
			
			for(int xOff = 0; xOff < 16; ++xOff)
			{
				for(int zOff = 0; zOff < 16; ++zOff)
				{
					for(int yOff = 0; yOff < 8; ++yOff)
					{
						if(replaces[(xOff * 16 + zOff) * 8 + yOff])
						{
							BlockPos pos = origin.offset(xOff, yOff, zOff);
							if(this.canReplaceBlock(level.getBlockState(pos)))
							{
								boolean isHighEnough = yOff >= 4;
								level.setBlock(pos, isHighEnough ? AIR : fluidState, 2);
								if(isHighEnough)
								{
									level.scheduleTick(pos, AIR.getBlock(), 0);
									this.markAboveForPostProcessing(level, pos);
								}
							}
						}
					}
				}
			}
			
			return true;
		}
	}
	
	public static boolean canWrite(WorldGenLevel level, BlockPos pos)
	{
		if(level instanceof WorldGenRegion region)
		{
			int i = SectionPos.blockToSectionCoord(pos.getX());
			int j = SectionPos.blockToSectionCoord(pos.getZ());
			ChunkPos chunkpos = region.getCenter();
			int k = Math.abs(chunkpos.x - i);
			int l = Math.abs(chunkpos.z - j);
			return k <= region.writeRadiusCutoff && l <= region.writeRadiusCutoff;
		}
		
		return true;
	}
	
	private boolean canReplaceBlock(BlockState state)
	{
		return !state.is(BlockTags.FEATURES_CANNOT_REPLACE);
	}
	
	public record OilLakeConfiguration(LakeType type, FloatProvider floodAmount)
			implements FeatureConfiguration
	{
		public static final Codec<OilLakeConfiguration> CODEC = RecordCodecBuilder.create(
				instance -> instance.group(
						LakeType.CODEC.fieldOf("type").forGetter(OilLakeConfiguration::type),
						FloatProvider.CODEC.fieldOf("flood").forGetter(OilLakeConfiguration::floodAmount)
				).apply(instance, OilLakeConfiguration::new)
		);
	}
	
	public enum LakeType
	{
		SMALL,
		MEDIUM,
		LARGE;
		
		public static final Codec<LakeType> CODEC = RecordCodecBuilder.create(
				instance -> instance.group(
						Codec.STRING.fieldOf("type").forGetter(LakeType::name)
				).apply(instance, LakeType::valueOf)
		);
	}
}