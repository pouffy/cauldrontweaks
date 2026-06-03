package io.github.pouffy.cauldrontweaks.common.data.misc.int_test.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.pouffy.cauldrontweaks.common.data.misc.int_test.IntTest;
import io.github.pouffy.cauldrontweaks.common.data.misc.int_test.IntTestType;
import io.github.pouffy.cauldrontweaks.init.CauldronIntTests;

public record ExactTest(int value) implements IntTest {

    public static final MapCodec<ExactTest> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("value").forGetter(ExactTest::value)
    ).apply(instance, ExactTest::new));

    @Override
    public IntTestType<?> getType() {
        return CauldronIntTests.EXACT.get();
    }

    @Override
    public boolean test(int incoming) {
        return incoming == this.value;
    }
}
