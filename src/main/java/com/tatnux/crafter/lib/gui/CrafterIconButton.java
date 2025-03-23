package com.tatnux.crafter.lib.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.item.TooltipHelper;
import net.createmod.catnip.gui.element.ScreenElement;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Objects;
import java.util.function.BooleanSupplier;

public class CrafterIconButton extends IconButton {

    private BooleanSupplier disabled = () -> false;

    private boolean disabledState = false;

    private Component tooltip;
    private Component[] description;

    private boolean tooltipWhenDisabled = true;

    public CrafterIconButton(int x, int y, ScreenElement icon) {
        super(x, y, icon);
    }

    public CrafterIconButton withDisabled(BooleanSupplier disabled) {
        this.disabled = disabled;
        return this;
    }

    public CrafterIconButton withTooltip(Component tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    public CrafterIconButton tooltipWhenDisabled(boolean tooltipWhenDisabled) {
        this.tooltipWhenDisabled = tooltipWhenDisabled;
        return this;
    }

    public CrafterIconButton withDescription(Component... description) {
        this.description = description;
        return this;
    }

    @Override
    public void tick() {
        if (Objects.nonNull(this.tooltip)) {
            this.getToolTip().clear();
            if (this.tooltipWhenDisabled || !this.disabledState) {
                this.getToolTip().add(this.tooltip);
                if (Objects.nonNull(this.description)) {
                    if (Screen.hasShiftDown()) {
                        for (Component component : this.description) {
                            this.getToolTip().add(component);
                        }
                    } else {
                        this.getToolTip().add(TooltipHelper.holdShift(FontHelper.Palette.YELLOW, Screen.hasShiftDown()));
                    }
                }
            }
        }
    }

    @Override
    public void doRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            this.disabledState = this.disabled.getAsBoolean();
            this.isHovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;

            AllGuiTextures button = this.disabledState ? AllGuiTextures.BUTTON_DOWN
                    : this.isMouseOver(mouseX, mouseY) ? AllGuiTextures.BUTTON_HOVER : AllGuiTextures.BUTTON;

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawBg(graphics, button);
            this.icon.render(graphics, this.getX() + 1, this.getY() + 1);
        }
    }

    @Override
    protected boolean clicked(double pMouseX, double pMouseY) {
        return !this.disabledState && super.clicked(pMouseX, pMouseY);
    }
}
