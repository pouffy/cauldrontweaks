package io.github.pouffy.cauldrontweaks.common.data.result.fluid.type;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.pouffy.cauldrontweaks.CauldronTweaks;
import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import io.github.pouffy.cauldrontweaks.common.data.result.fluid.CauldronFluidResult;
import io.github.pouffy.cauldrontweaks.common.data.result.fluid.CauldronFluidResultType;
import io.github.pouffy.cauldrontweaks.init.CauldronFluidResults;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.Optional;

public record DrainFluidResult(int amount, Optional<CauldronFluidResult> other) implements CauldronFluidResult {

    public static final MapCodec<DrainFluidResult> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ExtraCodecs.POSITIVE_INT.fieldOf("amount").forGetter(DrainFluidResult::amount),
            CauldronFluidResult.CODEC.optionalFieldOf("other").forGetter(DrainFluidResult::other)
    ).apply(instance, DrainFluidResult::new));

    @Override
    public CauldronFluidResultType<?> getType() {
        return CauldronFluidResults.DRAIN.get();
    }

    @Override
    public void alterTank(CauldronBlockEntity cauldron) {
        cauldron.getTank().drain(this.amount, IFluidHandler.FluidAction.EXECUTE);
        CauldronTweaks.LOGGER.info("Drained {} from cauldron", this.amount);
        other.ifPresent((r) -> r.alterTank(cauldron));
    }

    @Override
    public FluidStack getFluidResult(ItemStack usedItem, FluidStack usedFluid) {
        return usedFluid;
    }
}
