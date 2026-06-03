package io.github.pouffy.cauldrontweaks.common.data.condition.type;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import io.github.pouffy.cauldrontweaks.common.data.condition.CauldronCondition;
import io.github.pouffy.cauldrontweaks.common.data.condition.CauldronConditionType;
import io.github.pouffy.cauldrontweaks.init.CauldronConditions;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public record RejectFluidComponentsCondition(List<Holder<DataComponentType<?>>> components) implements CauldronCondition {

    public static final MapCodec<RejectFluidComponentsCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BuiltInRegistries.DATA_COMPONENT_TYPE.holderByNameCodec().listOf().fieldOf("components").forGetter(RejectFluidComponentsCondition::components)
    ).apply(instance, RejectFluidComponentsCondition::new));

    @Override
    public CauldronConditionType<?> getType() {
        return CauldronConditions.REJECT_FLUID_COMPONENTS.get();
    }

    @Override
    public boolean isValid(CauldronBlockEntity cauldron, Player player, InteractionHand hand, ItemStack stack) {
        FluidStack fluidStack = cauldron.getFluidStack();
        return this.components.stream().map(Holder::value).noneMatch(fluidStack::has);
    }
}
