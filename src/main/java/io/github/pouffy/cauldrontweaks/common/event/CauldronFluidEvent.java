package io.github.pouffy.cauldrontweaks.common.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.fluids.FluidStack;

public class CauldronFluidEvent extends BlockEvent {

    private FluidStack fluid = FluidStack.EMPTY;

    public CauldronFluidEvent(LevelAccessor level, BlockPos pos, BlockState state) {
        super(level, pos, state);
    }

    public void setFluid(FluidStack stack) {
        this.fluid = stack;
    }

    public FluidStack getFluid() {
        return fluid;
    }
}
