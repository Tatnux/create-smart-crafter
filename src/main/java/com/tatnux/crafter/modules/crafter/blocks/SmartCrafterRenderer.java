package com.tatnux.crafter.modules.crafter.blocks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import static com.simibubi.create.content.kinetics.base.DirectionalKineticBlock.FACING;
import static com.simibubi.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING;
import static com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer.standardKineticRotationTransform;

public class SmartCrafterRenderer extends SafeBlockEntityRenderer<SmartCrafterBlockEntity> {

    public SmartCrafterRenderer(BlockEntityRendererProvider.Context ignoredContext) {
    }

    @Override
    protected void renderSafe(SmartCrafterBlockEntity be, float partialTicks, PoseStack ms,
                              MultiBufferSource buffer, int light, int overlay) {
        BlockState blockState = be.getBlockState();
        VertexConsumer vb = buffer.getBuffer(RenderType.solid());

        if (!VisualizationManager.supportsVisualization(be.getLevel())) return;

        SuperByteBuffer superBuffer = CachedBuffers.partial(AllPartialModels.SHAFTLESS_COGWHEEL, blockState);
        standardKineticRotationTransform(superBuffer, be, light);
        superBuffer.rotateCentered((float) (blockState.getOptionalValue(HORIZONTAL_FACING).orElseGet(() -> blockState.getValue(FACING))
                .getAxis() != Direction.Axis.X ? 0 : (Math.PI / 2)), Direction.UP);
        superBuffer.rotateCentered((float) (Math.PI / 2), Direction.EAST);
        superBuffer.renderInto(ms, vb);
    }

}
