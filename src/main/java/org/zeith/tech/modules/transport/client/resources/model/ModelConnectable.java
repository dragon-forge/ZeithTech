package org.zeith.tech.modules.transport.client.resources.model;

import com.google.gson.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.client.model.IBakedModel;
import org.zeith.hammerlib.client.model.LoadUnbakedGeometry;
import org.zeith.hammerlib.util.java.tuples.Tuple2;
import org.zeith.hammerlib.util.java.tuples.Tuples;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@LoadUnbakedGeometry(path = "transport/block/connectable")
public class ModelConnectable
		implements IUnbakedGeometry<ModelConnectable>
{
	private final Map<String, List<ResourceLocation>> models = new HashMap<>();
	private final Set<String> inventoryParts = new HashSet<>();
	private final ResourceLocation renderTypeHint;
	
	public ModelConnectable(JsonObject json, JsonDeserializationContext context)
	{
		if(json.has("render_type"))
		{
			var renderTypeHintName = GsonHelper.getAsString(json, "render_type");
			this.renderTypeHint = new ResourceLocation(renderTypeHintName);
		} else
			this.renderTypeHint = null;
		
		var obj = json.getAsJsonObject("parts");
		
		for(var value : obj.keySet())
		{
			var val = obj.get(value);
			if(val.isJsonArray())
			{
				var arr = val.getAsJsonArray();
				models.put(value, IntStream.range(0, arr.size()).mapToObj(arr::get).map(JsonElement::getAsString).map(ResourceLocation::new).toList());
			} else
				models.put(value, List.of(new ResourceLocation(val.getAsString())));
		}
		
		var inv = json.getAsJsonArray("inventory");
		for(int i = 0; i < inv.size(); i++)
			inventoryParts.add(inv.get(i).getAsString());
	}
	
	@Override
	public BakedModel bake(IGeometryBakingContext context, ModelBaker bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation)
	{
		var renderTypes = renderTypeHint != null ? context.getRenderType(renderTypeHint) : RenderTypeGroup.EMPTY;
		
		ModelFacadeSystem.BakedFacadeModel facades = new ModelFacadeSystem.BakedFacadeModel(spriteGetter, bakery, ItemOverrides.EMPTY, modelLocation);
		
		return new Baked(spriteGetter, models.entrySet()
				.stream()
				.map(e -> Tuples.immutable(e.getKey(), e.getValue().stream().map(bakery::getModel).map(m -> m.bake(bakery, spriteGetter, modelState, modelLocation)).toList()))
				.collect(Collectors.toMap(Tuple2::a, Tuple2::b)), inventoryParts, facades, renderTypes);
	}
	
	private static class Baked
			implements IBakedModel
	{
		protected final net.minecraftforge.client.ChunkRenderTypeSet blockRenderTypes;
		protected final List<net.minecraft.client.renderer.RenderType> itemRenderTypes;
		protected final List<net.minecraft.client.renderer.RenderType> fabulousItemRenderTypes;
		
		private final Function<Material, TextureAtlasSprite> materialMap;
		private final Map<String, List<BakedModel>> models;
		
		private final ModelFacadeSystem.BakedFacadeModel facades;
		private final Set<String> inventoryParts;
		
		public Baked(Function<Material, TextureAtlasSprite> materialMap, Map<String, List<BakedModel>> models, Set<String> inventoryParts, ModelFacadeSystem.BakedFacadeModel facades, RenderTypeGroup renderTypes)
		{
			this.materialMap = materialMap;
			this.models = models;
			this.inventoryParts = inventoryParts;
			this.facades = facades;
			this.blockRenderTypes = !renderTypes.isEmpty() ? net.minecraftforge.client.ChunkRenderTypeSet.of(renderTypes.block()) : null;
			this.itemRenderTypes = !renderTypes.isEmpty() ? List.of(renderTypes.entity()) : null;
			this.fabulousItemRenderTypes = !renderTypes.isEmpty() ? List.of(renderTypes.entityFabulous()) : null;
		}
		
		@Override
		public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType)
		{
			List<BakedQuad> quads = new ArrayList<>();
			if(state == null && side == null)
			{
				for(String part : inventoryParts)
					for(var m : models.get(part))
						quads.addAll(m.getQuads(null, null, rand, data, renderType));
			} else if(state != null && renderType != null && blockRenderTypes.contains(renderType))
			{
				for(Property<?> property : state.getProperties())
					if(property instanceof BooleanProperty b)
					{
						var value = state.getValue(b);
						
						var name = property.getName();
						var inverse = "!" + name;
						
						if(value && models.containsKey(name))
							for(var m : models.get(name))
								quads.addAll(m.getQuads(state, side, rand, data, renderType));
						
						if(!value && models.containsKey(inverse))
							for(var m : models.get(inverse))
								quads.addAll(m.getQuads(state, side, rand, data, renderType));
					}
				if(models.containsKey(""))
					for(var m : models.get(""))
						quads.addAll(m.getQuads(state, side, rand, data, renderType));
			}
			quads.addAll(facades.getQuads(state, side, rand, data, renderType));
			return quads;
		}
		
		@Override
		public @NotNull ModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ModelData modelData)
		{
			return facades.getModelData(level, pos, state, modelData);
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
		
		TextureAtlasSprite particle;
		
		@Override
		public TextureAtlasSprite getParticleIcon()
		{
			if(particle == null)
				particle = models.values()
						.stream()
						.flatMap(List::stream)
						.map(BakedModel::getParticleIcon)
						.findFirst()
						.orElseGet(() -> materialMap.apply(new Material(InventoryMenu.BLOCK_ATLAS, MissingTextureAtlasSprite.getLocation())));
			return particle;
		}
		
		@Override
		public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data)
		{
			return ChunkRenderTypeSet.all();
		}
		
		@Override
		public List<RenderType> getRenderTypes(ItemStack itemStack, boolean fabulous)
		{
			if(!fabulous)
			{
				if(itemRenderTypes != null)
					return itemRenderTypes;
			} else
			{
				if(fabulousItemRenderTypes != null)
					return fabulousItemRenderTypes;
			}
			
			return IBakedModel.super.getRenderTypes(itemStack, fabulous);
		}
	}
}
