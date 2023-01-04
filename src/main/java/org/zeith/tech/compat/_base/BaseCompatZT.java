package org.zeith.tech.compat._base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zeith.hammerlib.compat.base.BaseCompat;
import org.zeith.tech.utils.LegacyEventBus;

public class BaseCompatZT
		extends BaseCompat<BaseCompatZT>
{
	protected final Logger log = LogManager.getLogger("ZeithTech/" + getClass().getSimpleName());
	
	public void setup(LegacyEventBus bus)
	{
	}
}