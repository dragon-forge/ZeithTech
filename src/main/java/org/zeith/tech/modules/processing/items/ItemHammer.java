package org.zeith.tech.modules.processing.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.tech.api.block.multiblock.base.MultiBlockRegistry;
import org.zeith.tech.api.enums.TechTier;
import org.zeith.tech.api.events.recipe.HammerHitEvent;
import org.zeith.tech.api.tile.IHammerable;
import org.zeith.tech.core.ZeithTech;
import org.zeith.tech.modules.generators.init.AdvancementTriggersZT;
import org.zeith.tech.modules.processing.init.RecipeRegistriesZT_Processing;
import org.zeith.tech.modules.shared.init.SoundsZT;

import java.util.Comparator;
import java.util.Optional;

public class ItemHammer
		extends Item
{
	private final RandomSource random = RandomSource.create();
	
	static
	{
		MinecraftForge.EVENT_BUS.addListener(ItemHammer::leftClickBlock);
		MinecraftForge.EVENT_BUS.addListener(ItemHammer::rightClickBlock);
		MinecraftForge.EVENT_BUS.addListener(ItemHammer::damageAnvil);
	}
	
	final Optional<TagKey<Item>> repairItem;
	
	public ItemHammer(Properties props, Optional<TagKey<Item>> repairItem)
	{
		super(props.defaultDurability(256));
		this.repairItem = repairItem;
	}
	
	@Override
	public InteractionResult useOn(UseOnContext ctx)
	{
		final var level = ctx.getLevel();
		final var pos = ctx.getClickedPos();
		
		final var horiz = Optional.ofNullable(ctx.getPlayer())
				.map(Player::getDirection)
				.map(Direction::getOpposite)
				.orElse(Direction.NORTH);
		
		var multiblock = MultiBlockRegistry.registered()
				.flatMap(former ->
						former.findCenterAndRotation(level, pos, horiz)
								.stream()
								.map(t -> t.add(former))
				)
				.findFirst()
				.orElse(null);
		
		if(multiblock != null)
		{
			if(!level.isClientSide)
				multiblock.d().placeMultiBlock(level, multiblock.b(), multiblock.c(), Cast.cast(multiblock.a()));
			
			ctx.getItemInHand()
					.hurtAndBreak(1, ctx.getPlayer(), pl -> pl.broadcastBreakEvent(ctx.getHand()));
		}
		
		return InteractionResult.sidedSuccess(multiblock != null);
	}
	
	@Override
	public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player)
	{
		return false;
	}
	
	@Override
	public boolean isRepairable(ItemStack stack)
	{
		return !repairItem.isEmpty();
	}
	
	@Override
	public boolean hasCraftingRemainingItem(ItemStack stack)
	{
		return true;
	}
	
	@Override
	public ItemStack getCraftingRemainingItem(ItemStack itemStack)
	{
		ItemStack damagedStack = itemStack.copy();
		return damagedStack.hurt(1, this.random, null) ? ItemStack.EMPTY : damagedStack;
	}
	
	@Override
	public boolean isValidRepairItem(ItemStack hammer, ItemStack repairItem)
	{
		return (this.repairItem.isPresent() && repairItem.is(this.repairItem.get()))
				|| super.isValidRepairItem(hammer, repairItem);
	}
	
	private static void rightClickBlock(PlayerInteractEvent.RightClickBlock e)
	{
		var player = e.getEntity();
		var level = e.getLevel();
		var pos = e.getPos();
		var state = level.getBlockState(pos);
		
		double d0 = player.getReachDistance();
		var hitResult = player.pick(d0, 1F, false);
		
		if(!level.isClientSide && hitResult.getType() == HitResult.Type.BLOCK && hitResult instanceof BlockHitResult res && res.getDirection() == Direction.UP)
		{
			if(res.getBlockPos().equals(pos))
			{
				var recipe = RecipeRegistriesZT_Processing.HAMMERING.getRecipes().stream().filter(r -> r.matches(state, e.getItemStack(), TechTier.BASIC)).findFirst().orElse(null);
				
				if(recipe != null)
				{
					if(!level.isClientSide() && level instanceof ServerLevel srv)
					{
						var vec = res.getLocation();
						
						var item = new ItemEntity(srv, vec.x, vec.y, vec.z, e.getItemStack().split(1), 0, 0, 0);
						item.setDefaultPickUpDelay();
						srv.addFreshEntity(item);
					}
					
					e.setCanceled(true);
				}
			}
		}
	}
	
	private static void damageAnvil(HammerHitEvent e)
	{
		var lvl = e.getEntity().getLevel();
		if(!lvl.isClientSide())
		{
			var hit = e.getHit();
			var pos = hit.getBlockPos();
			var state = lvl.getBlockState(pos);
			
			var damaged = AnvilBlock.damage(state);
			if(lvl.random.nextFloat() < 0.12F / 8F)
			{
				if(damaged != null)
				{
					lvl.setBlock(pos, damaged, 2);
				} else if(state.is(Blocks.DAMAGED_ANVIL))
				{
					lvl.removeBlock(pos, false);
					
					if(lvl instanceof ServerLevel srv)
						srv.playSound(null, e.getEntity(), SoundsZT.ANVIL_DESTROY, SoundSource.PLAYERS, 0.5F, 1F);
				}
			}
		}
	}
	
	private static void leftClickBlock(PlayerInteractEvent.LeftClickBlock e)
	{
		var hammerStack = e.getItemStack();
		
		if(!hammerStack.isEmpty() && hammerStack.getItem() instanceof ItemHammer hammer)
		{
			var player = e.getEntity();
			var level = e.getLevel();
			var pos = e.getPos();
			var state = level.getBlockState(pos);
			
			double d0 = player.getReachDistance();
			var hitResult = player.pick(d0, 1F, false);
			
			if(!level.isClientSide && hitResult.getType() == HitResult.Type.BLOCK && hitResult instanceof BlockHitResult res && res.getDirection() == Direction.UP)
			{
				if(res.getBlockPos().equals(pos))
				{
					var key = ZeithTech.MOD_ID + "_hammer_hits";
					var keyTotal = ZeithTech.MOD_ID + "_hammer_hits_total";
					var keyLast = ZeithTech.MOD_ID + "_hammer_hit_last";
					
					var hitLoc = res.getLocation();
					var hitBox = new AABB(hitLoc, hitLoc).inflate(0.125F);
					var items = level.getEntitiesOfClass(ItemEntity.class, hitBox);
					
					items.sort(Comparator.comparingDouble(ent -> ent.distanceToSqr(hitLoc)));
					
					for(var ent : items)
					{
						var dropStack = ent.getItem();
						
						int timeSinceHit = ent.getAge() - ent.getPersistentData().getInt(keyLast);
						
						var recipe = RecipeRegistriesZT_Processing.HAMMERING.getRecipes().stream().filter(r -> r.matches(state, dropStack, TechTier.BASIC)).findFirst().orElse(null);
						
						if(recipe != null && timeSinceHit > 5 && !MinecraftForge.EVENT_BUS.post(new HammerHitEvent(player, hammerStack, res, recipe, ent)))
						{
							var bb = ent.getBoundingBox();
							var pp = bb.getCenter();
							
							var data = ent.getPersistentData();
							
							if(level instanceof ServerLevel srv)
							{
								if(!data.contains(keyTotal))
									data.putInt(keyTotal, recipe.getHitCount(hammerStack, ent, hitLoc, player, srv));
								
								var done = false;
								
								int hits = data.getInt(key) + 1;
								if(hits >= data.getInt(keyTotal))
								{
									dropStack.split(1);
									
									// Send update to tracking players / kill empty item entity.
									if(!dropStack.isEmpty()) ent.setItem(dropStack.copy());
									else ent.remove(Entity.RemovalReason.KILLED);
									
									var out = recipe.getRecipeOutput();
									
									ent.spawnAtLocation(out);
									
									data.remove(key);
									data.remove(keyTotal);
									
									AdvancementTriggersZT.MAKE_PLATE.trigger(player);
									
									done = true;
								} else
									data.putInt(key, hits);
								
								data.putInt(keyLast, ent.getAge());
								
								srv.sendParticles(ParticleTypes.CRIT, pp.x, bb.maxY, pp.z, 10, 0.1, -0.1, 0.1, 0.2);
								srv.playSound(null, pp.x, pp.y, pp.z, done ? SoundsZT.ANVIL_USE_DONE : SoundsZT.ANVIL_USE, SoundSource.PLAYERS, 1F, 1F);
							}
							
							hammerStack.hurtAndBreak(1, player, pl -> pl.broadcastBreakEvent(e.getHand()));
							
							break;
						}
					}
					e.setCanceled(true);
				}
			}
			
			if(hitResult.getType() == HitResult.Type.BLOCK && hitResult instanceof BlockHitResult res)
			{
				IHammerable h;
				
				if(level.getBlockEntity(pos) instanceof IHammerable h0 && h0.onHammerLeftClicked(hammerStack, e.getSide(), e.getFace(), e.getEntity(), e.getHand(), res))
					h = h0;
				else if(level.getBlockState(pos).getBlock() instanceof IHammerable h0 && h0.onHammerLeftClicked(hammerStack, e.getSide(), e.getFace(), e.getEntity(), e.getHand(), res))
					h = h0;
				else
					return;
				
				if(level instanceof ServerLevel srv)
				{
					var pp = res.getLocation();
					srv.sendParticles(ParticleTypes.CRIT, pp.x, pp.y + 0.05, pp.z, 10, 0.1, -0.1, 0.1, 0.2);
					
					srv.playSound(null, player, h.getHammeredSound(hammerStack, e.getSide(), e.getFace(), e.getEntity(), e.getHand(), res), SoundSource.PLAYERS, 1F, 1F);
				}
				
				hammerStack.hurtAndBreak(1, player, pl -> pl.broadcastBreakEvent(e.getHand()));
				e.setCanceled(true);
			}
		}
	}
}