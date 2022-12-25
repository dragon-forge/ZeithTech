package org.zeith.tech.api.modules;

import org.zeith.tech.api.modules.processing.FarmData;

public interface IModuleProcessing
		extends IBaseModule
{
	FarmData farmData();
}