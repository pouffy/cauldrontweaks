package io.github.pouffy.cauldrontweaks.common.data.condition.type;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import io.github.pouffy.cauldrontweaks.common.data.condition.CauldronCondition;
import io.github.pouffy.cauldrontweaks.common.data.condition.CauldronConditionType;
import io.github.pouffy.cauldrontweaks.init.CauldronConditions;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record FluidComponentsCondition(DataComponentPredicate components, List<Holder<DataComponentType<?>>> expectedTypes) implements CauldronCondition {

    public static final MapCodec<FluidComponentsCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DataComponentPredicate.CODEC.fieldOf("components").forGetter(FluidComponentsCondition::components),
            BuiltInRegistries.DATA_COMPONENT_TYPE.holderByNameCodec().listOf().optionalFieldOf("types", List.of()).forGetter(FluidComponentsCondition::expectedTypes)
    ).apply(instance, FluidComponentsCondition::new));

    @Override
    public CauldronConditionType<?> getType() {
        return CauldronConditions.FLUID_COMPONENTS.get();
    }

    @Override
    public boolean isValid(CauldronBlockEntity cauldron, Player player, InteractionHand hand, ItemStack stack) {
        boolean typeCheck = true;
        for (var type : this.expectedTypes) {
            if (!cauldron.getFluidStack().has(type.value())) typeCheck = false;
        }
        return this.components.test(cauldron.getFluidStack().getComponents()) && typeCheck;
    }
}
