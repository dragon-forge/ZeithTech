package org.zeith.tech.core;

import org.zeith.tech.api.modules.*;
import org.zeith.tech.core.cfg.ZeithTechModuleConfigs;
import org.zeith.tech.modules.processing.ProcessingModule;
import org.zeith.tech.modules.shared.SharedModule;
import org.zeith.tech.modules.transport.TransportModule;
import org.zeith.tech.modules.world.WorldModule;
import org.zeith.tech.utils.LegacyEventBus;

import java.util.List;

class ModulesImpl
		implements IZeithTechModules, IInternalCode
{
	final SharedModule sharedModule = new SharedModule();
	final WorldModule worldModule = new WorldModule();
	final TransportModule transportModule = new TransportModule();
	final ProcessingModule processingModule = new ProcessingModule();
	
	final List<IInternalCode> subs = List.of(sharedModule, worldModule, transportModule, processingModule);
	
	@Override
	public void construct(LegacyEventBus bus)
	{
		for(IInternalCode sub : subs)
			sub.construct(bus);
	}
	
	@Override
	public void enable()
	{
		var cfg = ZeithTechModuleConfigs.INSTANCE
				.getCurrent()
				.states;
		
		if(cfg.sharedModule)
			sharedModule.enable();
		
		if(cfg.worldModule)
			worldModule.enable();
		
		if(cfg.transportModule)
			transportModule.enable();
		
		if(cfg.processingModule)
			processingModule.enable();
	}
	
	@Override
	public IModuleShared shared()
	{
		return sharedModule;
	}
	
	@Override
	public IModuleWorld world()
	{
		return worldModule;
	}
	
	@Override
	public IModuleTransport transport()
	{
		return transportModule;
	}
	
	@Override
	public IModuleProcessing processing()
	{
		return processingModule;
	}
}