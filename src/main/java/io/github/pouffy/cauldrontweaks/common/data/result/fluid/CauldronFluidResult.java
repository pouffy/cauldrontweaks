package io.github.pouffy.cauldrontweaks.common.data.result.fluid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import io.github.pouffy.cauldrontweaks.init.CauldronRegistries;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public interface CauldronFluidResult {

    Codec<CauldronFluidResult> CODEC = Codec.lazyInitialized(CauldronRegistries.CAULDRON_FLUID_RESULT_TYPE_REGISTRY::byNameCodec).dispatch("type", CauldronFluidResult::getType, CauldronFluidResultType::codec);

    CauldronFluidResultType<?> getType();

    void alterTank(CauldronBlockEntity cauldron, ItemStack usedItem);

    FluidStack getFluidResult(ItemStack usedItem, FluidStack usedFluid);

    Codec<FluidStack> CAULDRON_FLUID_CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder.create((instance) -> instance.group(
            FluidStack.FLUID_NON_EMPTY_CODEC.fieldOf("id").forGetter(FluidStack::getFluidHolder),
            ExtraCodecs.intRange(1, 1000).fieldOf("amount").forGetter(FluidStack::getAmount),
            DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter((stack) -> stack.getComponents().asPatch())
    ).apply(instance, FluidStack::new)));
}
