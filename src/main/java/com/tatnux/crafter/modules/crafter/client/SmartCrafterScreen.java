package com.tatnux.crafter.modules.crafter.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.tatnux.crafter.lib.gui.CrafterIconButton;
import com.tatnux.crafter.lib.gui.GuiTexture;
import com.tatnux.crafter.lib.gui.WidgetBox;
import com.tatnux.crafter.modules.crafter.SmartCrafterModule;
import com.tatnux.crafter.modules.crafter.blocks.SmartCrafterMenu;
import com.tatnux.crafter.modules.crafter.client.widget.RecipeList;
import com.tatnux.crafter.modules.crafter.data.CraftMode;
import com.tatnux.crafter.modules.crafter.data.CrafterRecipe;
import com.tatnux.crafter.modules.crafter.data.GhostSlots;
import com.tatnux.crafter.modules.network.NetworkHandler;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.gui.widget.AbstractSimiWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.awt.*;

import static com.simibubi.create.foundation.gui.AllGuiTextures.PLAYER_INVENTORY;

public class SmartCrafterScreen extends AbstractSimiContainerScreen<SmartCrafterMenu> {

    public static final int BOX_COLOR = new Color(50, 100, 181).getRGB();
    protected static final GuiTexture BG = GuiTexture.CRAFTER;
    protected static final AllGuiTextures PLAYER = AllGuiTextures.PLAYER_INVENTORY;
    private float lastRotate = 0;

