package org.zeith.tech.modules.shared.client.resources.model;

import com.google.gson.*;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.geometry.*;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.client.model.IBakedModel;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.hammerlib.util.java.tuples.Tuple2;
import org.zeith.hammerlib.util.java.tuples.Tuples;
import org.zeith.tech.api.item.multitool.IMultiToolItem;
import org.zeith.tech.api.item.multitool.IMultiToolPart;
import org.zeith.tech.core.ZeithTech;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ModelMultiTool
		implements IUnbakedGeometry<ModelMultiTool>
{
	public static class Loader
			implements IGeometryLoader<ModelMultiTool>
	{
		@Override
		public ModelMultiTool read(JsonObject json, JsonDeserializationContext context) throws JsonParseException
		{
			return new ModelMultiTool(json, context);
		}
	}
	
	protected ItemTransforms itemTransforms;
	protected Material particle;
	
	protected final Set<ResourceLocation> childModelKeys;
	protected final Map<ResourceLocation, UnbakedModel> unbakedModelMap = new HashMap<>();
	
	public ModelMultiTool(JsonObject json, JsonDeserializationContext ctx) throws JsonParseException
	{
		ItemTransforms transforms = ItemTransforms.NO_TRANSFORMS;
		if(json.has("display"))
		{
			JsonObject display = GsonHelper.getAsJsonObject(json, "display");
			transforms = ctx.deserialize(display, ItemTransforms.class);
		}
		
		var particle = parseTextureLocationOrReference(InventoryMenu.BLOCK_ATLAS, json.get("particle").getAsString());
		this.particle = particle.map(m -> m, l ->
		{
			ZeithTech.LOG.warn("Unable to parse texture: " + l);
			return new Material(InventoryMenu.BLOCK_ATLAS, MissingTextureAtlasSprite.getLocation());
		});
		
		this.itemTransforms = transforms;
		
		this.childModelKeys = ForgeRegistries.ITEMS
				.getValues()
				.stream()
				.flatMap(item -> Cast.optionally(item, IMultiToolPart.class).stream())
				.flatMap(i -> i.getAllPossibleMultiToolPartModels().stream())
				.collect(Collectors.toSet());
	}
	
	private static Either<Material, String> parseTextureLocationOrReference(ResourceLocation atlas, String texture)
	{
		if(isTextureReference(texture))
		{
			return Either.right(texture.substring(1));
		} else
		{
			ResourceLocation resourcelocation = ResourceLocation.tryParse(texture);
			if(resourcelocation == null)
			{
				throw new JsonParseException(texture + " is not valid resource location");
			} else
			{
				return Either.left(new Material(atlas, resourcelocation));
			}
		}
	}
	
	static boolean isTextureReference(String texture)
	{
		return texture.charAt(0) == '#';
	}
	
	public Map<ResourceLocation, BakedModel> getAllPartModelLocations(Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ModelBaker bakery)
	{
		return unbakedModelMap
				.entrySet()
				.stream()
				.map(path -> Tuples.immutable(
						path.getKey(),
						path.getValue().bake(bakery, spriteGetter, modelState, path.getKey())
				))
				.filter(t -> t.b() != null)
				.collect(Collectors.toMap(Tuple2::a, Tuple2::b));
	}
	
	@Override
	public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context)
	{
		for(var c : childModelKeys)
		{
			var model = modelGetter.apply(c);
			model.resolveParents(modelGetter);
			unbakedModelMap.put(c, model);
		}
	}
	
	@Override
	public BakedModel bake(IGeometryBakingContext context, ModelBaker bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation)
	{
		var bakedMap = getAllPartModelLocations(spriteGetter, modelState, bakery);
		return new BakedMultiToolModel(itemTransforms, bakedMap, particle, spriteGetter, overrides);
	}
	
	public static class BakedMultiToolModel
			implements IBakedModel
	{
		protected final ItemTransforms transforms;
		protected final Map<ResourceLocation, BakedModel> modelLocations;
		protected final Material particle;
		protected final Function<Material, TextureAtlasSprite> spriteGetter;
		protected final ItemOverrides overrides;
		
		public BakedMultiToolModel(ItemTransforms transforms, Map<ResourceLocation, BakedModel> modelLocations, Material particle, Function<Material, TextureAtlasSprite> spriteGetter, ItemOverrides overrides)
		{
			this.transforms = transforms;
			this.modelLocations = modelLocations;
			this.particle = particle;
			this.spriteGetter = spriteGetter;
			this.overrides = overrides;
		}
		
		@Override
		public ItemOverrides getOverrides()
		{
			return overrides;
		}
		
		@Override
		public List<RenderType> getRenderTypes(ItemStack itemStack, boolean fabulous)
		{
			if(!(itemStack.getItem() instanceof IMultiToolItem mt))
				return List.of();
			return mt.getParts(itemStack, true, true)
					.map(pair -> pair.getA().getMultiToolPartModel(pair.getB(), itemStack))
					.map(modelLocations::get)
					.filter(Objects::nonNull)
					.flatMap(m -> m.getRenderTypes(itemStack, fabulous).stream())
					.distinct()
					.toList();
		}
		
		@Override
		public List<BakedModel> getRenderPasses(ItemStack itemStack, boolean fabulous)
		{
			if(!(itemStack.getItem() instanceof IMultiToolItem mt))
				return List.of();
			return mt.getParts(itemStack, true, true)
					.map(pair -> pair.getA().getMultiToolPartModel(pair.getB(), itemStack))
					.map(modelLocations::get)
					.filter(Objects::nonNull)
					.toList();
		}
		
		@Override
		public TextureAtlasSprite getParticleIcon()
		{
			return spriteGetter.apply(particle);
		}
		
		@Override
		public ItemTransforms getTransforms()
		{
			return transforms;
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
			return false;
		}
		
		@Override
		public boolean isCustomRenderer()
		{
			return false;
		}
		
		@Override
		public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType)
		{
			return List.of();
		}
	}
}