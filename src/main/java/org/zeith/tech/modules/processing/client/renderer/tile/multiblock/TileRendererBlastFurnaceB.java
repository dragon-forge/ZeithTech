package org.zeith.tech.modules.processing.client.renderer.tile.multiblock;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import org.zeith.hammerlib.client.render.tile.IBESR;
import org.zeith.hammerlib.util.mcf.RotationHelper;
import org.zeith.tech.api.ZeithTechAPI;
import org.zeith.tech.core.client.renderer.*;
import org.zeith.tech.modules.processing.blocks.blast_furnace.TileBlastFurnaceB;

public class TileRendererBlastFurnaceB
		implements IBESR<TileBlastFurnaceB>
{
	protected final Cuboid cuboid;
	
	public TileRendererBlastFurnaceB()
	{
		cuboid = new Cuboid();
		
		cuboid.setTexture(Cuboid.ISpriteInfo.ofTexture(0, 48, 48, 48, 96, 96));
		cuboid.setTexture(Direction.UP, Cuboid.ISpriteInfo.ofTexture(0, 0, 48, 48, 96, 96));
		cuboid.setTexture(Direction.DOWN, Cuboid.ISpriteInfo.ofTexture(48, 0, 48, 48, 96, 96));
		cuboid.setTexture(Direction.NORTH, Cuboid.ISpriteInfo.ofTexture(48, 48, 48, 48, 96, 96));
	}
	
	public static final RenderType BLAST_FURNACE_TYPE = RenderType.entityCutout(ZeithTechAPI.id("textures/processing/entity/blast_furnace/basic.png"));
	
	@Override
	public void render(TileBlastFurnaceB entity, float partial, PoseStack pose, MultiBufferSource buf, int lighting, int overlay)
	{
		if(!entity.isMultiblockValid()) return;
		
		cuboid.bounds(
				new AABB(0, 0, 0, 1, 1, 1)
						.inflate(0.001)
		);
		
		lighting = entity.updateLighting(tile ->
		{
			Level level = tile.getLevel();
			int i;
			if(level != null)
			{
				i = 0;
				
				for(var direction : Direction.values())
				{
					var pos = tile.getBlockPos().relative(direction, 2);
					
					for(Direction d : Direction.values())
					{
						if(d.getAxis() != direction.getAxis())
						{
							BlockPos pos1 = pos.relative(d);
							
							Direction d2 = RotationHelper.rotate(d, direction.getAxis(), Direction.AxisDirection.POSITIVE, Rotation.CLOCKWISE_90);
							BlockPos pos2 = pos1.relative(d2);
							
							i = Math.max(i, LevelRenderer.getLightColor(level, pos1));
							i = Math.max(i, LevelRenderer.getLightColor(level, pos2));
						}
					}
				}
			} else i = 0xF000F0;
			return i;
		});
		
		pose.pushPose();
		
		RotatedRenderHelper.rotateHorizontalPoseStack(pose, entity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING), 0F, 0F, 0F);
		
		pose.translate(-1, -1, -1);
		pose.scale(3, 3, 3);
		CuboidRenderer.renderCube(cuboid, pose, buf.getBuffer(BLAST_FURNACE_TYPE), 0xFFFFFFFF, lighting, overlay, CuboidRenderer.FaceDisplay.FRONT, true);
		pose.popPose();
	}
}