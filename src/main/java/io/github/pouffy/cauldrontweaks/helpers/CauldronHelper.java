package io.github.pouffy.cauldrontweaks.helpers;

import io.github.pouffy.cauldrontweaks.common.event.CauldronFluidEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.fluids.FluidStack;

public class CauldronHelper {


    public static FluidStack getFluidForCauldron(BlockEntity entity) {
        var event = NeoForge.EVENT_BUS.post(new CauldronFluidEvent(entity.getLevel(), entity.getBlockPos(), entity.getBlockState()));
        return event.getFluid();
    }

    public static int cauldronLevelToAmount(int level) {
        return switch (level) {
            case 1 -> 250;
            case 2 -> 500;
            case 3 -> 1000;
            default -> 0;
        };
    }
}
