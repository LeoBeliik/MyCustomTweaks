package com.leobeliik.mycustomtweaks.mixins;

import me.desht.pneumaticcraft.client.gui.SmartChestScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import org.checkerframework.common.aliasing.qual.Unique;
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

    @Shadow
    @Final
    private AbstractContainerScreen<?> parent;

    @Shadow
    @Final
    private Supplier<List<Component>> tooltip;

    @Shadow @Final private int startX;

    @Unique private int i = 0;

    protected invButtonMixin(int pX, int pY, int pWidth, int pHeight, Component pMessage, OnPress pOnPress, CreateNarration pCreateNarration) {
        super(pX, pY, pWidth, pHeight, pMessage, pOnPress, pCreateNarration);
    }

    @Overwrite
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (parent instanceof SmartChestScreen) {
            boolean sortButton = this.tooltip.get().toString().contains("button.sort_container");
            boolean filterButton = this.tooltip.get().toString().contains("button.filter");
            if (!sortButton && !filterButton && i == 0) {
                i++;
                this.setX(this.getX() + 55);
                this.setY(this.getY() + 10);
            }
        }
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }
}
