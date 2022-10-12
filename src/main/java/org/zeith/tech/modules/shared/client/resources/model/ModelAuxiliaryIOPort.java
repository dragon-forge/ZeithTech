package org.zeith.tech.modules.shared.client.resources.model;

import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zeith.tech.api.misc.AuxiliaryPortDefinition;
import org.zeith.tech.api.tile.slots.SlotType;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.modules.shared.blocks.aux_io_port.TileAuxiliaryIOPort;

import java.util.*;

public class ModelAuxiliaryIOPort
		implements BakedModel
{
	private static final FaceBakery FACE_BAKERY = new FaceBakery();
	
	public final ResourceLocation modelLocation = new ResourceLocation(ZeithTech.MOD_ID, "blockauxiliary_io_port");
	
	TextureAtlasSprite base, up;
	final Map<ResourceLocation, TextureAtlasSprite> aux = new HashMap<>();
	
	public ModelAuxiliaryIOPort(ModelManager modelManager)
	{
		var blockAtlas = modelManager.getAtlas(InventoryMenu.BLOCK_ATLAS);
		
		this.base = blockAtlas.getSprite(new ResourceLocation(ZeithTech.MOD_ID, "block/aux_io_port/base"));
		this.up = blockAtlas.getSprite(new ResourceLocation(ZeithTech.MOD_ID, "block/aux_io_port/up"));
		
		SlotType.types()
				.map(SlotType::getTextures)
				.flatMap(AuxiliaryPortDefinition::textures)
				.forEach(rl -> aux.put(rl, blockAtlas.getSprite(rl)));
	}
	
	public static Vector3f ZERO = Vector3f.ZERO;
	public static Vector3f ONE = new Vector3f(16.0F, 16.0F, 16.0F);
	
	public static Vector3f ZERO2 = new Vector3f(-0.1F, -0.1F, -0.1F);
	public static Vector3f ONE2 = new Vector3f(16.1F, 16.1F, 16.1F);
	
	private static final BlockFaceUV FULL_UV = new BlockFaceUV(new float[] {
			0,
			0,
			16,
			16
	}, 0);
	
	@Override
	public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType)
	{
		var texGet = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
		
		List<BakedQuad> all = new ArrayList<>();
		
		if(side != null)
		{
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
	public boolean useAmbientOcclusion()
	{
		return true;
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
		return base;
	}
	
	@Override
	public ItemOverrides getOverrides()
	{
		return ItemOverrides.EMPTY;
	}
	
	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand)
	{
		return getQuads(state, side, rand, ModelData.EMPTY, RenderType.solid());
	}
}