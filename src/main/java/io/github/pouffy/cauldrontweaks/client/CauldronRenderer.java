package io.github.pouffy.cauldrontweaks.client;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import io.github.pouffy.cauldrontweaks.helpers.blockentity.SafeBlockEntityRenderer;
import net.createmod.catnip.platform.NeoForgeCatnipServices;
import net.createmod.catnip.render.PonderRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

public class CauldronRenderer extends SafeBlockEntityRenderer<CauldronBlockEntity> {

    public CauldronRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    protected void renderSafe(CauldronBlockEntity cauldron, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int light, int overlay) {
        renderFluid(cauldron, partialTicks, matrixStack, buffer, light);
    }

    public void renderFluid(CauldronBlockEntity cauldron, float pt, PoseStack ms, MultiBufferSource buffer, int light) {
        var fluidLevel = cauldron.getFluidLevel();
        if (fluidLevel == null) return;
        var tank = cauldron.getTank();
        var fluidStack = tank.getFluid();
        if (fluidStack.isEmpty()) return;

        float capHeight = 1 / 16f;
        float tankHullWidth = 0.1f / 16f + 1 / 128f;
        float minPuddleHeight = 4 / 16f;
        float totalHeight = 1 - 2 * capHeight - minPuddleHeight;

        float level = fluidLevel.getValue(pt) + 0.0225f / 16f;
        if (level < 1 / (512f * totalHeight)) return;
        float clampedLevel = Mth.clamp(level * totalHeight, 0, totalHeight);
        float xMax = tankHullWidth + 1 - 2 * tankHullWidth, yMin = totalHeight + capHeight + minPuddleHeight - clampedLevel, yMax = yMin + clampedLevel, zMax = tankHullWidth + 1 - 2 * tankHullWidth;

        ms.pushPose();
        ms.translate(0, clampedLevel - totalHeight, 0);
        ClientFluidHelper.renderFluidBox(fluidStack, tankHullWidth, yMin, tankHullWidth, xMax, yMax, zMax, buffer.getBuffer(PonderRenderTypes.fluid()), ms, light, false, false);
        ms.popPose();
    }
}
