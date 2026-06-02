package io.github.pouffy.cauldrontweaks.common.data.result.fluid;

import com.mojang.serialization.MapCodec;

public record CauldronFluidResultType<T extends CauldronFluidResult>(MapCodec<T> codec) {
}
