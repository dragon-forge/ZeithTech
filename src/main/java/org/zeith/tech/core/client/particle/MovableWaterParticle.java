package org.zeith.tech.core.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public class MovableWaterParticle
		extends TextureSheetParticle
{
	protected final double[] yCoords;
	protected final double originX, originY, originZ, destX, destY, destZ;
	
	protected MovableWaterParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd)
	{
		super(level, x, y, z, xd, yd, zd);
		
		lifetime = 20 + (int) Math.ceil(new Vec3(x, y, z).distanceTo(new Vec3(xd, yd, zd)));
		
		yCoords = createTrajectory(y, yd, lifetime, 0F, 0F);
		
		originX = x;
		originY = y;
		originZ = z;
		
		destX = xd;
		destY = yd;
		destZ = zd;
	}
	
	@Override
	public ParticleRenderType getRenderType()
	{
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}
	
	@Override
	public void tick()
	{
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
		this.preMoveUpdate();
		if(!this.removed)
		{
			double xMove = (destX - originX) / lifetime;
			double yMove = yCoords[age] - y;
			double zMove = (destZ - originZ) / lifetime;
			
			double d0 = xMove;
			double d1 = yMove;
			double d2 = zMove;
			
			if(this.hasPhysics && (xMove != 0.0D || yMove != 0.0D || zMove != 0.0D) && xMove * xMove + yMove * yMove + zMove * zMove < 10000.0)
			{
				Vec3 vec3 = Entity.collideBoundingBox((Entity) null, new Vec3(xMove, yMove, zMove), this.getBoundingBox(), this.level, List.of());
				xMove = vec3.x;
				yMove = vec3.y;
				zMove = vec3.z;
			}
			
			if(xMove != 0.0D || yMove != 0.0D || zMove != 0.0D)
			{
				this.setBoundingBox(this.getBoundingBox().move(xMove, yMove, zMove));
				this.setLocationFromBoundingbox();
			}
			
			this.onGround = d1 != yMove && d1 < 0.0D;
			if(d0 != xMove)
				this.xd = 0.0D;
			
			if(d2 != zMove)
				this.zd = 0.0D;
		}
	}
	
	protected void preMoveUpdate()
	{
		if(++this.age >= this.lifetime)
		{
			this.remove();
			for(int i = 0; i < 10; ++i)
				this.level.addParticle(ParticleTypes.SPLASH, this.x, this.y, this.z, 0.0D, 0.0D, 0.0D);
			SoundEvent soundevent = SoundEvents.POINTED_DRIPSTONE_DRIP_WATER;
			float f = Mth.randomBetween(this.random, 0.8F, 1.0F);
			this.level.playLocalSound(this.x, this.y, this.z, soundevent, SoundSource.BLOCKS, f, 1.0F, false);
		}
	}
	
	public static double[] createTrajectory(double y, double ty, int coords, float timeOffset, float offset)
	{
		double yDel = y - ty;
		float dy = (float) (yDel / coords) * 2;
		double[] yPoints = new double[coords + 1];
		for(int a = 0; a <= coords; ++a)
		{
			float my, phase = (float) a / (float) coords;
			my = Mth.sin((timeOffset + a) / 5.0F) * offset * (1.0F - phase);
			yPoints[a] = y - dy * a + my + Math.sin(Math.toRadians(phase * 180F)) * yDel * 2;
			dy *= 1.0F - 1.0F / (coords * 3.0F / 2.0F);
		}
		return yPoints;
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class WaterProvider
			implements ParticleProvider<SimpleParticleType>
	{
		protected final SpriteSet sprite;
		
		public WaterProvider(SpriteSet set)
		{
			this.sprite = set;
		}
		
		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xd, double yd, double zd)
		{
			var particle = new MovableWaterParticle(level, x, y, z, xd, yd, zd);
			particle.setColor(0.2F, 0.3F, 1.0F);
			particle.pickSprite(this.sprite);
			return particle;
		}
	}
}
