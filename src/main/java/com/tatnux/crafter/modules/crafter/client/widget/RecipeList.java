package com.tatnux.crafter.modules.crafter.client.widget;

import com.tatnux.crafter.modules.crafter.client.SmartCrafterScreen;
import net.createmod.catnip.gui.widget.AbstractSimiWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

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
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (scrollY > 0 && this.scrollOffset > 0) {
            this.scrollOffset--;
            this.entries.forEach(recipeEntry -> recipeEntry.index--);
        } else if (scrollY < 0 && this.scrollOffset < this.parent.getMenu().contentHolder.recipes.size() - SIZE) {
            this.entries.forEach(recipeEntry -> recipeEntry.index++);
            this.scrollOffset++;
        }
        return true;
    }

    @Override
    protected boolean clicked(double mouseX, double mouseY) {
        entries.stream().filter(recipeEntry -> recipeEntry.isMouseOver(mouseX, mouseY)).forEach(recipeEntry -> {
            recipeEntry.onClick(mouseX, mouseY);
        });
        return true;
    }

    @Override
    protected void doRender(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int endX = this.getX() + this.getWidth();
        int endY = this.getY() + this.getHeight();
        int y = this.getY() + ((this.getHeight() - 15) / SIZE) * this.scrollOffset;

        graphics.fill(endX - 5, this.getY(), endX, endY, 0xFFD3D3D3); // Light Gray (RGBA)

        ResourceLocation texture = ResourceLocation.tryBuild("create", "textures/gui/widgets.png");

        graphics.blit(texture,
                endX - 5, y,
                0, 209,
                6, 27,
                256, 256
        );
    }


}
