package com.tatnux.crafter.lib.gui;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

public class ItemRenderUtils {

    private ItemRenderUtils() {
    }

    public static void renderItem(GuiGraphics graphics, ItemStack itemStack, int x, int y, int scale) {
        Minecraft minecraft = Minecraft.getInstance();
        BakedModel bakedmodel = minecraft.getItemRenderer().getModel(itemStack, minecraft.level, minecraft.player, 0);
        PoseStack pose = graphics.pose();
        pose.pushPose();
        float halfScale = scale / 2f;
        pose.translate(x + halfScale, y + halfScale, (float)(150));

        pose.mulPose((new Matrix4f()).scaling(1.0F, -1.0F, 1.0F));
        pose.scale(scale, scale, scale);
        boolean flag = !bakedmodel.usesBlockLight();
        if (flag) {
            Lighting.setupForFlatItems();
        }

        minecraft.getItemRenderer().render(itemStack, ItemDisplayContext.GUI, false, pose, graphics.bufferSource(), 15728880, OverlayTexture.NO_OVERLAY, bakedmodel);
        graphics.flush();
        if (flag) {
            Lighting.setupFor3DItems();
        }
        pose.popPose();
    }

}
