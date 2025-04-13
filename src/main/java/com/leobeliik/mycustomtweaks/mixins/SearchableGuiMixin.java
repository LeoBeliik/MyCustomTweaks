package com.leobeliik.mycustomtweaks.mixins;

import com.mna.gui.base.SearchableGui;
import com.mna.gui.entity.GuiWanderingWizard;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SearchableGui.class)
public abstract class SearchableGuiMixin<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

    @Shadow protected EditBox searchBox;

    @Shadow protected Component searchValue;

    public SearchableGuiMixin(T pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Shadow protected abstract void searchTermChanged(String s);

    @Overwrite(remap = false)
    protected void initSearch(int searchX, int searchY, int width, int height) {
        assert this.minecraft != null;
        this.searchBox = new EditBox(this.minecraft.font, searchX, searchY, width, height, this.searchValue);
        this.searchBox.setMaxLength(60);
        this.searchBox.setResponder(this::searchTermChanged);
    }

    @Overwrite(remap = false)
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.searchBox != null) {
            if (this.searchBox.isMouseOver(mouseX, mouseY)) {
                this.searchBox.setFocused(true);
                this.setFocused(this.searchBox);
            } else {
                this.searchBox.setFocused(false);
                this.setFocused(false);
            }
            if (button == 1 && this.searchBox.isMouseOver(mouseX, mouseY)) {
                this.searchBox.setValue("");
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Overwrite(remap = false)
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 257 || keyCode == 335) {
            this.searchBox.setFocused(false);
            return true;
        }
        if (!this.searchBox.isFocused() && minecraft != null && minecraft.options.keyInventory.matches(keyCode, scanCode)) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
