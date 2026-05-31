package io.github.pouffy.cauldrontweaks.common.block;

import io.github.pouffy.cauldrontweaks.CauldronTweaks;
import io.github.pouffy.cauldrontweaks.helpers.blockentity.SmartFluidTank;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.component.DyedItemColor;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class CauldronTank extends SmartFluidTank {


    private int luminosity = 0;
    private final CauldronBlockEntity cauldron;

    public CauldronTank(Consumer<FluidStack> updateCallback, CauldronBlockEntity cauldron) {
        super(1000, updateCallback);
        this.cauldron = cauldron;
    }

    public int getLuminosity() {
        return luminosity;
    }

    @Override
    protected void onContentsChanged() {
        super.onContentsChanged();
        int luminosity = 0;
        if (!getFluid().isEmpty()) {
            luminosity = getFluid().getFluid().getFluidType().getLightLevel();
        }
        this.luminosity = luminosity;
    }

    @Override
    public void setFluid(@NotNull FluidStack stack) {
        super.setFluid(stack);
        int luminosity = 0;
        if (!getFluid().isEmpty()) {
            luminosity = getFluid().getFluid().getFluidType().getLightLevel();
        }
        this.luminosity = luminosity;
    }

    public FluidTank readFromNBT(HolderLookup.Provider lookupProvider, CompoundTag nbt) {
        this.fluid = FluidStack.parseOptional(lookupProvider, nbt.getCompound("Fluid"));

        int prevLum = luminosity;
        luminosity = nbt.getInt("Luminosity");

        if (luminosity != prevLum && cauldron.hasLevel()) cauldron.getLevel().getChunkSource().getLightEngine().checkBlock(cauldron.getBlockPos());
        return this;
    }

    public CompoundTag writeToNBT(HolderLookup.Provider lookupProvider, CompoundTag nbt) {
        if (!this.fluid.isEmpty()) {
            nbt.put("Fluid", this.fluid.save(lookupProvider));
        }
        nbt.putInt("Luminosity", this.luminosity);
        return nbt;
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
            DyedItemColor dyeditemcolor = copied.get(DataComponents.DYED_COLOR);
            if (dyeditemcolor != null) {
                int j1 = FastColor.ARGB32.red(dyeditemcolor.rgb());
                int k1 = FastColor.ARGB32.green(dyeditemcolor.rgb());
                int l1 = FastColor.ARGB32.blue(dyeditemcolor.rgb());
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
            boolean flag = dyeditemcolor == null || dyeditemcolor.showInTooltip();
            copied.set(DataComponents.DYED_COLOR, new DyedItemColor(l3, flag));
            return copied.copyWithAmount(fluidStack.getAmount());
        }
    }
}
