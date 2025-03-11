package com.tatnux.crafter.lib.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tatnux.crafter.SmartCrafter;
import net.createmod.catnip.gui.UIRenderHelper;
import net.createmod.catnip.gui.element.ScreenElement;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum GuiTexture implements ScreenElement {

    CRAFTER("crafter", 224, 173);


    public static final int FONT_COLOR = 0x575F7A;

    public final ResourceLocation location;
    public final int width, height;
    public final int startX, startY;

    GuiTexture(String location, int width, int height) {
        this(location, 0, 0, width, height);
    }

    GuiTexture(int startX, int startY) {
        this("icons", startX * 16, startY * 16, 16, 16);
    }

    GuiTexture(String location, int startX, int startY, int width, int height) {
        this(SmartCrafter.MOD_ID, location, startX, startY, width, height);
    }

    GuiTexture(String namespace, String location, int startX, int startY, int width, int height) {
        this.location = new ResourceLocation(namespace, "textures/gui/" + location + ".png");
        this.width = width;
        this.height = height;
        this.startX = startX;
        this.startY = startY;
    }

    @OnlyIn(Dist.CLIENT)
    public void bind() {
        RenderSystem.setShaderTexture(0, location);
    }

    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y) {
        graphics.blit(location, x, y, startX, startY, width, height);
    }

    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y, Color c) {
        bind();
        UIRenderHelper.drawColoredTexture(graphics, c, x, y, startX, startY, width, height);
    }
}
