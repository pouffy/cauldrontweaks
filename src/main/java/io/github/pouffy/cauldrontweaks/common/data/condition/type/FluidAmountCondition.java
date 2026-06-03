package io.github.pouffy.cauldrontweaks.common.data.condition.type;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import io.github.pouffy.cauldrontweaks.common.data.condition.CauldronCondition;
import io.github.pouffy.cauldrontweaks.common.data.condition.CauldronConditionType;
import io.github.pouffy.cauldrontweaks.common.data.misc.int_test.IntTest;
import io.github.pouffy.cauldrontweaks.init.CauldronConditions;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public record FluidAmountCondition(IntTest amount) implements CauldronCondition {

    public static final MapCodec<FluidAmountCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            IntTest.CODEC.fieldOf("amount").forGetter(FluidAmountCondition::amount)
    ).apply(instance, FluidAmountCondition::new));

    @Override
    public CauldronConditionType<?> getType() {
        return CauldronConditions.FLUID_AMOUNT.get();
    }

    @Override
    public boolean isValid(CauldronBlockEntity cauldron, Player player, InteractionHand hand, ItemStack stack) {
        int contained = cauldron.getTank().getFluidAmount();
        return this.amount.test(contained);
    }
}
