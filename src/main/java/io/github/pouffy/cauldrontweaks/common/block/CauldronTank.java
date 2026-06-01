package io.github.pouffy.cauldrontweaks.common.block;

import io.github.pouffy.cauldrontweaks.helpers.blockentity.SmartFluidTank;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

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

}
