package org.zeith.tech.api.tile.facade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;
import org.apache.commons.compress.utils.Lists;
import org.zeith.hammerlib.core.init.TagsHL;
import org.zeith.tech.api.ZeithTechAPI;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Supplier;

@AutoRegisterCapability
public class FacadeData
		implements INBTSerializable<ListTag>
{
	private static final ModelProperty<FacadeData> FACADE_DATA = new ModelProperty<>();
	
	public static final double THIN_THICKNESS = 1D / 16D;
	
	public static final AABB[] THIN_FACADE_BOXES = new AABB[] {
			new AABB(0.0, 0.0, 0.0, 1.0, THIN_THICKNESS, 1.0),
			new AABB(0.0, 1.0 - THIN_THICKNESS, 0.0, 1.0, 1.0, 1.0),
			new AABB(0.0, 0.0, 0.0, 1.0, 1.0, THIN_THICKNESS),
			new AABB(0.0, 0.0, 1.0 - THIN_THICKNESS, 1.0, 1.0, 1.0),
			new AABB(0.0, 0.0, 0.0, THIN_THICKNESS, 1.0, 1.0),
			new AABB(1.0 - THIN_THICKNESS, 0.0, 0.0, 1.0, 1.0, 1.0)
	};
	
	public static final VoxelShape[] THIN_FACADE_SHAPES = new VoxelShape[] {
			Shapes.box(0.0, 0.0, 0.0, 1.0, THIN_THICKNESS, 1.0),
			Shapes.box(0.0, 1.0 - THIN_THICKNESS, 0.0, 1.0, 1.0, 1.0),
			Shapes.box(0.0, 0.0, 0.0, 1.0, 1.0, THIN_THICKNESS),
			Shapes.box(0.0, 0.0, 1.0 - THIN_THICKNESS, 1.0, 1.0, 1.0),
			Shapes.box(0.0, 0.0, 0.0, THIN_THICKNESS, 1.0, 1.0),
			Shapes.box(1.0 - THIN_THICKNESS, 0.0, 0.0, 1.0, 1.0, 1.0)
	};
	
	private final Map<Direction, FacadeFace> faces = new HashMap<>();
	
	WeakReference<BlockAndTintGetter> levelReference;
	BlockPos position;
	final Supplier<List<AABB>> usedBoundingBoxes;
	final Runnable onFacadesChanged;
	
	public FacadeData(Supplier<List<AABB>> usedBoundingBoxes, Runnable onFacadesChanged)
	{
		this.usedBoundingBoxes = usedBoundingBoxes;
		this.onFacadesChanged = onFacadesChanged;
	}
	
	public VoxelShape orShapes(VoxelShape blockShape)
	{
		return Shapes.or(blockShape, faces.keySet().stream().map(Direction::ordinal).map(id -> THIN_FACADE_SHAPES[id]).toArray(VoxelShape[]::new));
	}
	
	public Optional<ItemStack> pickFacade(BlockPos pos, Vec3 coord)
	{
		for(Direction dir : Direction.values())
			if(THIN_FACADE_BOXES[dir.ordinal()].move(pos).inflate(0.00001F).contains(coord))
				if(faces.containsKey(dir))
					return Optional.of(faces.get(dir).facadeItem().copy());
		return Optional.empty();
	}
	
	public InteractionResult placeFacade(UseOnContext context)
	{
		var face = context.getClickedFace();
		
		if(context.getItemInHand().is(TagsHL.Items.TOOLS_WRENCH) && context.getPlayer().isShiftKeyDown())
		{
			List<Direction> matched = Lists.newArrayList();
			for(Direction dir : Direction.values())
				if(THIN_FACADE_BOXES[dir.ordinal()].move(context.getClickedPos()).inflate(0.00001F).contains(context.getClickLocation()) && faces.containsKey(dir))
					matched.add(dir);
			
			if(matched.isEmpty())
				return InteractionResult.PASS;
			
			var dir = matched.contains(context.getClickedFace())
					? context.getClickedFace()
					: matched.get(0);
			
			if(faces.containsKey(dir))
			{
				var data = faces.remove(dir);
				
				if(!context.getLevel().isClientSide && !context.getPlayer().getAbilities().instabuild)
				{
					var loc = context.getClickLocation();
					var ent = new ItemEntity(context.getLevel(), loc.x, loc.y, loc.z, data.facadeItem());
					ent.setDeltaMovement(Vec3.ZERO);
					ent.setNoPickUpDelay();
					context.getLevel().addFreshEntity(ent);
				}
				
				onFacadesChanged.run();
				return InteractionResult.SUCCESS;
			}
		}
		
		if(faces.containsKey(face))
		{
			return InteractionResult.PASS;
		}
		
		var item = context.getItemInHand();
		var state = ZeithTechAPI.get().getFacadeFromItem(item).orElse(null);
		if(state != null)
		{
			if(context.getPlayer().getAbilities().instabuild)
				item = item.copy();
			faces.put(face, new FacadeFace(face, item.split(1), state, !state.isSolidRender(context.getLevel(), context.getClickedPos())));
			
			onFacadesChanged.run();
			return InteractionResult.SUCCESS;
		}
		
		return InteractionResult.PASS;
	}
	
	public static FacadeData get(ModelData data)
	{
		return data.get(FACADE_DATA);
	}
	
	public static boolean has(ModelData data)
	{
		return data.has(FACADE_DATA);
	}
	
	public ModelData.Builder attach(ModelData.Builder builder, BlockEntity from)
	{
		setLevel(from.getLevel());
		position = from.getBlockPos();
		return builder.with(FACADE_DATA, this);
	}
	
	public void setLevel(BlockAndTintGetter level)
	{
		this.levelReference = new WeakReference<>(level);
	}
	
	public BlockAndTintGetter getLevel()
	{
		return levelReference.get();
	}
	
	public BlockPos getPos()
	{
		return position;
	}
	
	public List<AABB> getBoundingBoxes()
	{
		return usedBoundingBoxes.get();
	}
	
	@Override
	public ListTag serializeNBT()
	{
		var lst = new ListTag();
		
		for(var f : faces.values())
			if(f != null)
				lst.add(NbtOps.INSTANCE.withEncoder(FacadeFace.CODEC).apply(f).getOrThrow(false, ZeithTechAPI.LOG::error));
		
		return lst;
	}
	
	@Override
	public void deserializeNBT(ListTag nbt)
	{
		faces.clear();
		for(var i = 0; i < nbt.size(); ++i)
		{
			var f = NbtOps.INSTANCE.withDecoder(FacadeFace.CODEC).apply(nbt.getCompound(i)).getOrThrow(false, ZeithTechAPI.LOG::error).getFirst();
			faces.put(f.identity(), f);
		}
		onFacadesChanged.run();
	}
	
	public Map<Direction, FacadeFace> getFaces()
	{
		return faces;
	}
	
	public record FacadeFace(Direction identity, ItemStack facadeItem, BlockState facadeState, boolean transparent)
	{
		public static final Codec<FacadeFace> CODEC = RecordCodecBuilder.create(instance ->
				instance.group(
						Direction.CODEC.fieldOf("face").forGetter(FacadeFace::identity),
						ItemStack.CODEC.fieldOf("item").forGetter(FacadeFace::facadeItem),
						BlockState.CODEC.fieldOf("state").forGetter(FacadeFace::facadeState),
						Codec.BOOL.fieldOf("transparent").forGetter(FacadeFace::transparent)
				).apply(instance, FacadeFace::new)
		);
	}
}