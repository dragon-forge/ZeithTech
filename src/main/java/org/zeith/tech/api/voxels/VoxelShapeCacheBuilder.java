package org.zeith.tech.api.voxels;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VoxelShapeCacheBuilder
{
	public static final VoxelShapeCacheBuilder INSTANCE = new VoxelShapeCacheBuilder();
	
	public VoxelShape box(Direction rotation, double x, double y, double z, double x2, double y2, double z2)
	{
		Vec3 pivot = new Vec3(8, 8, 8);
		
		Vec3 a = rotateAround(pivot, new Vec3(x, y, z), rotation),
				b = rotateAround(pivot, new Vec3(x2, y2, z2), rotation);
		
		return Block.box(
				Math.min(a.x, b.x), Math.min(a.y, b.y), Math.min(a.z, b.z),
				Math.max(a.x, b.x), Math.max(a.y, b.y), Math.max(a.z, b.z)
		);
	}
	
	public Vec3 rotateAround(Vec3 pivot, Vec3 pos, Direction rotation)
	{
		return switch(rotation)
				{
					default -> pos;
					case WEST -> new Vec3(pivot.x + (pos.z - pivot.z), pos.y, pivot.z - (pos.x - pivot.x));
					case SOUTH -> new Vec3(pivot.x - (pos.x - pivot.x), pos.y, pivot.z - (pos.z - pivot.z));
					case EAST -> new Vec3(pivot.x - (pos.z - pivot.z), pos.y, pivot.z + (pos.x - pivot.x));
				};
	}
}