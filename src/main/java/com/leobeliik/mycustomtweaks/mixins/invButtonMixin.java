package com.leobeliik.mycustomtweaks.mixins;

import net.dries007.tfc.client.screen.LargeVesselScreen;
import net.dries007.tfc.common.container.LargeVesselContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.violetmoon.quark.content.management.client.screen.widgets.MiniInventoryButton;

import java.util.List;
import java.util.function.Supplier;

@Mixin(MiniInventoryButton.class)
public class invButtonMixin extends Button {

    @Shadow @Final private AbstractContainerScreen<?> parent;

    @Shadow @Final private Supplier<List<Component>> tooltip;

    protected invButtonMixin(int pX, int pY, int pWidth, int pHeight, Component pMessage, OnPress pOnPress, CreateNarration pCreateNarration) {
        super(pX, pY, pWidth, pHeight, pMessage, pOnPress, pCreateNarration);
    }

    @Overwrite
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        boolean sortButton = this.tooltip.get().toString().contains("button.sort_container");
        boolean filterButton = this.tooltip.get().toString().contains("button.filter");
        if (sortButton || filterButton) {
            this.setX(this.parent.getGuiLeft() + this.parent.getXSize() - (filterButton ? 30 : 18));
            this.setY(this.parent.getGuiTop() + 5);
        }
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void onPress() {
        if (parent instanceof LargeVesselScreen vessel && this.tooltip.get().toString().contains("extract")) {
            if (vessel.getMenu().isSealed()) {
                return;
            }
        }
        super.onPress();
    }
}
