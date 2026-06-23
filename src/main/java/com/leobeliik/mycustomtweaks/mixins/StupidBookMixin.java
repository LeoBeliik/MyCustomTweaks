package com.leobeliik.mycustomtweaks.mixins;

import com.klikli_dev.modonomicon.client.gui.book.node.BookCategoryNodeScreen;
import com.klikli_dev.modonomicon.client.gui.book.node.BookParentNodeScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BookParentNodeScreen.class)
public abstract class StupidBookMixin extends Screen {

	protected StupidBookMixin(Component title) {
		super(title);
	}

	@Shadow
	public abstract BookCategoryNodeScreen getCurrentCategoryScreen();

	@Overwrite
	public boolean keyPressed(int key, int scanCode, int modifiers) {
		if (getCurrentCategoryScreen() instanceof BookCategoryNodeScreen && minecraft.options.keyInventory.matches(key, scanCode)) {
			super.onClose();
			return true;
		}
		if (this.getCurrentCategoryScreen().keyPressed(key, scanCode, modifiers)) {
			return true;
		}
		return super.keyPressed(key, scanCode, modifiers);
	}
}
