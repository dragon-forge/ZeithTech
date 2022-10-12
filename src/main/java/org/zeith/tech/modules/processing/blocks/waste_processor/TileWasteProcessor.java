package org.zeith.tech.modules.processing.blocks.waste_processor;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.zeith.hammerlib.util.physics.FrictionRotator;
import org.zeith.tech.modules.processing.blocks.base.machine.ContainerBaseMachine;
import org.zeith.tech.modules.processing.blocks.base.machine.TileBaseMachine;
import org.zeith.tech.modules.processing.init.TilesZT_Processing;

import java.util.List;

public class TileWasteProcessor
		extends TileBaseMachine<TileWasteProcessor>
{
	public final FrictionRotator rotator = new FrictionRotator();
	
	public TileWasteProcessor(BlockPos pos, BlockState state)
	{
		super(TilesZT_Processing.WASTE_PROCESSOR, pos, state);
		this.rotator.friction = 1F;
	}
	
	@Override
	public void update()
	{
		
		setEnabledState(false);
		
		if(isOnClient())
		{
			this.rotator.update();
			if(isEnabled())
				this.rotator.speedupTo(100, 5);
		}
		
	}
	
	@Override
	public ContainerBaseMachine<TileWasteProcessor> openContainer(Player player, int windowId)
	{
		return null;
	}
	
	@Override
	public List<Container> getAllInventories()
	{
		return List.of();
	}
}