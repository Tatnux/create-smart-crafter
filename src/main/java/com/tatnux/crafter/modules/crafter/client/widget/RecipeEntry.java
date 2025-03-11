package com.tatnux.crafter.modules.crafter.client.widget;

import com.tatnux.crafter.lib.gui.ItemRenderUtils;
import com.tatnux.crafter.modules.crafter.client.SmartCrafterScreen;
import com.tatnux.crafter.modules.crafter.data.CrafterRecipe;
import com.tatnux.crafter.modules.network.NetworkHandler;
import net.createmod.catnip.gui.widget.AbstractSimiWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class RecipeEntry extends AbstractSimiWidget {

    public static final int HOVER_COLOR = new Color(138, 148, 153).getRGB();
    public static final int HOVER_COLOR_DARK = new Color(147, 158, 163).getRGB();
    public static final int SELECTED_COLOR = new Color(91, 153, 194).getRGB();

    public byte index;
    private final SmartCrafterScreen parent;

    protected RecipeEntry(SmartCrafterScreen parent, byte index, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.parent = parent;
        this.index = index;

    }

    @Override
    protected void doRender(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int color = -1;
        if (this.parent.getMenu().contentHolder.selectedRecipeIndex == this.index) {
            color = SELECTED_COLOR;
        } else {
            boolean dark = this.index % 2 == 0;
            if (this.isMouseOver(mouseX, mouseY)) {
                color = dark ? HOVER_COLOR_DARK : HOVER_COLOR;
            } else if (dark) {
                color = Color.GRAY.getRGB();
            }
        }

        if (color != -1) {
            graphics.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), color);
        }

        NonNullList<CrafterRecipe> recipes = this.parent.getMenu().contentHolder.recipes;
        if (recipes == null || this.index >= recipes.size()) {
            return;
        }

        CrafterRecipe recipe = recipes.get(this.index);
        ItemStack output = recipe.getOutput();
        graphics.enableScissor(this.getX(), this.getY(), this.getX() + this.getWidth() - 1, this.getY() + this.getHeight());

        if (!output.isEmpty()) {
            ItemRenderUtils.renderItem(graphics, output, this.getX() + 1, this.getY() + 1, 13);
        }

        graphics.drawString(Minecraft.getInstance().font,
                output.isEmpty() ? Component.literal("<empty>") : output.getHoverName(),
                this.getX() + 16,
                this.getY() + 4,
                0xFFFFFF,
                false);

        graphics.disableScissor();
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        NetworkHandler.selectSlot(this.index);
    }
}
