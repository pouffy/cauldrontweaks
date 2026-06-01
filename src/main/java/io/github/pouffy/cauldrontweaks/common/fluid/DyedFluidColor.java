package io.github.pouffy.cauldrontweaks.common.fluid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.pouffy.cauldrontweaks.CauldronTweaks;
import io.github.pouffy.cauldrontweaks.init.CauldronDataComponents;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.DyeItem;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public record DyedFluidColor(int rgb) {
    private static final Codec<DyedFluidColor> FULL_CODEC = RecordCodecBuilder.create((instance) -> instance.group(Codec.INT.fieldOf("rgb").forGetter(DyedFluidColor::rgb)).apply(instance, DyedFluidColor::new));
    public static final Codec<DyedFluidColor> CODEC = Codec.withAlternative(FULL_CODEC, Codec.INT, DyedFluidColor::new);
    public static final StreamCodec<ByteBuf, DyedFluidColor> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.INT, DyedFluidColor::rgb, DyedFluidColor::new);
    public static final int LEATHER_COLOR = -6265536;

    public static int getOrDefault(FluidStack stack, int defaultValue) {
        DyedFluidColor fluidColor = stack.get(CauldronDataComponents.DYED_COLOR);
        return fluidColor != null ? FastColor.ARGB32.opaque(fluidColor.rgb()) : defaultValue;
    }

    public static FluidStack applyDyes(FluidStack fluidStack, List<DyeItem> dyes) {
        if (!fluidStack.is(CauldronTweaks.DYEABLE_FLUID)) {
            return FluidStack.EMPTY;
        } else {
            FluidStack copied = fluidStack.copyWithAmount(1);
            int i = 0;
            int j = 0;
            int k = 0;
            int l = 0;
            int i1 = 0;
            DyedFluidColor fluidColor = copied.get(CauldronDataComponents.DYED_COLOR);
            if (fluidColor != null) {
                int j1 = FastColor.ARGB32.red(fluidColor.rgb());
                int k1 = FastColor.ARGB32.green(fluidColor.rgb());
                int l1 = FastColor.ARGB32.blue(fluidColor.rgb());
                l += Math.max(j1, Math.max(k1, l1));
                i += j1;
                j += k1;
                k += l1;
                i1++;
            }

            for (DyeItem dyeitem : dyes) {
                int j3 = dyeitem.getDyeColor().getTextureDiffuseColor();
                int i2 = FastColor.ARGB32.red(j3);
                int j2 = FastColor.ARGB32.green(j3);
                int k2 = FastColor.ARGB32.blue(j3);
                l += Math.max(i2, Math.max(j2, k2));
                i += i2;
                j += j2;
                k += k2;
                i1++;
            }

            int l2 = i / i1;
            int i3 = j / i1;
            int k3 = k / i1;
            float f = (float)l / (float)i1;
            float f1 = (float)Math.max(l2, Math.max(i3, k3));
            l2 = (int)((float)l2 * f / f1);
            i3 = (int)((float)i3 * f / f1);
            k3 = (int)((float)k3 * f / f1);
            int l3 = FastColor.ARGB32.color(0, l2, i3, k3);
            copied.set(CauldronDataComponents.DYED_COLOR, new DyedFluidColor(l3));
            return copied.copyWithAmount(fluidStack.getAmount());
        }
    }
}
