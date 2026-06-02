package io.github.pouffy.cauldrontweaks.common.data.result.item;

import com.mojang.serialization.MapCodec;

public record CauldronItemResultType<T extends CauldronItemResult>(MapCodec<T> codec) {
}
