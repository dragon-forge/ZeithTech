package org.zeith.tech.modules.processing.blocks.farm.actions;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayerFactory;
import org.jetbrains.annotations.NotNull;
import org.zeith.tech.api.misc.farm.FarmItemConsumer;
import org.zeith.tech.api.misc.farm.IFarmController;

import java.util.Comparator;
import java.util.UUID;

public record PlaceBlockAction(FarmItemConsumer item, BlockPos pos, BlockState state, int waterUsage, int priority)
		implements Comparable<PlaceBlockAction>
{
	public static final Comparator<PlaceBlockAction> COMPARATOR = Comparator.comparingInt(PlaceBlockAction::priority)
			.thenComparing(a -> a.pos);
	
	public static final Codec<PlaceBlockAction> CODEC = RecordCodecBuilder.create(instance ->
			instance.group(
					FarmItemConsumer.CODEC.fieldOf("item").forGetter(PlaceBlockAction::item),
					BlockPos.CODEC.fieldOf("pos").forGetter(PlaceBlockAction::pos),
					BlockState.CODEC.fieldOf("state").forGetter(PlaceBlockAction::state),
					Codec.INT.fieldOf("water").forGetter(PlaceBlockAction::priority),
					Codec.INT.fieldOf("priority").forGetter(PlaceBlockAction::priority)
			).apply(instance, PlaceBlockAction::new)
	);
	
	@Override
	public int compareTo(@NotNull PlaceBlockAction o)
	{
		return COMPARATOR.compare(this, o);
	}
	
	public static final GameProfile FARM_PLAYER = new GameProfile(new UUID(640839673496L, 3497230482305L), "ZeithTechFarm");
	
	public boolean canPlace(IFarmController controller, ServerLevel level)
	{
		if(!item.consumeItem(controller, true))
			return false;
		
		var item = this.item.getConsumedItem(controller);
		var player = FakePlayerFactory.get(level, FARM_PLAYER);
		player.setItemInHand(InteractionHand.MAIN_HAND, item);
		
		if(level.isEmptyBlock(pos))
			return state.canSurvive(level, pos);
		
		var ctx = new BlockPlaceContext(player, InteractionHand.MAIN_HAND, item, new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false));
		
		return level.getBlockState(pos).canBeReplaced(ctx) && state.canSurvive(level, pos);
	}
}