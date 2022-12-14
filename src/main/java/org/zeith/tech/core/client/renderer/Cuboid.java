package org.zeith.tech.core.client.renderer;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.client.render.FluidRendererHelper;
import org.zeith.hammerlib.client.utils.FluidTextureType;

import java.util.Arrays;
import java.util.function.Predicate;

public class Cuboid
{
	private static final Direction[] DIRECTIONS = Direction.values();
	
	public float minX;
	public float minY;
	public float minZ;
	public float maxX;
	public float maxY;
	public float maxZ;
	private final ISpriteInfo[] textures = new ISpriteInfo[6];
	private final boolean[] renderSides = new boolean[] {
			true,
			true,
			true,
			true,
			true,
			true
	};
	
	public Cuboid()
	{
	}
	
	public Cuboid setSideRender(Predicate<Direction> shouldRender)
	{
		for(var dir : DIRECTIONS)
			this.setSideRender(dir, shouldRender.test(dir));
		return this;
	}
	
	public Cuboid setSideRender(Direction side, boolean value)
	{
		this.renderSides[side.ordinal()] = value;
		return this;
	}
	
	public Cuboid copy()
	{
		Cuboid copy = new Cuboid();
		System.arraycopy(this.textures, 0, copy.textures, 0, this.textures.length);
		System.arraycopy(this.renderSides, 0, copy.renderSides, 0, this.renderSides.length);
		return copy.bounds(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
	}
	
	@Nullable
	public ISpriteInfo getSpriteToRender(Direction side)
	{
		int ordinal = side.ordinal();
		return this.renderSides[ordinal] ? this.textures[ordinal] : null;
	}
	
	public Cuboid shrink(float amount)
	{
		return this.grow(-amount);
	}
	
	public Cuboid grow(float amount)
	{
		return this.bounds(this.minX - amount, this.minY - amount, this.minZ - amount, this.maxX + amount, this.maxY + amount, this.maxZ + amount);
	}
	
	public Cuboid xBounds(float min, float max)
	{
		this.minX = min;
		this.maxX = max;
		return this;
	}
	
	public Cuboid yBounds(float min, float max)
	{
		this.minY = min;
		this.maxY = max;
		return this;
	}
	
	public Cuboid zBounds(float min, float max)
	{
		this.minZ = min;
		this.maxZ = max;
		return this;
	}
	
	public Cuboid bounds(float min, float max)
	{
		return this.bounds(min, min, min, max, max, max);
	}
	
	public Cuboid bounds(AABB shape)
	{
		return this.xBounds((float) shape.minX, (float) shape.maxX)
				.yBounds((float) shape.minY, (float) shape.maxY)
				.zBounds((float) shape.minZ, (float) shape.maxZ);
	}
	
	public Cuboid bounds(VoxelShape shape)
	{
		return this.xBounds((float) shape.min(Direction.Axis.X), (float) shape.max(Direction.Axis.X))
				.yBounds((float) shape.min(Direction.Axis.Y), (float) shape.max(Direction.Axis.Y))
				.zBounds((float) shape.min(Direction.Axis.Z), (float) shape.max(Direction.Axis.Z));
	}
	
	public Cuboid bounds(float minX, float minY, float minZ, float maxX, float maxY, float maxZ)
	{
		return this.xBounds(minX, maxX).yBounds(minY, maxY).zBounds(minZ, maxZ);
	}
	
	public Cuboid prepSingleFaceModelSize(Direction face)
	{
		this.bounds(0.0F, 1.0F);
		Cuboid var10000;
		switch(face)
		{
			case NORTH:
				var10000 = this.zBounds(-0.01F, -0.001F);
				break;
			case SOUTH:
				var10000 = this.zBounds(1.001F, 1.01F);
				break;
			case WEST:
				var10000 = this.xBounds(-0.01F, -0.001F);
				break;
			case EAST:
				var10000 = this.xBounds(1.001F, 1.01F);
				break;
			case DOWN:
				var10000 = this.yBounds(-0.01F, -0.001F);
				break;
			case UP:
				var10000 = this.yBounds(1.001F, 1.01F);
				break;
			default:
				throw new IncompatibleClassChangeError();
		}
		
		return var10000;
	}
	
	public Cuboid prepFlowing(@NotNull FluidStack fluid)
	{
		SpriteInfo still = new SpriteInfo(FluidRendererHelper.getFluidTexture(fluid, FluidTextureType.STILL), 16);
		SpriteInfo flowing = new SpriteInfo(FluidRendererHelper.getFluidTexture(fluid, FluidTextureType.FLOWING), 8);
		return this.setTextures(still, still, flowing, flowing, flowing, flowing);
	}
	
	public Cuboid setTexture(Direction side, ISpriteInfo spriteInfo)
	{
		this.textures[side.ordinal()] = spriteInfo;
		return this;
	}
	
	public Cuboid setTexture(TextureAtlasSprite tex)
	{
		return this.setTexture(tex, 16);
	}
	
	public Cuboid setTexture(TextureAtlasSprite tex, int size)
	{
		return setTexture(new SpriteInfo(tex, size));
	}
	
	public Cuboid setTexture(ISpriteInfo sprite)
	{
		Arrays.fill(this.textures, sprite);
		return this;
	}
	
	public Cuboid setTextures(ISpriteInfo down, ISpriteInfo up, ISpriteInfo north, ISpriteInfo south, ISpriteInfo west, ISpriteInfo east)
	{
		this.textures[0] = down;
		this.textures[1] = up;
		this.textures[2] = north;
		this.textures[3] = south;
		this.textures[4] = west;
		this.textures[5] = east;
		return this;
	}
	
	public interface ISpriteInfo
	{
		float getU(float progress);
		
		float getV(float progress);
		
		static ISpriteInfo ofTexture(int x, int y, int spriteWidth, int spriteHeight, int textureWidth, int textureHeight)
		{
			return new ISpriteInfo()
			{
				@Override
				public float getU(float progress)
				{
					return (x + spriteWidth * progress) / textureWidth;
				}
				
				@Override
				public float getV(float progress)
				{
					return (y + spriteHeight * progress) / textureHeight;
				}
			};
		}
	}
	
	public record SpriteInfo(TextureAtlasSprite sprite, int size)
			implements ISpriteInfo
	{
		@Override
		public float getU(float progress)
		{
			return sprite.getU(progress * size);
		}
		
		@Override
		public float getV(float progress)
		{
			return sprite.getV(progress * size);
		}
	}
}