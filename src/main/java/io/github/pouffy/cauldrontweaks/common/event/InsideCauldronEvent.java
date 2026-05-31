package io.github.pouffy.cauldrontweaks.common.event;

import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import io.github.pouffy.cauldrontweaks.common.block.CauldronTank;
import io.github.pouffy.cauldrontweaks.helpers.blockentity.SmartFluidTank;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.EntityEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;

public class InsideCauldronEvent extends EntityEvent {
    private final CauldronBlockEntity cauldron;

    public InsideCauldronEvent(Entity entity, CauldronBlockEntity cauldron) {
        super(entity);
        this.cauldron = cauldron;
    }

    public FluidStack getFluid() {
        return cauldron.getFluidStack();
    }

    public SmartFluidTank getTank() {
        return cauldron.getTank();
    }

    public BlockState getBlockState() { return cauldron.getBlockState(); }
}
