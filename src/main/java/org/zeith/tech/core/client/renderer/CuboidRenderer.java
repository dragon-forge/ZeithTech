package org.zeith.tech.core.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.Util;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import org.zeith.hammerlib.util.colors.ColorHelper;

import java.util.Arrays;

public class CuboidRenderer
{
	private static final Direction[] DIRECTIONS = Direction.values();
	private static final int[] combinedARGB = new int[DIRECTIONS.length];
	private static final Vector3f NORMAL = Util.make(new Vector3f(1.0F, 1.0F, 1.0F), Vector3f::normalize);
	
	public record SpriteInfo(TextureAtlasSprite sprite, int size)
	{
	}
	
	public enum FaceDisplay
	{
		FRONT(true, false),
		BACK(false, true),
		BOTH(true, true);
		
		private final boolean front;
		private final boolean back;
		
		FaceDisplay(boolean front, boolean back)
		{
			this.front = front;
			this.back = back;
		}
	}
	
	public static void renderCube(Cuboid cube, PoseStack matrix, VertexConsumer buffer, int argb, int light, int overlay, FaceDisplay faceDisplay, boolean fakeDisableDiffuse)
	{
		Arrays.fill(combinedARGB, argb);
		renderCube(cube, matrix, buffer, combinedARGB, light, overlay, faceDisplay, fakeDisableDiffuse);
	}
	
	public static void renderCube(Cuboid cube, PoseStack matrix, VertexConsumer buffer, int[] colors, int light, int overlay, FaceDisplay faceDisplay, boolean fakeDisableDiffuse)
	{
		int xShift = Mth.floor(cube.minX);
		int yShift = Mth.floor(cube.minY);
		int zShift = Mth.floor(cube.minZ);
		matrix.pushPose();
		matrix.translate(xShift, yShift, zShift);
		float minX = cube.minX - (float) xShift;
		float minY = cube.minY - (float) yShift;
		float minZ = cube.minZ - (float) zShift;
		float maxX = cube.maxX - (float) xShift;
		float maxY = cube.maxY - (float) yShift;
		float maxZ = cube.maxZ - (float) zShift;
		int xDelta = calculateDelta(minX, maxX);
		int yDelta = calculateDelta(minY, maxY);
		int zDelta = calculateDelta(minZ, maxZ);
		float[] xBounds = getBlockBounds(xDelta, minX, maxX);
		float[] yBounds = getBlockBounds(yDelta, minY, maxY);
		float[] zBounds = getBlockBounds(zDelta, minZ, maxZ);
		PoseStack.Pose lastMatrix = matrix.last();
		Matrix4f matrix4f = lastMatrix.pose();
		Matrix3f normalMatrix = lastMatrix.normal();
		Vector3f normal = fakeDisableDiffuse ? NORMAL : new Vector3f(0, 1, 0);
		Vector3f from = new Vector3f();
		Vector3f to = new Vector3f();
		
		for(int y = 0; y <= yDelta; ++y)
		{
			Cuboid.ISpriteInfo upSprite = y == yDelta ? cube.getSpriteToRender(Direction.UP) : null;
			Cuboid.ISpriteInfo downSprite = y == 0 ? cube.getSpriteToRender(Direction.DOWN) : null;
			from.y = yBounds[y];
			to.y = yBounds[y + 1];
			
			for(int z = 0; z <= zDelta; ++z)
			{
				Cuboid.ISpriteInfo northSprite = z == 0 ? cube.getSpriteToRender(Direction.NORTH) : null;
				Cuboid.ISpriteInfo southSprite = z == zDelta ? cube.getSpriteToRender(Direction.SOUTH) : null;
				from.z = zBounds[z];
				to.z = zBounds[z + 1];
				
				for(int x = 0; x <= xDelta; ++x)
				{
					Cuboid.ISpriteInfo westSprite = x == 0 ? cube.getSpriteToRender(Direction.WEST) : null;
					Cuboid.ISpriteInfo eastSprite = x == xDelta ? cube.getSpriteToRender(Direction.EAST) : null;
					from.x = xBounds[x];
					to.x = xBounds[x + 1];
					putTexturedQuad(buffer, matrix4f, normalMatrix, westSprite, from, to, Direction.WEST, colors, light, overlay, faceDisplay, normal);
					putTexturedQuad(buffer, matrix4f, normalMatrix, eastSprite, from, to, Direction.EAST, colors, light, overlay, faceDisplay, normal);
					putTexturedQuad(buffer, matrix4f, normalMatrix, northSprite, from, to, Direction.NORTH, colors, light, overlay, faceDisplay, normal);
					putTexturedQuad(buffer, matrix4f, normalMatrix, southSprite, from, to, Direction.SOUTH, colors, light, overlay, faceDisplay, normal);
					putTexturedQuad(buffer, matrix4f, normalMatrix, upSprite, from, to, Direction.UP, colors, light, overlay, faceDisplay, normal);
					putTexturedQuad(buffer, matrix4f, normalMatrix, downSprite, from, to, Direction.DOWN, colors, light, overlay, faceDisplay, normal);
				}
			}
		}
		
		matrix.popPose();
	}
	
