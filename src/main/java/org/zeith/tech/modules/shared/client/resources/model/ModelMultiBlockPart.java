package org.zeith.tech.modules.shared.client.resources.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.client.model.IBakedModel;
import org.zeith.hammerlib.client.model.LoadUnbakedGeometry;
import org.zeith.tech.modules.shared.blocks.multiblock_part.TileMultiBlockPart;
import org.zeith.tech.modules.transport.client.resources.model.ModelFacadeSystem;
import org.zeith.tech.shadow.codechicken.lib.model.pipeline.transformers.QuadReInterpolator;
import org.zeith.tech.shadow.codechicken.lib.model.pipeline.transformers.QuadTinter;
import org.zeith.tech.shadow.fabric.*;

import java.util.*;
import java.util.function.Function;

@LoadUnbakedGeometry(path = "multiblock_part")
public class ModelMultiBlockPart
		implements IUnbakedGeometry<ModelMultiBlockPart>
{
	public final Material particle;
	
	public ModelMultiBlockPart(JsonObject obj, JsonDeserializationContext ctx)
	{
		this.particle = new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(GsonHelper.getAsString(obj, "particle")));
	}
	
	@Override
	public BakedModel bake(IGeometryBakingContext context, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation)
	{
		return new Baked(spriteGetter.apply(particle));
	}
	
	@Override
	public Collection<Material> getMaterials(IGeometryBakingContext context, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
	{
		return List.of(particle);
	}
	
	private static class Baked
			implements IBakedModel
	{
		TextureAtlasSprite particle;
		
		public Baked(TextureAtlasSprite particle)
		{
			this.particle = particle;
		}
		
		private static final Renderer renderer = Renderer.getInstance();
		
		@Override
		public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType)
		{
			var sub = data.get(TileMultiBlockPart.SUB_STATE);
			var lvl = data.get(TileMultiBlockPart.SUB_LEVEL);
			var pos = data.get(TileMultiBlockPart.SUB_POS);
			
			if(sub != null && lvl != null && pos != null)
			{
				var dispatcher = Minecraft.getInstance().getBlockRenderer();
				var model = dispatcher.getBlockModel(sub);
				
				if(renderType == null || model.getRenderTypes(sub, rand, ModelData.EMPTY).contains(renderType))
				{
					var quads = model.getQuads(sub, side, rand, ModelData.EMPTY, renderType);
					var facadeAccess = new ModelFacadeSystem.FacadeBlockAccess(lvl, pos, side, sub);
					BlockColors blockColors = Minecraft.getInstance().getBlockColors();
					QuadReInterpolator interpolator = new QuadReInterpolator();
					
					MeshBuilder meshBuilder = renderer.meshBuilder();
					QuadEmitter emitter = meshBuilder.getEmitter();
					
					for(var quad : quads)
					{
						var ti = quad.getTintIndex();
						QuadTinter quadTinter = null;
						if(ti != -1)
							quadTinter = new QuadTinter(blockColors.getColor(sub, facadeAccess, pos, ti));
						
						emitter.fromVanilla(quad.getVertices(), 0, false);
						emitter.nominalFace(quad.getDirection());
						
						interpolator.setInputQuad(emitter);
						
						interpolator.transform(emitter);
						
						if(quadTinter != null) quadTinter.transform(emitter);
						
						emitter.emit();
					}
					
					return meshBuilder.build().toBakedBlockQuads();
				}
			}
			
			return List.of();
		}
		
		@Override
		public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data)
		{
			return ChunkRenderTypeSet.all();
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
			return particle;
		}
	}
}