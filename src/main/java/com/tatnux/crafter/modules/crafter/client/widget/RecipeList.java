package com.tatnux.crafter.modules.crafter.client.widget;

import com.tatnux.crafter.modules.crafter.client.SmartCrafterScreen;
import net.createmod.catnip.gui.widget.AbstractSimiWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RecipeList extends AbstractSimiWidget {

    public static final int SIZE = 5;

    private final SmartCrafterScreen parent;
    private int scrollOffset = 0;

    private final List<RecipeEntry> entries = new ArrayList<>(SIZE);

    public RecipeList(SmartCrafterScreen parent, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.parent = parent;
    }

    public void init() {
        int lineHeight = this.getHeight() / SIZE;
        for (byte i = 0; i < SIZE; i++) {
            RecipeEntry recipeEntry = new RecipeEntry(this.parent,
                    i,
                    this.getX(),
                    this.getY() + i * lineHeight,
                    this.getWidth() - 5,
                    lineHeight);
            this.entries.add(recipeEntry);
            this.parent.addRenderableWidget(recipeEntry);
        }
    }


    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (delta > 0 && this.scrollOffset > 0) {
            this.scrollOffset--; // Scroll vers le haut
            this.entries.forEach(recipeEntry -> recipeEntry.index--);
        } else if (delta < 0 && this.scrollOffset < this.parent.getMenu().contentHolder.recipes.size() - SIZE) {
            this.entries.forEach(recipeEntry -> recipeEntry.index++);
            this.scrollOffset++; // Scroll vers le bas
        }
        return true; // Scroll géré
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        entries.stream().filter(recipeEntry -> recipeEntry.isMouseOver(mouseX, mouseY)).forEach(recipeEntry -> {
            recipeEntry.onClick(mouseX, mouseY);
        });
    }

    @Override
    protected void doRender(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int endX = this.getX() + this.getWidth();
        int endY = this.getY() + this.getHeight();
        int y = this.getY() + ((this.getHeight() - 15) / SIZE) * this.scrollOffset;
        graphics.fill(endX - 5, this.getY(), endX, endY, Color.LIGHT_GRAY.getRGB());
//        AllGuiTextures.TRAIN_PROMPT_L.render(graphics, this.getX() + this.getWidth() - 5, y);
//        AllGuiTextures.TRAIN_PROMPT_R.render(graphics, this.getX() + this.getWidth() - 2, y);
        graphics.blitNineSliced(new ResourceLocation("create", "textures/gui/widgets.png"),
                this.getX() + this.getWidth() - 5, y,
                6, 27,
                0, 4,
                6, 16,
                8, 209
        );
//        int offsetX = 2;
//        int offsetY = 1;
//        graphics.enableScissor(this.getX(), this.getY(), this.getX() + this.getWidth() - 1, this.getY() + this.getHeight());
//        for (int i = this.scrollOffset; i < this.parent.recipes.size() && i < this.scrollOffset + 5; i++) {
//            ItemStack itemStack = this.parent.recipes.get(i);
//
//            int x = this.getX() + offsetX;
//            int y = this.getY() + offsetY + (i - this.scrollOffset) * LINE_HEIGHT;
//
//            if (!itemStack.isEmpty()) {
//                graphics.renderItem(itemStack, x, y);
//            }
//            graphics.drawString(Minecraft.getInstance().font,
//                    itemStack.isEmpty() ? Component.literal("<empty>") : itemStack.getHoverName(),
//                    x + 19,
//                    y + 4,
//                    0xFFFFFF,
//                    false);
//
////            renderScrollingString(
////                    graphics,
////                    Minecraft.getInstance().font,
////                    itemStack.isEmpty() ? Component.literal("<empty>") : itemStack.getHoverName(),
////                    x + 19,
////                    y + 2,
////                    x + 108,
////                    y + 14,
////                    0xFFFFFF);
//
//        }
//        graphics.disableScissor();
    }
}
