package org.zeith.tech.modules.shared.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.core.Direction;

public class RotatedRenderHelper
{
	public static void rotateHorizontalPoseStack(PoseStack matrix, Direction direction, float x, float y, float z)
	{
		switch(direction)
		{
			case NORTH ->
			{
				matrix.translate(x, y, z);
			}
			case SOUTH ->
			{
				matrix.translate(1 - x, y, 1 - z);
				matrix.mulPose(Vector3f.YP.rotationDegrees(180F));
			}
			case WEST ->
			{
				matrix.translate(z, y, 1 - x);
				matrix.mulPose(Vector3f.YP.rotationDegrees(90F));
			}
			case EAST ->
			{
				matrix.translate(1 - z, y, x);
				matrix.mulPose(Vector3f.YP.rotationDegrees(270F));
			}
		}
	}
}