package org.zeith.tech.compat.patchouli.client;

import org.zeith.tech.api.ZeithTechAPI;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.ClientBookRegistry;

public class ClientPatchouliCompat
{
	public void run()
	{
		register("machine_assembly", PageMachineAssembly.class);
	}
	
	private static void register(String id, Class<? extends BookPage> page)
	{
		ClientBookRegistry.INSTANCE.pageTypes.put(ZeithTechAPI.id(id), page);
	}
}