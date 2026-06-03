package io.github.pouffy.cauldrontweaks.common.data.misc.int_test;

import com.mojang.serialization.MapCodec;

public record IntTestType<T extends IntTest>(MapCodec<T> codec) {
}
