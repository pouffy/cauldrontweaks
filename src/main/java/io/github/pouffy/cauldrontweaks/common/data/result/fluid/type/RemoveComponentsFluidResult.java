package io.github.pouffy.cauldrontweaks.common.data.result.fluid.type;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import io.github.pouffy.cauldrontweaks.common.data.result.fluid.CauldronFluidResult;
import io.github.pouffy.cauldrontweaks.common.data.result.fluid.CauldronFluidResultType;
import io.github.pouffy.cauldrontweaks.init.CauldronFluidResults;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public record RemoveComponentsFluidResult(List<Holder<DataComponentType<?>>> components) implements CauldronFluidResult {

    public static final MapCodec<RemoveComponentsFluidResult> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BuiltInRegistries.DATA_COMPONENT_TYPE.holderByNameCodec().listOf().fieldOf("components").forGetter(RemoveComponentsFluidResult::components)
    ).apply(instance, RemoveComponentsFluidResult::new));

    @Override
    public CauldronFluidResultType<?> getType() {
        return CauldronFluidResults.REMOVE_COMPONENTS.get();
    }

    @Override
    public void alterTank(CauldronBlockEntity cauldron) {
        FluidStack copy = cauldron.getFluidStack().copy();
        this.components.stream().map(Holder::value).forEach(copy::remove);
        cauldron.getTank().setFluid(copy);
    }

    @Override
    public FluidStack getFluidResult(ItemStack usedItem, FluidStack usedFluid) {
        FluidStack copy = usedFluid.copy();
        this.components.stream().map(Holder::value).forEach(copy::remove);
        return copy;
    }

}
