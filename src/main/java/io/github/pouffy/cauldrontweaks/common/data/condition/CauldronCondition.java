package io.github.pouffy.cauldrontweaks.common.data.condition;

import com.mojang.serialization.Codec;
import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import io.github.pouffy.cauldrontweaks.init.CauldronLootParamSets;
import io.github.pouffy.cauldrontweaks.init.CauldronRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.List;
import java.util.Optional;

public interface CauldronCondition {

    Codec<CauldronCondition> CODEC = Codec.lazyInitialized(CauldronRegistries.CAULDRON_CONDITION_TYPE_REGISTRY::byNameCodec).dispatch("type", CauldronCondition::getType, CauldronConditionType::codec);

    CauldronConditionType<?> getType();

    boolean isValid(CauldronBlockEntity cauldron, Player player, InteractionHand hand, ItemStack stack);

    static LootContext getLootContext(CauldronBlockEntity cauldron, Player player, ItemStack stack) {
        if (cauldron.getLevel() instanceof ServerLevel level) {
            LootParams params = new LootParams.Builder(level)
                    .withParameter(LootContextParams.BLOCK_ENTITY, cauldron)
                    .withParameter(LootContextParams.BLOCK_STATE, cauldron.getBlockState())
                    .withParameter(LootContextParams.ORIGIN, cauldron.getBlockPos().getCenter())
                    .withParameter(LootContextParams.TOOL, stack)
                    .withParameter(LootContextParams.THIS_ENTITY, player).create(CauldronLootParamSets.CAULDRON);
            return new LootContext.Builder(params).create(Optional.empty());
        }
        return null;
    }

    static boolean test(List<CauldronCondition> conditions, CauldronBlockEntity cauldron, Player player, InteractionHand hand, ItemStack stack) {
        if (conditions.isEmpty()) return true;
        return conditions.stream().allMatch(c -> c.isValid(cauldron, player, hand, stack));
    }
}
