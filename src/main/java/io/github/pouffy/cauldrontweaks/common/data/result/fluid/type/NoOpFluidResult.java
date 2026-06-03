package io.github.pouffy.cauldrontweaks.common.data.result.fluid.type;

import com.mojang.serialization.MapCodec;
import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import io.github.pouffy.cauldrontweaks.common.data.result.fluid.CauldronFluidResult;
import io.github.pouffy.cauldrontweaks.common.data.result.fluid.CauldronFluidResultType;
import io.github.pouffy.cauldrontweaks.init.CauldronFluidResults;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public enum NoOpFluidResult implements CauldronFluidResult {
    INSTANCE;

    public static final MapCodec<NoOpFluidResult> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public CauldronFluidResultType<?> getType() {
        return CauldronFluidResults.NO_OP.get();
    }

    @Override
    public void alterTank(CauldronBlockEntity cauldron, ItemStack usedItem) {}

    @Override
    public FluidStack getFluidResult(ItemStack usedItem, FluidStack usedFluid) {
        return usedFluid;
    }
}
