package io.github.pouffy.cauldrontweaks.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.platform.CatnipServices;
import net.createmod.catnip.platform.services.ModFluidHelper;
import net.createmod.catnip.render.FluidRenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

@SuppressWarnings("unchecked")
public class ClientFluidHelper {

    public static void renderFluidBox(FluidStack fluid, float xMin, float yMin, float zMin, float xMax, float yMax, float zMax, VertexConsumer builder, PoseStack ms, int light, boolean renderBottom, boolean invertGasses) {
        ModFluidHelper<FluidStack> helper = (ModFluidHelper<FluidStack>) CatnipServices.FLUID_HELPER;

        TextureAtlasSprite fluidTexture = helper.getStillTextureOrMissing(fluid);
        int color = getColor(fluid, null, null);

        int blockLightIn = (light >> 4) & 0xF;
        int luminosity = Math.max(blockLightIn, helper.getLuminosity(fluid));
        light = (light & 0xF00000) | luminosity << 4;

        Vec3 center = new Vec3(xMin + (xMax - xMin) / 2, yMin + (yMax - yMin) / 2, zMin + (zMax - zMin) / 2);
        ms.pushPose();
        if (invertGasses && helper.isLighterThanAir(fluid)) {
            ms.translate(center.x, center.y, center.z);
            ms.mulPose(Axis.XP.rotationDegrees(180));
            ms.translate(-center.x, -center.y, -center.z);
        }

        for (Direction side : Iterate.directions) {
            if (side == Direction.DOWN && !renderBottom)
                continue;

            boolean positive = side.getAxisDirection() == Direction.AxisDirection.POSITIVE;
            if (side.getAxis().isHorizontal()) {
                if (side.getAxis() == Direction.Axis.X) {
                    FluidRenderHelper.renderStillTiledFace(side, zMin, yMin, zMax, yMax, positive ? xMax : xMin, builder, ms, light, color, fluidTexture);
                } else {
                    FluidRenderHelper.renderStillTiledFace(side, xMin, yMin, xMax, yMax, positive ? zMax : zMin, builder, ms, light, color, fluidTexture);
                }
            } else {
                FluidRenderHelper.renderStillTiledFace(side, xMin, zMin, xMax, zMax, positive ? yMax : yMin, builder, ms, light, color, fluidTexture);
            }
        }

        ms.popPose();
    }

    public static int getColor(FluidStack stack, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos) {
        Fluid fluid = stack.getFluid();
        IClientFluidTypeExtensions extension = IClientFluidTypeExtensions.of(fluid);
        int dyeColor = 0x00000000;
        if (stack.has(DataComponents.DYED_COLOR)) {
            Color jColor = new Color(stack.get(DataComponents.DYED_COLOR).rgb(), false);
            int r = jColor.getRed(), g = jColor.getGreen(), b = jColor.getBlue();
            int alpha = 255;
            dyeColor = new Color(r, g, b, alpha).getRGB();
        }
        if (level == null || pos == null) {
            int color = extension.getTintColor(stack);
            if (stack.has(DataComponents.DYED_COLOR)) {
                color = dyeColor;
            }
            return color;
        }
        int tintColor = extension.getTintColor(fluid.defaultFluidState(), level, pos);
        if (stack.has(DataComponents.DYED_COLOR)) {
            tintColor = dyeColor;
        }
        return tintColor;
    }

    public static Color mix(Color c1, Color c2, float ratio) {
        if ( ratio > 1f ) ratio = 1f;
        else if ( ratio < 0f ) ratio = 0f;
        float iRatio = 1.0f - ratio;

        int i1 = c1.getRGB();
        int i2 = c2.getRGB();

        int a1 = (i1 >> 24 & 0xff);
        int r1 = ((i1 & 0xff0000) >> 16);
        int g1 = ((i1 & 0xff00) >> 8);
        int b1 = (i1 & 0xff);

        int a2 = (i2 >> 24 & 0xff);
        int r2 = ((i2 & 0xff0000) >> 16);
        int g2 = ((i2 & 0xff00) >> 8);
        int b2 = (i2 & 0xff);

        int a = (int)((a1 * iRatio) + (a2 * ratio));
        int r = (int)((r1 * iRatio) + (r2 * ratio));
        int g = (int)((g1 * iRatio) + (g2 * ratio));
        int b = (int)((b1 * iRatio) + (b2 * ratio));

        return new Color( a << 24 | r << 16 | g << 8 | b );
    }
}
