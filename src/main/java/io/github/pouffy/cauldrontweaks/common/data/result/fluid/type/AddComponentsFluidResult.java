package io.github.pouffy.cauldrontweaks.common.data.result.fluid.type;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import io.github.pouffy.cauldrontweaks.common.data.result.fluid.CauldronFluidResult;
import io.github.pouffy.cauldrontweaks.common.data.result.fluid.CauldronFluidResultType;
import io.github.pouffy.cauldrontweaks.init.CauldronFluidResults;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public record AddComponentsFluidResult(DataComponentPatch components) implements CauldronFluidResult {

    public static final MapCodec<AddComponentsFluidResult> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DataComponentPatch.CODEC.fieldOf("components").forGetter(AddComponentsFluidResult::components)
    ).apply(instance, AddComponentsFluidResult::new));

    @Override
    public CauldronFluidResultType<?> getType() {
        return CauldronFluidResults.ADD_COMPONENTS.get();
    }

    @Override
    public void alterTank(CauldronBlockEntity cauldron) {
        FluidStack copy = cauldron.getFluidStack().copy();
        copy.applyComponents(this.components);
        cauldron.getTank().setFluid(copy);
    }

    @Override
    public FluidStack getFluidResult(ItemStack usedItem, FluidStack usedFluid) {
        FluidStack copy = usedFluid.copy();
        copy.applyComponents(this.components);
        return copy;
    }
}
