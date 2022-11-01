package org.zeith.tech.modules.processing.blocks.mining_quarry.basic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.io.NBTSerializable;
import org.zeith.hammerlib.util.mcf.BlockPosList;
import org.zeith.tech.api.ZeithTechCapabilities;
import org.zeith.tech.api.energy.EnergyTier;
import org.zeith.tech.api.enums.*;
import org.zeith.tech.api.tile.RedstoneControl;
import org.zeith.tech.api.tile.energy.EnergyManager;
import org.zeith.tech.api.tile.energy.EnumEnergyManagerKind;
import org.zeith.tech.api.tile.sided.ITileSidedConfig;
import org.zeith.tech.api.tile.sided.TileSidedConfigImpl;
import org.zeith.tech.api.utils.InventoryHelper;
import org.zeith.tech.modules.processing.blocks.base.machine.TileBaseMachine;
import org.zeith.tech.modules.processing.init.TilesZT_Processing;
import org.zeith.tech.modules.processing.items.ItemMiningHead;
import org.zeith.tech.modules.shared.init.TagsZT;
import org.zeith.tech.utils.SidedInventory;

import java.util.*;
import java.util.stream.IntStream;

public class TileMiningQuarryB
		extends TileBaseMachine<TileMiningQuarryB>
{
	@NBTSerializable("Sides")
	public final TileSidedConfigImpl sidedConfig = new TileSidedConfigImpl(this::getFront, EnumSet.of(SidedConfigTyped.ENERGY, SidedConfigTyped.ITEM))
			.setDefaults(SidedConfigTyped.ENERGY, SideConfig.NONE)
			.setDefaults(SidedConfigTyped.ITEM, SideConfig.NONE)
			.setForAll(RelativeDirection.DOWN, SideConfig.DISABLE)
			.setForAll(RelativeDirection.FRONT, SideConfig.DISABLE);
	
	{
		var ecfg = sidedConfig.getSideConfigs(SidedConfigTyped.ENERGY);
		ecfg.setRelative(RelativeDirection.BACK, SideConfig.PULL);
		ecfg.setRelative(RelativeDirection.LEFT, SideConfig.PULL);
		ecfg.setRelative(RelativeDirection.RIGHT, SideConfig.PULL);
		
		var icfg = sidedConfig.getSideConfigs(SidedConfigTyped.ITEM);
		icfg.setRelative(RelativeDirection.UP, SideConfig.PUSH);
		icfg.setRelative(RelativeDirection.BACK, SideConfig.PULL);
		icfg.setRelative(RelativeDirection.LEFT, SideConfig.PULL);
		icfg.setRelative(RelativeDirection.RIGHT, SideConfig.PULL);
	}
	
	/**
	 * Slot 0 is for {@link org.zeith.tech.modules.processing.items.ItemMiningHead}, other slots are output slots of the quarry, a temporal storage of sort.
	 */
	@NBTSerializable("Items")
	public final SidedInventory inventory = new SidedInventory(9, sidedConfig.createItemAccess(new int[] {
			0,
			1,
			2
	}, new int[] {
			3,
			4,
			5,
			6,
			7,
			8
	}));
	
	@NBTSerializable("Redstone")
	public final RedstoneControl redstone = new RedstoneControl();
	
	{
		inventory.isStackValid = (slot, stack) ->
		{
			if(slot == 0) return stack.getItem() instanceof ItemMiningHead;
			if(slot < 3) return stack.is(TagsZT.Items.MINING_PIPE);
			return false;
		};
	}
	
	@NBTSerializable("FE")
	public final EnergyManager energy = new EnergyManager(EnergyTier.EXTRA_LOW_VOLTAGE, EnumEnergyManagerKind.CONSUMER);
	
	@NBTSerializable
	public final BlockPosList yQueue = new BlockPosList();
	
	@NBTSerializable("CurY")
	public int currentY;
	
	@NBTSerializable("Cooldown")
	public int cooldown = 50;
	
	@NBTSerializable("Halted")
	public boolean halted;
	
	@NBTSerializable("DoneMining")
	public boolean isDone;
	
	@NBTSerializable("MiningAt")
	public long doneMiningXZ;
	
	public int defaultCooldown = 50;
	
	public TileMiningQuarryB(BlockPos pos, BlockState state)
	{
		this(TilesZT_Processing.BASIC_QUARRY, pos, state);
	}
	
	public TileMiningQuarryB(BlockEntityType<TileMiningQuarryB> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
		currentY = pos.getY() - 1;
	}
	
	private static final Direction[] DIRECTIONS = Direction.values();
	
	@Override
	public void update()
	{
		energy.update(level, worldPosition, sidedConfig);
		
		if(isDone && atTickRate(20) && doneMiningXZ != BlockPos.asLong(worldPosition.getX(), 0, worldPosition.getZ()))
		{
			currentY = worldPosition.getY() - 1;
			isDone = false;
		}
		
		if(atTickRate(8))
		{
			glob:
			for(Direction to : DIRECTIONS)
			{
				if(sidedConfig.getAccess(SidedConfigTyped.ITEM, to) == SideConfig.PUSH)
				{
					var be = level.getBlockEntity(worldPosition.relative(to));
					var handler = be == null ? null : be.getCapability(ForgeCapabilities.ITEM_HANDLER, to.getOpposite()).resolve().orElse(null);
					
					if(handler != null)
					{
						for(int slotIdx = 3; slotIdx < inventory.getSlots(); ++slotIdx)
						{
							ItemStack contents = inventory.getItem(slotIdx);
							
							var slots = handler.getSlots();
							for(int i = 0; i < slots; ++i)
							{
								var remaining = handler.insertItem(i, contents, isOnClient());
								if(!ItemStack.matches(contents, remaining))
								{
									// If we are not simulating, perform the insert and update the remaining items.
									if(isOnServer())
									{
										inventory.setItem(slotIdx, remaining);
										break glob;
									}
									if(remaining.isEmpty())
										break glob;
								}
							}
						}
					}
				}
			}
		}
		
		if(cooldown > 0)
		{
			boolean decreaseCooldown = halted;
			
			if(!decreaseCooldown)
			{
				if(energy.consumeEnergy(40))
				{
					isInterrupted.setBool(false);
					decreaseCooldown = true;
				} else isInterrupted.setBool(true);
			}
			
			if(decreaseCooldown)
			{
				--cooldown;
				if(!isEnabled())
					setEnabledState(true);
			}
		} else if(cooldown == 0 && !isDone && redstone.shouldWork(this))
		{
			halted = false;
			
			var res = mineOnLevel();
			
			switch(res)
			{
				case WAIT -> cooldown = defaultCooldown;
				case HALT ->
				{
					cooldown = defaultCooldown;
					halted = true;
				}
				case DONE ->
				{
					doneMiningXZ = BlockPos.asLong(worldPosition.getX(), 0, worldPosition.getZ());
					isDone = true;
				}
				case MOVE_DOWN ->
				{
					var qp = new BlockPos(worldPosition.getX(), currentY, worldPosition.getZ());
					
					int minY = level.getMinBuildHeight();
					if(currentY > minY)
					{
						boolean canMove = level.getBlockState(qp).is(TagsZT.Blocks.MINING_PIPE);
						
						if(!canMove)
							for(int i = 1; i < 3; ++i)
							{
								var it = inventory.getItem(i);
								if(it.is(TagsZT.Items.MINING_PIPE))
								{
									var block = Block.byItem(it.getItem());
									if(block != Blocks.AIR)
									{
										it.split(1);
										level.setBlockAndUpdate(qp, block.defaultBlockState());
										canMove = true;
										break;
									}
								}
							}
						
						if(canMove)
						{
							yQueue.clear();
							--currentY;
							cooldown = defaultCooldown;
						}
					} else
					{
						doneMiningXZ = BlockPos.asLong(worldPosition.getX(), 0, worldPosition.getZ());
						isDone = true;
						cooldown = -1;
						halted = true;
						setEnabledState(false);
					}
				}
			}
		} else if(isDone && currentY < worldPosition.getY() && atTickRate(10))
		{
			var qpos = new BlockPos(worldPosition.getX(), currentY, worldPosition.getZ());
			
			var state = level.getBlockState(qpos);
			
			if(state.is(TagsZT.Blocks.MINING_PIPE) && level instanceof ServerLevel srv)
			{
				List<ItemStack> blockDrops = state.getDrops(new LootContext.Builder(srv)
						.withRandom(srv.random)
						.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(qpos))
						.withOptionalParameter(LootContextParams.BLOCK_ENTITY, level.getBlockEntity(qpos))
						.withParameter(LootContextParams.TOOL, Items.NETHERITE_PICKAXE.getDefaultInstance())
				);
				
				// Create copy of all drops!
				List<ItemStack> blockDropsCopy = new ArrayList<>(blockDrops);
				blockDropsCopy.replaceAll(ItemStack::copy);
				
				if(storeAll(blockDrops, true))
				{
					storeAll(blockDropsCopy, false);
					level.destroyBlock(qpos, false);
					++currentY;
				}
			} else
			{
				++currentY;
			}
		} else if(isDone && isEnabled())
			setEnabledState(false);
	}
	
	public boolean storeAll(List<ItemStack> stacks, boolean simulate)
	{
		return InventoryHelper.storeAllStacks(inventory, IntStream.range(3, inventory.getSlots()), stacks, simulate);
	}
	
	public MineResponse mineOnLevel()
	{
		ItemStack miningHead = inventory.getItem(0);
		if(miningHead.isEmpty())
			return MineResponse.HALT;
		ItemMiningHead head;
		if(miningHead.getItem() instanceof ItemMiningHead h)
			head = h;
		else return MineResponse.HALT;
		
		var qpos = new BlockPos(worldPosition.getX(), currentY, worldPosition.getZ());
		if(level.getBlockState(qpos).is(TagsZT.Blocks.MINING_PIPE))
			return MineResponse.MOVE_DOWN;
		
		var allMineables = discoverMineables();
		for(BlockPos pos : allMineables)
			if(!yQueue.contains(pos))
				yQueue.add(pos);
		
		boolean minedBase = level.isEmptyBlock(qpos);
		
		if(!minedBase && yQueue.isEmpty())
		{
			if(level.getBlockState(qpos).getDestroySpeed(level, qpos) < 0)
				return MineResponse.DONE;
			return MineResponse.HALT;
		}
		
		if(minedBase && yQueue.isEmpty())
			return MineResponse.MOVE_DOWN;
		
		for(int i = 0; i < yQueue.size(); ++i)
		{
			var pos = yQueue.get(i);
			if(level.isEmptyBlock(pos))
			{
				yQueue.remove(i);
				--i;
				continue;
			}
			
			var state = level.getBlockState(pos);
			
			if(head.canMine(level, pos, state, miningHead) && level instanceof ServerLevel srv)
			{
				List<ItemStack> blockDrops = state.getDrops(new LootContext.Builder(srv)
						.withRandom(srv.random)
						.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
						.withOptionalParameter(LootContextParams.BLOCK_ENTITY, level.getBlockEntity(pos))
						.withParameter(LootContextParams.TOOL, miningHead)
				);
				
				// Create copy of all drops!
				List<ItemStack> blockDropsCopy = new ArrayList<>(blockDrops);
				blockDropsCopy.replaceAll(ItemStack::copy);
				
				if(storeAll(blockDrops, true))
				{
					head.onPreMine(level, pos, state, miningHead);
					{
						storeAll(blockDropsCopy, false);
						level.destroyBlock(pos, false);
						
						var player = FakePlayerFactory.getMinecraft(srv);
						player.setPos(Vec3.atCenterOf(worldPosition));
						player.setItemInHand(InteractionHand.MAIN_HAND, miningHead.copy());
						miningHead.hurtAndBreak(1, player, pl -> pl.broadcastBreakEvent(EquipmentSlot.MAINHAND));
					}
					head.onPostMine(level, pos, state, miningHead);
					
					return MineResponse.WAIT;
				} else
					return MineResponse.HALT;
			}
		}
		
		return yQueue.isEmpty() ? MineResponse.MOVE_DOWN : MineResponse.HALT;
	}
	
	static final Direction[] HORIZONTAL = {
			Direction.NORTH,
			Direction.EAST,
			Direction.WEST,
			Direction.SOUTH
	};
	
	public List<BlockPos> discoverMineables()
	{
		ItemStack miningHead = inventory.getItem(0);
		if(miningHead.isEmpty())
			return List.of();
		
		ItemMiningHead head;
		if(miningHead.getItem() instanceof ItemMiningHead h)
			head = h;
		else return List.of();
		
		var qpos = new BlockPos(worldPosition.getX(), currentY, worldPosition.getZ());
		if(level.getBlockState(qpos).getDestroySpeed(level, qpos) < 0)
			return List.of();
		
		List<BlockPos> posLst = new ArrayList<>();
		
		posLst.add(qpos);
		
		for(int i = 0; i < posLst.size() && posLst.size() < 196; ++i)
		{
			var pos = posLst.get(i);
			for(Direction dir : HORIZONTAL)
			{
				var rpos = pos.relative(dir);
				if(!posLst.contains(rpos))
				{
					var state = level.getBlockState(rpos);
					if(state.is(Tags.Blocks.ORES) && head.canMine(level, pos, state, miningHead))
						posLst.add(rpos);
				}
			}
		}
		posLst.sort(Comparator.comparingDouble(p -> -p.distSqr(qpos)));
		return posLst;
	}
	
	public enum MineResponse
	{
		MOVE_DOWN,
		WAIT,
		HALT,
		DONE;
	}
	
	@Override
	public ContainerMiningQuarryB openContainer(Player player, int windowId)
	{
		return new ContainerMiningQuarryB(this, player, windowId);
	}
	
	private final LazyOptional<IEnergyStorage> energyCap = LazyOptional.of(() -> energy);
	private final LazyOptional<? extends IItemHandler>[] itemHandlers = SidedInvWrapper.create(inventory, Direction.values());
	private final LazyOptional<ITileSidedConfig> sidedConfigCap = LazyOptional.of(() -> sidedConfig);
	private final LazyOptional<RedstoneControl> redstoneCap = LazyOptional.of(() -> redstone);
	
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
	{
		if(cap == ForgeCapabilities.ENERGY && (side == null || sidedConfig.canAccess(SidedConfigTyped.ENERGY, side))) return energyCap.cast();
		if(cap == ZeithTechCapabilities.REDSTONE_CONTROL) return redstoneCap.cast();
		if(cap == ZeithTechCapabilities.SIDED_CONFIG) return sidedConfigCap.cast();
		if(cap == ZeithTechCapabilities.ENERGY_MEASURABLE) return energy.measurableCap.cast();
		if(cap == ForgeCapabilities.ITEM_HANDLER && (side == null || sidedConfig.canAccess(SidedConfigTyped.ITEM, side))) return itemHandlers[side == null ? 0 : side.ordinal()].cast();
		return super.getCapability(cap, side);
	}
	
	@Override
	public List<Container> getAllInventories()
	{
		return List.of(inventory, energy.batteryInventory);
	}
}