package io.github.pouffy.cauldrontweaks.common.data.misc.int_test;

import com.mojang.serialization.Codec;
import io.github.pouffy.cauldrontweaks.common.data.misc.int_test.type.AboveTest;
import io.github.pouffy.cauldrontweaks.common.data.misc.int_test.type.BelowTest;
import io.github.pouffy.cauldrontweaks.common.data.misc.int_test.type.BetweenTest;
import io.github.pouffy.cauldrontweaks.common.data.misc.int_test.type.ExactTest;
import io.github.pouffy.cauldrontweaks.init.CauldronRegistries;

public interface IntTest {

    Codec<IntTest> CODEC = Codec.lazyInitialized(CauldronRegistries.INT_TEST_TYPE_REGISTRY::byNameCodec).dispatch("type", IntTest::getType, IntTestType::codec);

    IntTestType<?> getType();

    boolean test(int incoming);

    static ExactTest exact(int value) {
        return new ExactTest(value);
    }
    static BetweenTest between(int min, int max) {
        return new BetweenTest(min, max, true);
    }
    static BetweenTest between(int min, int max, boolean allowEqual) {
        return new BetweenTest(min, max, allowEqual);
    }
    static AboveTest above(int bound) {
        return new AboveTest(bound, true);
    }
    static AboveTest above(int bound, boolean allowEqual) {
        return new AboveTest(bound, allowEqual);
    }
    static BelowTest below(int bound) {
        return new BelowTest(bound, true);
    }
    static BelowTest below(int bound, boolean allowEqual) {
        return new BelowTest(bound, allowEqual);
    }
}
