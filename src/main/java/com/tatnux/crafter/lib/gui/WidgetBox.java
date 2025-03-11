package com.tatnux.crafter.lib.gui;

import net.createmod.catnip.gui.widget.AbstractSimiWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import org.jetbrains.annotations.NotNull;

public class WidgetBox extends AbstractSimiWidget {

    private final int color;

    public WidgetBox(AbstractWidget widget, int color) {
        super(widget.getX() - 1, widget.getY() - 1, widget.getWidth() + 2, widget.getHeight() + 2);
        this.color = color;
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.renderOutline(this.getX(), this.getY(), this.getWidth(), this.getHeight(), this.color);
        int endX = this.getX() + this.getWidth();
        int endY = this.getY() + this.getHeight();
        graphics.fill(this.getX() + 1, endY - 2, this.getX() + 2, endY - 1, this.color);
        graphics.fill(endX - 2, this.getY() + 1, endX - 1, this.getY() + 2, this.color);
    }
}
