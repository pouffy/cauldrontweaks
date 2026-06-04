package io.github.pouffy.cauldrontweaks.common.data.result.fluid.type;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.pouffy.cauldrontweaks.CauldronTweaks;
import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import io.github.pouffy.cauldrontweaks.common.data.result.fluid.CauldronFluidResult;
import io.github.pouffy.cauldrontweaks.common.data.result.fluid.CauldronFluidResultType;
import io.github.pouffy.cauldrontweaks.init.CauldronFluidResults;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public record FillFluidResult(FluidStack fluid) implements CauldronFluidResult {

    public static final MapCodec<FillFluidResult> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CAULDRON_FLUID_CODEC.fieldOf("fluid").forGetter(FillFluidResult::fluid)
    ).apply(instance, FillFluidResult::new));

    @Override
    public CauldronFluidResultType<?> getType() {
        return CauldronFluidResults.FILL.get();
    }

    @Override
    public void alterTank(CauldronBlockEntity cauldron, ItemStack usedItem) {
        cauldron.getTank().fill(getFluidResult(usedItem, cauldron.getFluidStack()), IFluidHandler.FluidAction.EXECUTE);
        CauldronTweaks.LOGGER.info("Filled {} to cauldron. Has {} contained", fluid.toString(), cauldron.getTank().getFluidAmount());
    }

    @Override
    public FluidStack getFluidResult(ItemStack usedItem, FluidStack usedFluid) {
        return this.fluid;
    }
}
