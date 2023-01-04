package org.zeith.tech.core.mixins.client;
/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import net.minecraft.client.renderer.texture.*;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zeith.tech.shadow.fabric.SpriteFinderImpl;

import java.util.Map;

@Mixin(TextureAtlas.class)
public class TextureAtlasMixin
		implements SpriteFinderImpl.SpriteFinderAccess
{
	@Final
	@Shadow
	private Map<ResourceLocation, TextureAtlasSprite> texturesByName;
	
	private SpriteFinderImpl fabric_spriteFinder = null;
	
	@Inject(at = @At("RETURN"), method = "upload")
	private void uploadHook(SpriteLoader.Preparations input, CallbackInfo info)
	{
		fabric_spriteFinder = null;
	}
	
	@Override
	public SpriteFinderImpl fabric_spriteFinder()
	{
		SpriteFinderImpl result = fabric_spriteFinder;
		
		if(result == null)
		{
			result = new SpriteFinderImpl(texturesByName, (TextureAtlas) (Object) this);
			fabric_spriteFinder = result;
		}
		
		return result;
	}
}