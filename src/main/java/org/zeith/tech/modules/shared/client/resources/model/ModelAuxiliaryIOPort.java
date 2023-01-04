package org.zeith.tech.modules.shared.client.resources.model;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.zeith.hammerlib.client.model.IBakedModel;
import org.zeith.hammerlib.client.model.LoadUnbakedGeometry;
import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.api.misc.AuxiliaryPortDefinition;
import org.zeith.tech.api.tile.slots.SlotType;
import org.zeith.tech.modules.shared.blocks.aux_io_port.TileAuxiliaryIOPort;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

@LoadUnbakedGeometry(path = "block/auxiliary_io_port")
public class ModelAuxiliaryIOPort
		implements IUnbakedGeometry<ModelAuxiliaryIOPort>
{
	private static final FaceBakery FACE_BAKERY = new FaceBakery();
	
	public static final Material BASE_MATERIAL = new Material(InventoryMenu.BLOCK_ATLAS, ZeithTechAPI.id("block/aux_io_port/base"));
	public static final Material UP_MATERIAL = new Material(InventoryMenu.BLOCK_ATLAS, ZeithTechAPI.id("block/aux_io_port/up"));
	
	public static final Supplier<Stream<Material>> ALL_MATERIALS = () ->
			Stream.concat(
					Stream.of(BASE_MATERIAL, UP_MATERIAL),
					SlotType.types()
							.map(SlotType::getTextures)
							.flatMap(AuxiliaryPortDefinition::textures)
							.distinct()
							.map(path -> new Material(InventoryMenu.BLOCK_ATLAS, path))
			);
	
	@Override
	public BakedModel bake(IGeometryBakingContext context, ModelBaker bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation)
	{
		return new Baked(context, bakery, spriteGetter, modelState, overrides, modelLocation);
	}
	
	private static class Baked
			implements IBakedModel
	{
		public static Vector3f ZERO = new Vector3f(0);
		public static Vector3f ONE = new Vector3f(16.0F, 16.0F, 16.0F);
		
		public static Vector3f ZERO2 = new Vector3f(-0.1F, -0.1F, -0.1F);
		public static Vector3f ONE2 = new Vector3f(16.1F, 16.1F, 16.1F);
		
		private static final BlockFaceUV FULL_UV = new BlockFaceUV(new float[] {
				0,
				0,
				16,
				16
		}, 0);
		
		final ResourceLocation modelLocation;
		TextureAtlasSprite base, up;
		final Map<ResourceLocation, TextureAtlasSprite> aux = new HashMap<>();
		final ItemOverrides overrides;
		
		public Baked(IGeometryBakingContext context, ModelBaker bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation)
		{
			this.base = spriteGetter.apply(BASE_MATERIAL);
			this.up = spriteGetter.apply(UP_MATERIAL);
			
			this.modelLocation = modelLocation;
			this.overrides = overrides;
			
			ALL_MATERIALS.get().forEach(mat ->
					aux.put(mat.texture(), spriteGetter.apply(mat)));
		}
		
		@Override
		public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType)
		{
			if(side == null) return List.of();
			
			List<BakedQuad> all = new ArrayList<>();
			
			all.add(FACE_BAKERY.bakeQuad(ZERO, ONE, new BlockElementFace(side, 0xFFFFFFFF, "base", FULL_UV),
					side == Direction.UP ? up : base, side, BlockModelRotation.X0_Y0, null, false, modelLocation));
			
			var io = data.get(TileAuxiliaryIOPort.IO_PORT_DATA);
			if(io != null && io.containsKey(side) && side != Direction.UP)
			{
				var purpose = io.get(side);
				
				var sprite = aux.getOrDefault(purpose.getTexture(), base);
				var overlay = aux.getOrDefault(purpose.type().getTextures().auxPortOverlayTexture(), base);
				
				all.add(FACE_BAKERY.bakeQuad(ZERO2, ONE2, new BlockElementFace(side, 0xFFFFFFFF, "type", FULL_UV),
						sprite, side, BlockModelRotation.X0_Y0, null, false, modelLocation));
				
				all.add(FACE_BAKERY.bakeQuad(ZERO2, ONE2, new BlockElementFace(side, purpose.color().getRGB(), "overlay", FULL_UV),
						overlay, side, BlockModelRotation.X0_Y0, null, false, modelLocation));
			}
			
			return all;
		}
		
		private static final net.minecraftforge.client.ChunkRenderTypeSet CUTOUT = net.minecraftforge.client.ChunkRenderTypeSet.of(RenderType.cutout());
		
		@Override
		public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data)
		{
			return CUTOUT;
		}
		
		@Override
		public boolean useBlockTransforms()
		{
			return true;
		}
		
		@Override
		public boolean useAmbientOcclusion()
		{
			return false;
		}
		
		@Override
		public boolean isGui3d()
		{
			return true;
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
			return Math.random() >= 0.75 ? up : base;
		}
		
		@Override
		public ItemOverrides getOverrides()
		{
			return overrides;
		}
		
		@Override
		public @NotNull ModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ModelData modelData)
		{
			return level.getBlockEntity(pos) instanceof TileAuxiliaryIOPort port ? port.getModelData() : modelData;
		}
	}
}