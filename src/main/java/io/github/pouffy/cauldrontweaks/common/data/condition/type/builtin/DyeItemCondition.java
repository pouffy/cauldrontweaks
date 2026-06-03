package io.github.pouffy.cauldrontweaks.common.data.condition.type.builtin;

import com.mojang.serialization.MapCodec;
import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import io.github.pouffy.cauldrontweaks.common.data.condition.CauldronCondition;
import io.github.pouffy.cauldrontweaks.common.data.condition.CauldronConditionType;
import io.github.pouffy.cauldrontweaks.init.CauldronConditions;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;

public enum DyeItemCondition implements CauldronCondition {
    INSTANCE;

    public static final MapCodec<DyeItemCondition> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public CauldronConditionType<?> getType() {
        return CauldronConditions.DYE_ITEM.get();
    }

    @Override
    public boolean isValid(CauldronBlockEntity cauldron, Player player, InteractionHand hand, ItemStack stack) {
        return stack.getItem() instanceof DyeItem;
    }
}
