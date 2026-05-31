package io.github.pouffy.cauldrontweaks.helpers.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public abstract class SafeBlockEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {

    @Override
    public final void render(@NotNull T be, float partialTicks, @NotNull PoseStack ms, @NotNull MultiBufferSource bufferSource, int light, int overlay) {
        if (isInvalid(be)) return;
        renderSafe(be, partialTicks, ms, bufferSource, light, overlay);
    }

    protected abstract void renderSafe(T be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay);

    public boolean isInvalid(T be) {
        return !be.hasLevel() || be.getBlockState().getBlock() == Blocks.AIR;
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(@NotNull T blockEntity) {
        if (blockEntity instanceof CachedRenderBBBlockEntity cbe) return cbe.getRenderBoundingBox();
        return BlockEntityRenderer.super.getRenderBoundingBox(blockEntity);
    }
}
