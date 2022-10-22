package org.zeith.tech.modules.transport.client.resources.model;


import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.client.model.IBakedModel;
import org.zeith.hammerlib.client.model.LoadUnbakedGeometry;
import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.api.ZeithTechCapabilities;
import org.zeith.tech.api.tile.facade.FacadeData;
import org.zeith.tech.shadow.codechicken.lib.model.pipeline.transformers.*;
import org.zeith.tech.shadow.fabric.*;

import java.util.*;
import java.util.function.Function;

import static org.zeith.tech.api.tile.facade.FacadeData.*;

@LoadUnbakedGeometry(path = "block/facade_system")
public class ModelFacadeSystem
		implements IUnbakedGeometry<ModelFacadeSystem>
{
	public static final Material FACADE_PARTICLE = new Material(InventoryMenu.BLOCK_ATLAS, ZeithTechAPI.id("block/aux_io_port/base"));
	
	@Override
	public BakedModel bake(IGeometryBakingContext context, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation)
	{
		return new BakedFacadeModel(spriteGetter, bakery, overrides, modelLocation);
	}
	
	@Override
	public Collection<Material> getMaterials(IGeometryBakingContext context, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
	{
		return List.of(
				FACADE_PARTICLE
		);
	}
	
	public static class BakedFacadeModel
			implements IBakedModel
	{
		protected final Function<Material, TextureAtlasSprite> spriteGetter;
		protected final ModelBakery bakery;
		protected final ItemOverrides overrides;
		protected final ResourceLocation modelLocation;
		
		public BakedFacadeModel(Function<Material, TextureAtlasSprite> spriteGetter, ModelBakery bakery, ItemOverrides overrides, ResourceLocation modelLocation)
		{
			this.spriteGetter = spriteGetter;
			this.bakery = bakery;
			this.overrides = overrides;
			this.modelLocation = modelLocation;
		}
		
		@Override
		public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType)
		{
			var facades = FacadeData.get(data);
			if(facades == null || side != null) return List.of();
			List<BakedQuad> quads = new ArrayList<>();
			getFacadeMesh(facades, rand, data, renderType)
					.forEach(q -> quads.add(q.toBlockBakedQuad()));
			return quads;
		}
		
		@Override
		public @NotNull ModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ModelData modelData)
		{
			if(!FacadeData.has(modelData))
			{
				var be = level.getBlockEntity(pos);
				if(be != null)
				{
					var fd = be.getCapability(ZeithTechCapabilities.FACADES).orElse(null);
					if(fd != null)
					{
						modelData = fd.attach(modelData.derive(), be).build();
					}
				}
			}
			
			return modelData;
		}
		
		private static final net.minecraftforge.client.ChunkRenderTypeSet ALL_BASE_TYPES = net.minecraftforge.client.ChunkRenderTypeSet.of(
				RenderType.solid(),
				RenderType.cutout(),
				RenderType.cutoutMipped(),
				RenderType.translucent()
		);
		
		@Override
		public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data)
		{
			return ALL_BASE_TYPES;
		}
		
		@Override
		public boolean useAmbientOcclusion()
		{
			return false;
		}
		
		@Override
		public boolean isGui3d()
		{
			return false;
		}
		
		@Override
		public boolean usesBlockLight()
		{
			return true;
		}
		
		@Override
		public boolean isCustomRenderer()
		{
			return false;
		}
		
		@Override
		public TextureAtlasSprite getParticleIcon()
		{
			return spriteGetter.apply(FACADE_PARTICLE);
		}
	}
	
	private static final Renderer renderer = Renderer.getInstance();
	
	private static Mesh getFacadeMesh(FacadeData facades, RandomSource random, ModelData data, RenderType renderType)
	{
		var parentWorld = facades.getLevel();
		Map<Direction, FacadeData.FacadeFace> facadeStates = facades.getFaces();
		List<AABB> partBoxes = facades.getBoundingBoxes();
		BlockPos pos = facades.getPos();
		BlockColors blockColors = Minecraft.getInstance().getBlockColors();
		
		MeshBuilder meshBuilder = renderer.meshBuilder();
		QuadEmitter emitter = meshBuilder.getEmitter();
		
		for(Map.Entry<Direction, FacadeData.FacadeFace> entry : facadeStates.entrySet())
		{
			Direction side = entry.getKey();
			int sideIndex = side.ordinal();
			var facadeRenderState = entry.getValue();
			
			BlockState blockState = facadeRenderState.facadeState();
			AABB fullBounds = THIN_FACADE_BOXES[sideIndex];
			AABB facadeBox = fullBounds;
			// If we are a transparent facade, we need to modify out BB.
			if(facadeRenderState.transparent())
			{
				double offset = THIN_THICKNESS;
				AEAxisAlignedBB tmpBB = null;
				for(Direction face : Direction.values())
				{
					// Only faces that aren't on our axis
					if(face.getAxis() != side.getAxis())
					{
						var otherState = facadeStates.get(face);
						if(otherState != null && !otherState.transparent())
						{
							if(tmpBB == null)
							{
								tmpBB = AEAxisAlignedBB.fromBounds(facadeBox);
							}
							switch(face)
							{
								case DOWN -> tmpBB.minY += offset;
								case UP -> tmpBB.maxY -= offset;
								case NORTH -> tmpBB.minZ += offset;
								case SOUTH -> tmpBB.maxZ -= offset;
								case WEST -> tmpBB.minX += offset;
								case EAST -> tmpBB.maxX -= offset;
							}
						}
					}
				}
				
				if(tmpBB != null) facadeBox = tmpBB.getBoundingBox();
			}
			
			// calculate the side mask.
			int facadeMask = 0;
			for(Map.Entry<Direction, FacadeData.FacadeFace> ent : facadeStates.entrySet())
			{
				Direction s = ent.getKey();
				if(s.getAxis() != side.getAxis())
				{
					var otherState = ent.getValue();
					if(!otherState.transparent())
					{
						facadeMask |= 1 << s.ordinal();
					}
				}
			}
			
			AEAxisAlignedBB cutOutBox = getCutOutBox(facadeBox, partBoxes);
			List<AABB> holeStrips = getBoxes(facadeBox, cutOutBox, side.getAxis());
			var facadeAccess = new FacadeBlockAccess(parentWorld, pos, side, blockState);
			
			var dispatcher = Minecraft.getInstance().getBlockRenderer();
			var model = dispatcher.getBlockModel(blockState);
			
			QuadFaceStripper faceStripper = new QuadFaceStripper(fullBounds, facadeMask);
			// Setup the kicker.
			QuadCornerKicker kicker = new QuadCornerKicker();
			kicker.setSide(sideIndex);
			kicker.setFacadeMask(facadeMask);
			kicker.setBox(fullBounds);
			kicker.setThickness(THIN_THICKNESS);
			
			QuadReInterpolator interpolator = new QuadReInterpolator();
			
			for(int cullFaceIdx = 0; cullFaceIdx <= ModelHelper.NULL_FACE_ID; cullFaceIdx++)
			{
				Direction cullFace = ModelHelper.faceFromIndex(cullFaceIdx);
				List<BakedQuad> quads = renderType == null || model.getRenderTypes(blockState, random, ModelData.EMPTY).contains(renderType) ?
						model.getQuads(blockState, cullFace, random, ModelData.EMPTY, renderType) : List.of();
				
				for(BakedQuad quad : quads)
				{
					QuadTinter quadTinter = null;
					
					// Prebake the color tint into the quad
					if(quad.getTintIndex() != -1)
					{
						quadTinter = new QuadTinter(
								blockColors.getColor(blockState, facadeAccess, pos, quad.getTintIndex()));
					}
					
					for(AABB box : holeStrips)
					{
						emitter.fromVanilla(quad.getVertices(), 0, false);
						// Keep the cull-face for faces that are flush with the outer block-face on the
						// side the facade is attached to, but clear it for anything that faces inwards
						emitter.cullFace(cullFace == side ? side : null);
						emitter.nominalFace(quad.getDirection());
						interpolator.setInputQuad(emitter);
						
						QuadClamper clamper = new QuadClamper(box);
						if(!clamper.transform(emitter))
						{
							continue;
						}
						
						// Strips faces if they match a mask.
						if(!faceStripper.transform(emitter))
						{
							continue;
						}
						
						// Kicks the edge inner corners in, solves Z fighting
						if(!kicker.transform(emitter))
						{
							continue;
						}
						
						interpolator.transform(emitter);
						
						// Tints the quad if we need it to. Disabled by default.
						if(quadTinter != null)
						{
							quadTinter.transform(emitter);
						}
						
						emitter.emit();
					}
				}
			}
		}
		
		
		return meshBuilder.build();
	}
	
	@javax.annotation.Nullable
	private static AEAxisAlignedBB getCutOutBox(AABB facadeBox, List<AABB> partBoxes)
	{
		AEAxisAlignedBB b = null;
		for(AABB bb : partBoxes)
		{
			if(bb.intersects(facadeBox))
			{
				if(b == null)
				{
					b = AEAxisAlignedBB.fromBounds(bb);
				} else
				{
					b.maxX = Math.max(b.maxX, bb.maxX);
					b.maxY = Math.max(b.maxY, bb.maxY);
					b.maxZ = Math.max(b.maxZ, bb.maxZ);
					b.minX = Math.min(b.minX, bb.minX);
					b.minY = Math.min(b.minY, bb.minY);
					b.minZ = Math.min(b.minZ, bb.minZ);
				}
			}
		}
		return b;
	}
	
	/**
	 * Generates the box segments around the specified hole. If the specified hole is null, a Singleton of the Facade
	 * box is returned.
	 *
	 * @param fb
	 * 		The Facade's box.
	 * @param hole
	 * 		The hole to 'cut'.
	 * @param axis
	 * 		The axis the facade is on.
	 *
	 * @return The box segments.
	 */
	private static List<AABB> getBoxes(AABB fb, AEAxisAlignedBB hole, Direction.Axis axis)
	{
		if(hole == null)
		{
			return Collections.singletonList(fb);
		}
		List<AABB> boxes = new ArrayList<>();
		switch(axis)
		{
			case Y:
				boxes.add(new AABB(fb.minX, fb.minY, fb.minZ, hole.minX, fb.maxY, fb.maxZ));
				boxes.add(new AABB(hole.maxX, fb.minY, fb.minZ, fb.maxX, fb.maxY, fb.maxZ));
				
				boxes.add(new AABB(hole.minX, fb.minY, fb.minZ, hole.maxX, fb.maxY, hole.minZ));
				boxes.add(new AABB(hole.minX, fb.minY, hole.maxZ, hole.maxX, fb.maxY, fb.maxZ));
				
				break;
			case Z:
				boxes.add(new AABB(fb.minX, fb.minY, fb.minZ, fb.maxX, hole.minY, fb.maxZ));
				boxes.add(new AABB(fb.minX, hole.maxY, fb.minZ, fb.maxX, fb.maxY, fb.maxZ));
				
				boxes.add(new AABB(fb.minX, hole.minY, fb.minZ, hole.minX, hole.maxY, fb.maxZ));
				boxes.add(new AABB(hole.maxX, hole.minY, fb.minZ, fb.maxX, hole.maxY, fb.maxZ));
				
				break;
			case X:
				boxes.add(new AABB(fb.minX, fb.minY, fb.minZ, fb.maxX, hole.minY, fb.maxZ));
				boxes.add(new AABB(fb.minX, hole.maxY, fb.minZ, fb.maxX, fb.maxY, fb.maxZ));
				
				boxes.add(new AABB(fb.minX, hole.minY, fb.minZ, fb.maxX, hole.maxY, hole.minZ));
				boxes.add(new AABB(fb.minX, hole.minY, hole.maxZ, fb.maxX, hole.maxY, fb.maxZ));
				break;
			default:
				// should never happen.
				throw new RuntimeException("switch falloff. " + String.valueOf(axis));
		}
		
		return boxes;
	}
	
	public static class FacadeBlockAccess
			implements BlockAndTintGetter
	{
		private final BlockAndTintGetter level;
		private final BlockPos pos;
		private final Direction side;
		private final BlockState state;
		
		public FacadeBlockAccess(BlockAndTintGetter level, BlockPos pos, Direction side, BlockState state)
		{
			this.level = level;
			this.pos = pos;
			this.side = side;
			this.state = state;
		}
		
		@javax.annotation.Nullable
		@Override
		public BlockEntity getBlockEntity(BlockPos pos)
		{
			return this.level.getBlockEntity(pos);
		}
		
		@Override
		public BlockState getBlockState(BlockPos pos)
		{
			if(this.pos == pos)
			{
				return this.state;
			}
			return this.level.getBlockState(pos);
		}
		
		@Override
		public FluidState getFluidState(BlockPos pos)
		{
			return level.getFluidState(pos);
		}
		
		// This is for diffuse lighting
		@Override
		public float getShade(Direction p_230487_1_, boolean p_230487_2_)
		{
			return level.getShade(p_230487_1_, p_230487_2_);
		}
		
		@Override
		public LevelLightEngine getLightEngine()
		{
			return level.getLightEngine();
		}
		
		@Override
		public int getBlockTint(BlockPos blockPosIn, ColorResolver colorResolverIn)
		{
			return level.getBlockTint(blockPosIn, colorResolverIn);
		}
		
		@Override
		public int getHeight()
		{
			return level.getHeight();
		}
		
		@Override
		public int getMinBuildHeight()
		{
			return level.getMinBuildHeight();
		}
	}
	
	private static class AEAxisAlignedBB
	{
		public double minX;
		public double minY;
		public double minZ;
		public double maxX;
		public double maxY;
		public double maxZ;
		
		public AABB getBoundingBox()
		{
			return new AABB(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
		}
		
		public AEAxisAlignedBB(double a, double b, double c, double d, double e,
							   double f)
		{
			this.minX = a;
			this.minY = b;
			this.minZ = c;
			this.maxX = d;
			this.maxY = e;
			this.maxZ = f;
		}
		
		public static AEAxisAlignedBB fromBounds(double a, double b, double c, double d,
												 double e, double f)
		{
			return new AEAxisAlignedBB(a, b, c, d, e, f);
		}
		
		public static AEAxisAlignedBB fromBounds(AABB bb)
		{
			return new AEAxisAlignedBB(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
		}
	}
}