package org.zeith.tech.modules.shared.client.resources.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
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
		
		@Override
		public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType)
		{
			var sub = data.get(TileMultiBlockPart.SUB_STATE);
			
			if(sub != null)
			{
				var dispatcher = Minecraft.getInstance().getBlockRenderer();
				var model = dispatcher.getBlockModel(sub);
				
				return renderType == null || model.getRenderTypes(sub, rand, ModelData.EMPTY).contains(renderType) ?
						model.getQuads(sub, side, rand, ModelData.EMPTY, renderType) : List.of();
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