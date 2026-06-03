package io.github.pouffy.cauldrontweaks.common.data.condition.type;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import io.github.pouffy.cauldrontweaks.common.data.condition.CauldronCondition;
import io.github.pouffy.cauldrontweaks.common.data.condition.CauldronConditionType;
import io.github.pouffy.cauldrontweaks.init.CauldronConditions;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public record ItemCondition(Ingredient ingredient) implements CauldronCondition {

    public static final MapCodec<ItemCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.fieldOf("ingredient").forGetter(ItemCondition::ingredient)
    ).apply(instance, ItemCondition::new));

    @Override
    public CauldronConditionType<?> getType() {
        return CauldronConditions.ITEM.get();
    }

    @Override
    public boolean isValid(CauldronBlockEntity cauldron, Player player, InteractionHand hand, ItemStack stack) {
        return ingredient.test(stack);
    }
}
