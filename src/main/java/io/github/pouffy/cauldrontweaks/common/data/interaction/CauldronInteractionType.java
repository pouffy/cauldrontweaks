package io.github.pouffy.cauldrontweaks.common.data.interaction;

import com.mojang.serialization.MapCodec;

public record CauldronInteractionType<T extends ICauldronInteraction>(MapCodec<T> codec) {
}