	private static float[] getBlockBounds(int delta, float start, float end)
	{
		float[] bounds = new float[2 + delta];
		bounds[0] = start;
		int offset = (int) start;
		
		for(int i = 1; i <= delta; ++i)
		{
			bounds[i] = (float) (i + offset);
		}
		
		bounds[delta + 1] = end;
		return bounds;
	}
	
	private static int calculateDelta(float min, float max)
	{
		int delta = (int) (max - (float) ((int) min));
		if((double) max % 1.0 == 0.0)
		{
			--delta;
		}
		
		return delta;
	}
	
	private static void putTexturedQuad(VertexConsumer buffer, Matrix4f matrix, Matrix3f normalMatrix, @Nullable Cuboid.@Nullable ISpriteInfo spriteInfo, Vector3f from, Vector3f to, Direction face, int[] colors, int light, int overlay, FaceDisplay faceDisplay, Vector3f normal)
	{
		if(spriteInfo != null)
		{
			float x1 = from.x();
			float y1 = from.y();
			float z1 = from.z();
			float x2 = to.x();
			float y2 = to.y();
			float z2 = to.z();
			float u1;
			float u2;
			float v1;
			float v2;
			switch(face.getAxis())
			{
				case Z:
					u1 = x2;
					u2 = x1;
					v1 = y1;
					v2 = y2;
					break;
				case X:
					u1 = z2;
					u2 = z1;
					v1 = y1;
					v2 = y2;
					break;
				default:
					u1 = x1;
					u2 = x2;
					v1 = z2;
					v2 = z1;
			}
			
			boolean bigger = u1 > u2;
			u1 %= 1.0F;
			u2 %= 1.0F;
			if(bigger)
			{
				if(u1 == 0.0F)
				{
					u1 = 1.0F;
				}
			} else if(u2 == 0.0F)
			{
				u2 = 1.0F;
			}
			
			bigger = v1 > v2;
			v1 %= 1.0F;
			v2 %= 1.0F;
			if(bigger)
			{
				if(v1 == 0.0F)
				{
					v1 = 1.0F;
				}
			} else if(v2 == 0.0F)
			{
				v2 = 1.0F;
			}
			
			float temp = v1;
			v1 = 1.0F - v2;
			v2 = 1.0F - temp;
			
			float minU = spriteInfo.getU(u1);
			float maxU = spriteInfo.getU(u2);
			float minV = spriteInfo.getV(v1);
			float maxV = spriteInfo.getV(v2);
			
			int argb = colors[face.ordinal()];
			float red = ColorHelper.getRed(argb);
			float green = ColorHelper.getGreen(argb);
			float blue = ColorHelper.getBlue(argb);
			float alpha = ColorHelper.getAlpha(argb);
			
			switch(face)
			{
				case DOWN:
					drawFace(buffer, matrix, normalMatrix, red, green, blue, alpha, minU, maxU, minV, maxV, light, overlay, faceDisplay, normal, x1, y1, z2, x1, y1, z1, x2, y1, z1, x2, y1, z2);
					break;
				case UP:
					drawFace(buffer, matrix, normalMatrix, red, green, blue, alpha, minU, maxU, minV, maxV, light, overlay, faceDisplay, normal, x1, y2, z1, x1, y2, z2, x2, y2, z2, x2, y2, z1);
					break;
				case NORTH:
					drawFace(buffer, matrix, normalMatrix, red, green, blue, alpha, minU, maxU, minV, maxV, light, overlay, faceDisplay, normal, x1, y1, z1, x1, y2, z1, x2, y2, z1, x2, y1, z1);
					break;
				case SOUTH:
					drawFace(buffer, matrix, normalMatrix, red, green, blue, alpha, minU, maxU, minV, maxV, light, overlay, faceDisplay, normal, x2, y1, z2, x2, y2, z2, x1, y2, z2, x1, y1, z2);
					break;
				case WEST:
					drawFace(buffer, matrix, normalMatrix, red, green, blue, alpha, minU, maxU, minV, maxV, light, overlay, faceDisplay, normal, x1, y1, z2, x1, y2, z2, x1, y2, z1, x1, y1, z1);
					break;
				case EAST:
					drawFace(buffer, matrix, normalMatrix, red, green, blue, alpha, minU, maxU, minV, maxV, light, overlay, faceDisplay, normal, x2, y1, z1, x2, y2, z1, x2, y2, z2, x2, y1, z2);
			}
			
		}
	}
	
