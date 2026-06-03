package io.github.pouffy.cauldrontweaks.common.data.misc.int_test.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.pouffy.cauldrontweaks.common.data.misc.int_test.IntTest;
import io.github.pouffy.cauldrontweaks.common.data.misc.int_test.IntTestType;
import io.github.pouffy.cauldrontweaks.init.CauldronIntTests;

public record AboveTest(int bound, boolean allowEqual) implements IntTest {

    public static final MapCodec<AboveTest> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("bound").forGetter(AboveTest::bound),
            Codec.BOOL.optionalFieldOf("allow_equal", true).forGetter(AboveTest::allowEqual)
    ).apply(instance, AboveTest::new));

    @Override
    public IntTestType<?> getType() {
        return CauldronIntTests.ABOVE.get();
    }

    @Override
    public boolean test(int incoming) {
        if (allowEqual) return incoming >= this.bound;
        return incoming > this.bound;
    }
}
