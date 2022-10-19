package org.zeith.tech.core.advancements;

import com.google.gson.JsonObject;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import org.zeith.tech.core.ZeithTech;

import java.util.*;

public class AdvancementTrigger
		implements CriterionTrigger<AdvancementTrigger.Instance>, IAdvancementTrigger
{
	private final ResourceLocation id;
	private final Map<PlayerAdvancements, AdvancementTrigger.Listeners> listeners = new HashMap<>();
	
	public AdvancementTrigger(String id)
	{
		super();
		this.id = new ResourceLocation(ZeithTech.MOD_ID, id);
	}
	
	public Instance instance()
	{
		return new Instance(id);
	}
	
	@Override
	public ResourceLocation getId()
	{
		return this.id;
	}
	
	@Override
	public void addPlayerListener(PlayerAdvancements advancements, CriterionTrigger.Listener<Instance> listener)
	{
		this.listeners.computeIfAbsent(advancements, AdvancementTrigger.Listeners::new).add(listener);
	}
	
	@Override
	public void removePlayerListener(PlayerAdvancements advancements, CriterionTrigger.Listener<Instance> listener)
	{
		AdvancementTrigger.Listeners l = this.listeners.get(advancements);
		if(l != null)
		{
			l.remove(listener);
			if(l.isEmpty()) this.listeners.remove(advancements);
		}
	}
	
	@Override
	public void removePlayerListeners(PlayerAdvancements playerAdvancementsIn)
	{
		this.listeners.remove(playerAdvancementsIn);
	}
	
	@Override
	public Instance createInstance(JsonObject object, DeserializationContext conditions)
	{
		return new AdvancementTrigger.Instance(this.getId());
	}
	
	@Override
	public void trigger(ServerPlayer player)
	{
		AdvancementTrigger.Listeners l = this.listeners.get(player.getAdvancements());
		if(l != null) l.trigger();
	}
	
	public static class Instance
			implements CriterionTriggerInstance
	{
		private final ResourceLocation id;
		
		public Instance(ResourceLocation id)
		{
			this.id = id;
		}
		
		public boolean test()
		{
			return true;
		}
		
		@Override
		public ResourceLocation getCriterion()
		{
			return id;
		}
		
		@Override
		public JsonObject serializeToJson(SerializationContext conditions)
		{
			return new JsonObject();
		}
	}
	
	static class Listeners
	{
		private final PlayerAdvancements playerAdvancements;
		private final Set<CriterionTrigger.Listener<Instance>> listeners = new HashSet<>();
		
		Listeners(PlayerAdvancements playerAdvancementsIn)
		{
			this.playerAdvancements = playerAdvancementsIn;
		}
		
		public boolean isEmpty()
		{
			return this.listeners.isEmpty();
		}
		
		public void add(CriterionTrigger.Listener<Instance> listener)
		{
			this.listeners.add(listener);
		}
		
		public void remove(CriterionTrigger.Listener<Instance> listener)
		{
			this.listeners.remove(listener);
		}
		
		public Listeners trigger()
		{
			List<CriterionTrigger.Listener<Instance>> list = null;
			
			for(CriterionTrigger.Listener<Instance> listener : this.listeners)
			{
				if(listener.getTriggerInstance().test())
				{
					if(list == null)
					{
						list = new ArrayList<>();
					}
					
					list.add(listener);
				}
			}
			
			if(list != null)
			{
				for(CriterionTrigger.Listener<Instance> l : list)
				{
					l.run(this.playerAdvancements);
				}
			}
			
			return this;
		}
	}
}