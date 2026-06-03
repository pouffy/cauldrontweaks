package io.github.pouffy.cauldrontweaks.common.data.misc.int_test.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.pouffy.cauldrontweaks.common.data.misc.int_test.IntTest;
import io.github.pouffy.cauldrontweaks.common.data.misc.int_test.IntTestType;
import io.github.pouffy.cauldrontweaks.init.CauldronIntTests;

public record BetweenTest(int min, int max, boolean allowEqual) implements IntTest {

    public static final MapCodec<BetweenTest> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("min").forGetter(BetweenTest::min),
            Codec.INT.fieldOf("max").forGetter(BetweenTest::max),
            Codec.BOOL.optionalFieldOf("allow_equal", true).forGetter(BetweenTest::allowEqual)
    ).apply(instance, BetweenTest::new));

    @Override
    public IntTestType<?> getType() {
        return CauldronIntTests.BETWEEN.get();
    }

    @Override
    public boolean test(int incoming) {
        if(this.allowEqual) return incoming >= this.min && incoming <= this.max;
        return incoming > this.min && incoming < this.max;
    }
}
