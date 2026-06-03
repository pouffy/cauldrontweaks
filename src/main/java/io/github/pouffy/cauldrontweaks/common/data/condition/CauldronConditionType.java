package io.github.pouffy.cauldrontweaks.common.data.condition;

import com.mojang.serialization.MapCodec;

public record CauldronConditionType<T extends CauldronCondition>(MapCodec<T> codec) {
}