	private static void drawFace(VertexConsumer buffer, Matrix4f matrix, Matrix3f normalMatrix, float red, float green, float blue, float alpha, float minU, float maxU, float minV, float maxV, int light, int overlay, FaceDisplay faceDisplay, Vector3f normal, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4)
	{
		if(faceDisplay.front)
		{
			buffer.vertex(matrix, x1, y1, z1).color(red, green, blue, alpha).uv(minU, maxV).overlayCoords(overlay).uv2(light).normal(normalMatrix, normal.x(), normal.y(), normal.z()).endVertex();
			buffer.vertex(matrix, x2, y2, z2).color(red, green, blue, alpha).uv(minU, minV).overlayCoords(overlay).uv2(light).normal(normalMatrix, normal.x(), normal.y(), normal.z()).endVertex();
			buffer.vertex(matrix, x3, y3, z3).color(red, green, blue, alpha).uv(maxU, minV).overlayCoords(overlay).uv2(light).normal(normalMatrix, normal.x(), normal.y(), normal.z()).endVertex();
			buffer.vertex(matrix, x4, y4, z4).color(red, green, blue, alpha).uv(maxU, maxV).overlayCoords(overlay).uv2(light).normal(normalMatrix, normal.x(), normal.y(), normal.z()).endVertex();
		}
		
		if(faceDisplay.back)
		{
			buffer.vertex(matrix, x4, y4, z4).color(red, green, blue, alpha).uv(maxU, maxV).overlayCoords(overlay).uv2(light).normal(normalMatrix, 0.0F, -1.0F, 0.0F).endVertex();
			buffer.vertex(matrix, x3, y3, z3).color(red, green, blue, alpha).uv(maxU, minV).overlayCoords(overlay).uv2(light).normal(normalMatrix, 0.0F, -1.0F, 0.0F).endVertex();
			buffer.vertex(matrix, x2, y2, z2).color(red, green, blue, alpha).uv(minU, minV).overlayCoords(overlay).uv2(light).normal(normalMatrix, 0.0F, -1.0F, 0.0F).endVertex();
			buffer.vertex(matrix, x1, y1, z1).color(red, green, blue, alpha).uv(minU, maxV).overlayCoords(overlay).uv2(light).normal(normalMatrix, 0.0F, -1.0F, 0.0F).endVertex();
		}
	}
}