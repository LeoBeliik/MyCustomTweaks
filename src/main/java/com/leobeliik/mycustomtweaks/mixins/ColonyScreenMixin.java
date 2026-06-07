package com.leobeliik.mycustomtweaks.mixins;

import com.ldtteam.blockui.BOScreen;
import com.ldtteam.blockui.views.BOWindow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import static com.ldtteam.blockui.Pane.getFocus;

@Mixin(BOWindow.class)
public abstract class ColonyScreenMixin {

	@Shadow
	public abstract boolean onUnhandledKeyTyped(int ch, int key);

	@Shadow
	protected BOScreen screen;

	@Overwrite
	public boolean onKeyTyped(char ch, int key) {
		if (getFocus() == null) {
			if (key == 69) {
				this.screen.onClose();
			}
			return true;
		} else
			return getFocus().onKeyTyped(ch, key) || onUnhandledKeyTyped(ch, key);
	}
}
