package com.leobeliik.mycustomtweaks;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

import static com.leobeliik.mycustomtweaks.MyCustomTweaks.MODID;

@Mod(value = MODID, dist = Dist.CLIENT)
public class MyCustomTweaksClient {

	public MyCustomTweaksClient(IEventBus modEventBus, ModContainer modContainer) {
		modEventBus.addListener(this::registerBindings);
	}

	public static final KeyMapping magnetKey = new KeyMapping(
			new TranslatableContents("Toggle Botania Magnet KEY", null, TranslatableContents.NO_ARGS).getKey(),
			InputConstants.Type.KEYSYM,
			InputConstants.UNKNOWN.getValue(),
			"key.categories.misc");

	private void registerBindings(RegisterKeyMappingsEvent event) {
		event.register(magnetKey);
	}
}
