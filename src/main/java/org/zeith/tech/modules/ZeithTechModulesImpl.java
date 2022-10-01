package org.zeith.tech.modules;

import org.zeith.tech.api.modules.*;
import org.zeith.tech.modules.processing.ProcessingModule;
import org.zeith.tech.modules.shared.SharedModule;
import org.zeith.tech.modules.transport.TransportModule;
import org.zeith.tech.modules.world.WorldModule;

public class ZeithTechModulesImpl
		implements IZeithTechModules, IInternalCode
{
	final SharedModule sharedModule = new SharedModule();
	final TransportModule transportModule = new TransportModule();
	final WorldModule worldModule = new WorldModule();
	final ProcessingModule processingModule = new ProcessingModule();
	
	@Override
	public void enable()
	{
		sharedModule.enable();
		transportModule.enable();
		worldModule.enable();
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