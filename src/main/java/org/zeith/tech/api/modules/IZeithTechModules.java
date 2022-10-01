package org.zeith.tech.api.modules;

public interface IZeithTechModules
{
	IModuleShared shared();
	
	IModuleWorld world();
	
	IModuleTransport transport();
	
	IModuleProcessing processing();
}