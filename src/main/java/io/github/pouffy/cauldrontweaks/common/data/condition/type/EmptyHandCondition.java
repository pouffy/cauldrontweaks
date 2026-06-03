package io.github.pouffy.cauldrontweaks.common.data.condition.type;

import com.mojang.serialization.MapCodec;
import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import io.github.pouffy.cauldrontweaks.common.data.condition.CauldronCondition;
import io.github.pouffy.cauldrontweaks.common.data.condition.CauldronConditionType;
import io.github.pouffy.cauldrontweaks.init.CauldronConditions;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public enum EmptyHandCondition implements CauldronCondition {
    INSTANCE;

    public static final MapCodec<EmptyHandCondition> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public CauldronConditionType<?> getType() {
        return CauldronConditions.EMPTY_HAND.get();
    }

    @Override
    public boolean isValid(CauldronBlockEntity cauldron, Player player, InteractionHand hand, ItemStack stack) {
        boolean handTest;
        if (hand == InteractionHand.OFF_HAND) {
            handTest = player.isShiftKeyDown();
        } else {
            handTest = player.getItemInHand(hand).isEmpty();
        }
        return handTest;
    }
}