    public SmartCrafterScreen(SmartCrafterMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void init() {
        this.setWindowSize(30 + BG.width, BG.height + PLAYER.getHeight() - 24);
        this.setWindowOffset(-11, 0);
        super.init();

        int yFooter = this.topPos + BG.height - 47;

        this.addRenderableWidget(new IconButton(
                this.leftPos + 30 + BG.width - 33,
                yFooter,
                AllIcons.I_CONFIRM)
                .withCallback(() -> {
                    this.minecraft.player.closeContainer();
                }));

        AbstractSimiWidget resetButton = new CrafterIconButton(
                this.leftPos + 185,
                this.topPos + 56,
                AllIcons.I_DISABLE)
                .withTooltip(Component.literal("Reset the recipe"))
                .tooltipWhenDisabled(false)
                .withDisabled(this.menu::isCraftingEmpty)
                .withCallback(() -> NetworkHandler.resetRecipe(this.menu.contentHolder.selectedRecipeIndex));
        this.addRenderableWidget(resetButton);
        this.addRenderableWidget(new WidgetBox(resetButton, BOX_COLOR));

        int buttonsX = this.leftPos + 40;

        AbstractSimiWidget extButton = new CrafterIconButton(
                buttonsX,
                yFooter,
                AllIcons.I_PRIORITY_VERY_LOW)
                .withDisabled(() -> this.menu.contentHolder.getSelectedRecipe().getCraftMode() == CraftMode.EXT)
                .withTooltip(Component.literal("Ext"))
                .withDescription(Component.literal("All items output will go in the result slots."))
                .withCallback(() -> this.updateCraftMode(CraftMode.EXT));

        AbstractSimiWidget extCButton = new CrafterIconButton(
                buttonsX + 18,
                yFooter,
                AllIcons.I_PRIORITY_LOW)
                .withDisabled(() -> this.menu.contentHolder.getSelectedRecipe().getCraftMode() == CraftMode.EXTC)
                .withTooltip(Component.literal("Secondary Int"))
                .withDescription(Component.literal("The primary result will go in the result slots"), Component.literal("and the secondary item in the ingredients slots."))
                .withCallback(() -> this.updateCraftMode(CraftMode.EXTC));

        AbstractSimiWidget intButton = new CrafterIconButton(
                buttonsX + 18 + 18,
                yFooter,
                AllIcons.I_ROTATE_CCW)
                .withDisabled(() -> this.menu.contentHolder.getSelectedRecipe().getCraftMode() == CraftMode.INT)
                .withTooltip(Component.literal("Int"))
                .withDescription(Component.literal("All items output will go back in the ingredients slots."))
                .withCallback(() -> this.updateCraftMode(CraftMode.INT));

        int keepGap = 18 * 4;

        AbstractSimiWidget keepButton = new CrafterIconButton(
                buttonsX + keepGap,
                yFooter,
                AllIcons.I_WHITELIST_OR)
                .withDisabled(() -> this.menu.contentHolder.keepMode)
                .withTooltip(Component.literal("Keep Mode"))
                .withDescription(Component.literal("1 item will remain in each ingredients slots."))
                .withCallback(() -> this.updateKeepMode(true));

        AbstractSimiWidget dontKeepButton = new CrafterIconButton(
                buttonsX + keepGap + 18,
                yFooter,
                AllIcons.I_WHITELIST_NOT)
                .withDisabled(() -> !this.menu.contentHolder.keepMode)
                .withTooltip(Component.literal("Out Mode"))
                .withDescription(Component.literal("No item is kept in the ingredients slots."))
                .withCallback(() -> this.updateKeepMode(false));

        int ghostGap = 18 * 7;

        AbstractSimiWidget ghostSaveButton = new CrafterIconButton(
                buttonsX + ghostGap,
                yFooter,
                AllIcons.I_CONFIG_SAVE)
                .withTooltip(Component.literal("Remember Items"))
                .withCallback(() -> NetworkHandler.updateGhostItems(false));

        AbstractSimiWidget ghostForgetButton = new CrafterIconButton(
                buttonsX + ghostGap + 18 + 4,
                yFooter,
                AllIcons.I_TRASH)
                .withDisabled(() -> this.menu.contentHolder.ghostSlots.isEmpty())
                .withTooltip(Component.literal("Forget Items"))
                .withCallback(() -> NetworkHandler.updateGhostItems(true));

        this.addRenderableWidgets(extButton, extCButton, intButton, keepButton, dontKeepButton, ghostSaveButton, ghostForgetButton);


        RecipeList recipeList = new RecipeList(this, this.leftPos + 38, this.topPos - 2, 110, 75);
        this.addRenderableWidget(recipeList);
        recipeList.init();
    }

    @Override
    public <T extends GuiEventListener & Renderable & NarratableEntry> @NotNull T addRenderableWidget(@NotNull T pWidget) {
        return super.addRenderableWidget(pWidget);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        int x = this.leftPos + this.imageWidth - BG.width;
        int y = this.topPos - 23;

        BG.render(graphics, x, y);
        graphics.drawString(this.font, this.title, x + 15, y + 4, Color.decode("#BEBEBE").getRGB(), false);

        int invX = this.getLeftOfCentered(PLAYER_INVENTORY.getWidth());
        int invY = this.topPos + this.imageHeight - PLAYER.getHeight();
        this.renderPlayerInventory(graphics, invX, invY);
        this.renderCrafter(graphics, x + BG.width + 55, y + BG.height + 15, partialTicks);
        this.drawGhostSlots(graphics);
    }

    private void renderCrafter(GuiGraphics graphics, int x, int y, float partialTicks) {
        PoseStack ms = graphics.pose();
        TransformStack.of(ms)
                .pushPose()
                .translate(x, y, 100)
                .scale(50)
                .rotateX(-22)
                .rotateY(-202);

        GuiGameElement.of(SmartCrafterModule.SMART_CRAFTER
                        .getDefaultState())
                .render(graphics);

        this.lastRotate = this.lastRotate % 360;

        TransformStack.of(ms)
                .pushPose();
        GuiGameElement.of(AllPartialModels.SHAFTLESS_COGWHEEL)
                .rotateBlock(90, Math.abs(this.menu.contentHolder.getSpeed()) > 0 ? this.lastRotate = this.lastRotate + this.menu.contentHolder.getSpeed() / 24 : 22, 0)
                .render(graphics);
        ms.popPose();
        ms.popPose();
    }

    private void drawGhostSlots(GuiGraphics graphics) {
        com.mojang.blaze3d.platform.Lighting.setupFor3DItems();
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.translate(this.leftPos, this.topPos, 100.0F);

        GhostSlots ghostSlots = this.menu.contentHolder.ghostSlots;
        GlStateManager._enableDepthTest();
        GlStateManager._disableBlend();


        for (GhostSlots.GhostSlotEntry entry : ghostSlots.getEntries()) {
            ItemStack item = entry.getItem();
            if (!item.isEmpty()) {
                for (Byte slotIndex : entry.getSlots()) {
                    Slot slot = this.menu.getSlot(slotIndex);
                    if (!slot.hasItem()) {
                        renderGhostItem(graphics, item, slot);
                    }
                }
            }
        }

        matrixStack.popPose();
    }

    private static void renderGhostItem(GuiGraphics graphics, ItemStack stack, Slot slot) {
        renderAndDecorateItem(graphics, stack, slot.x, slot.y);

//                    RenderSystem.disableLighting();// @todo 1.18
        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, PLAYER_INVENTORY.location);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.5F);
        drawTexturedModalRect(graphics.pose(), slot.x, slot.y, 8, 18, 16, 16);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    public static void renderAndDecorateItem(GuiGraphics graphics, ItemStack stack, int x, int y) {
        graphics.renderItem(stack, x, y, x * y * 31);
        graphics.renderItemDecorations(Minecraft.getInstance().font, stack, x, y, null);
    }

    public static void drawTexturedModalRect(PoseStack poseStack, int x, int y, int u, int v, int width, int height) {
        Matrix4f matrix = poseStack.last().pose();
        float zLevel = 0.01F;
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(matrix, (float) (x), (float) (y + height), zLevel).uv((float) (u) * f, (float) (v + height) * f1).endVertex();
        buffer.vertex(matrix, (float) (x + width), (float) (y + height), zLevel).uv((float) (u + width) * f, (float) (v + height) * f1).endVertex();
        buffer.vertex(matrix, (float) (x + width), (float) (y), zLevel).uv((float) (u + width) * f, (float) (v) * f1).endVertex();
        buffer.vertex(matrix, (float) (x), (float) (y), zLevel).uv((float) (u) * f, (float) (v) * f1).endVertex();
        tessellator.end();
    }

    private void updateCraftMode(CraftMode mode) {
        CrafterRecipe selectedRecipe = this.menu.contentHolder.getSelectedRecipe();
        if (selectedRecipe.getCraftMode() != mode) {
            NetworkHandler.selectCraftMode(mode);
        }
    }

    private void updateKeepMode(boolean keepMode) {
        if (this.menu.contentHolder.keepMode != keepMode) {
            NetworkHandler.setKeepMode(keepMode);
        }
    }

}
