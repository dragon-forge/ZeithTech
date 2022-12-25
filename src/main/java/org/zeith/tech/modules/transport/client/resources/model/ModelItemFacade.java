package org.zeith.tech.modules.transport.client.resources.model;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Vector3f;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.zeith.hammerlib.client.model.IBakedModel;
import org.zeith.hammerlib.client.model.LoadUnbakedGeometry;
import org.zeith.tech.api.tile.facade.FacadeData;
import org.zeith.tech.modules.transport.items.ItemFacade;
import org.zeith.tech.shadow.codechicken.lib.model.pipeline.transformers.*;
import org.zeith.tech.shadow.fabric.*;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

@LoadUnbakedGeometry(path = "item/facade")
public class ModelItemFacade
		implements IUnbakedGeometry<ModelItemFacade>
{
	private static final Renderer renderer = Renderer.getInstance();
	
	@Override
	public BakedModel bake(IGeometryBakingContext context, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation)
	{
		return new GlobalFacadeModel(spriteGetter, overrides);
	}
	
	@Override
	public Collection<Material> getMaterials(IGeometryBakingContext context, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
	{
		return List.of();
	}
	
	public static class GlobalFacadeModel
			implements IBakedModel
	{
		private final Function<Material, TextureAtlasSprite> spriteGetter;
		private final ItemOverrides overrides;
		private final Int2ObjectMap<DiscreteFacadeModel> cache = new Int2ObjectArrayMap<>();
		
		public GlobalFacadeModel(Function<Material, TextureAtlasSprite> spriteGetter, ItemOverrides overrides)
		{
			this.spriteGetter = spriteGetter;
			this.overrides = overrides;
		}
		
		@Override
		public List<BakedModel> getRenderPasses(ItemStack stack, boolean fabulous)
		{
			if(!(stack.getItem() instanceof ItemFacade facade))
			{
				return List.of(this);
			}
			
			ItemStack textureItem = facade.getFacadeBlockStack(stack);
			
			int hash = Objects.hash(ForgeRegistries.ITEMS.getKey(textureItem.getItem()), textureItem.getTag());
			DiscreteFacadeModel model = cache.get(hash);
			if(model == null)
			{
				model = new DiscreteFacadeModel(this, textureItem, overrides);
				cache.put(hash, model);
			}
			
			return List.of(model);
		}
		
		@Override
		public @NotNull List<BakedQuad> getQuads(@org.jetbrains.annotations.Nullable BlockState state, @org.jetbrains.annotations.Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, @org.jetbrains.annotations.Nullable RenderType renderType)
		{
			return List.of();
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
			return spriteGetter.apply(ModelFacadeSystem.FACADE_PARTICLE);
		}
		
		ItemTransforms BLOCK_TRANSFORMS = new ItemTransforms(
				new ItemTransform(new Vector3f(75, 45, 0), new Vector3f(1 / 16F, -0.75F / 16F, 2 / 16F), new Vector3f(0.375F, 0.375F, 0.375F)), // thirdPersonLeftHand
				new ItemTransform(new Vector3f(75, 45, 0), new Vector3f(1 / 16F, -0.75F / 16F, 2 / 16F), new Vector3f(0.375F, 0.375F, 0.375F)), // thirdPersonRightHand
				new ItemTransform(new Vector3f(0, 30, 0), new Vector3f(0, 2 / 16F, 3 / 16F), new Vector3f(0.4F, 0.4F, 0.4F)), // firstPersonLeftHand
				new ItemTransform(new Vector3f(0, 30, 0), new Vector3f(0, 2 / 16F, 3 / 16F), new Vector3f(0.4F, 0.4F, 0.4F)), // firstPersonRightHand
				ItemTransform.NO_TRANSFORM, // head
				new ItemTransform(new Vector3f(30, 225, 0), new Vector3f(-4 / 16F, 1.5F / 16F, 0), new Vector3f(0.625F, 0.625F, 0.625F)), // gui
				new ItemTransform(new Vector3f(), new Vector3f(0, 0.1875F, 0), new Vector3f(0.25F, 0.25F, 0.25F)), // ground
				new ItemTransform(new Vector3f(), new Vector3f(), new Vector3f(0.5F, 0.5F, 0.5F)), // fixed
				ImmutableMap.of()
		);
		
		@Override
		public ItemTransforms getTransforms()
		{
			return BLOCK_TRANSFORMS;
		}
	}
	
	public static class DiscreteFacadeModel
			implements IBakedModel
	{
		private final BakedModel base;
		private final ItemStack textureStack;
		private final ItemOverrides overrides;
		private List<BakedQuad> quads = null;
		
		protected DiscreteFacadeModel(BakedModel base, ItemStack textureStack, ItemOverrides overrides)
		{
			this.base = base;
			this.textureStack = textureStack;
			this.overrides = overrides;
		}
		
		@Override
		public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData data, RenderType renderType)
		{
			if(side != null) return Collections.emptyList();
			if(quads == null)
			{
				quads = buildFacadeItemQuads(this.textureStack, Direction.NORTH).toBakedBlockQuads();
				quads.addAll(base.getQuads(state, null, rand, data, renderType));
				quads = Collections.unmodifiableList(quads);
			}
			return quads;
		}
		
		@Override
		public boolean useAmbientOcclusion()
		{
			return false;
		}
		
		public Mesh buildFacadeItemQuads(ItemStack textureItem, Direction side)
		{
			MeshBuilder meshBuilder = renderer.meshBuilder();
			QuadEmitter emitter = meshBuilder.getEmitter();
			
			BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(textureItem, null,
					null, 0);
			
			QuadReInterpolator interpolator = new QuadReInterpolator();
			
			var itemColors = Minecraft.getInstance().getItemColors();
			QuadClamper clamper = new QuadClamper(FacadeData.THIN_FACADE_BOXES[side.ordinal()]);
			
			for(int cullFaceIdx = 0; cullFaceIdx <= ModelHelper.NULL_FACE_ID; cullFaceIdx++)
			{
				Direction cullFace = ModelHelper.faceFromIndex(cullFaceIdx);
				List<BakedQuad> quads = model.getQuads(null, cullFace, RandomSource.create());
				
				for(BakedQuad quad : quads)
				{
					QuadTinter quadTinter = null;
					
					// Prebake the color tint into the quad
					if(quad.getTintIndex() != -1)
						quadTinter = new QuadTinter(itemColors.getColor(textureItem, quad.getTintIndex()));
					
					emitter.fromVanilla(quad.getVertices(), 0, false);
					emitter.cullFace(cullFace);
					emitter.nominalFace(quad.getDirection());
					interpolator.setInputQuad(emitter);
					
					if(!clamper.transform(emitter)) continue;
					interpolator.transform(emitter);
					
					// Tints the quad if we need it to. Disabled by default.
					if(quadTinter != null) quadTinter.transform(emitter);
					
					emitter.emit();
				}
			}
			
			return meshBuilder.build();
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
			return Minecraft.getInstance()
					.getItemRenderer()
					.getModel(this.textureStack, null, null, 0)
					.getParticleIcon();
		}
		
		@Override
		public ItemOverrides getOverrides()
		{
			return overrides;
		}
	}
}