package io.github.pouffy.cauldrontweaks.init;

import com.mojang.serialization.MapCodec;
import io.github.pouffy.cauldrontweaks.CauldronTweaks;
import io.github.pouffy.cauldrontweaks.common.data.misc.int_test.IntTest;
import io.github.pouffy.cauldrontweaks.common.data.misc.int_test.IntTestType;
import io.github.pouffy.cauldrontweaks.common.data.misc.int_test.type.AboveTest;
import io.github.pouffy.cauldrontweaks.common.data.misc.int_test.type.BelowTest;
import io.github.pouffy.cauldrontweaks.common.data.misc.int_test.type.BetweenTest;
import io.github.pouffy.cauldrontweaks.common.data.misc.int_test.type.ExactTest;
import io.github.pouffy.cauldrontweaks.helpers.ModUtils;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CauldronIntTests {
    public static final DeferredRegister<IntTestType<?>> HELPER = ModUtils.createRegister(CauldronRegistries.INT_TEST_TYPE);

    public static final DeferredHolder<IntTestType<?>, IntTestType<ExactTest>> EXACT = create("exact", ExactTest.CODEC);
    public static final DeferredHolder<IntTestType<?>, IntTestType<BetweenTest>> BETWEEN = create("between", BetweenTest.CODEC);
    public static final DeferredHolder<IntTestType<?>, IntTestType<AboveTest>> ABOVE = create("above", AboveTest.CODEC);
    public static final DeferredHolder<IntTestType<?>, IntTestType<BelowTest>> BELOW = create("below", BelowTest.CODEC);

    public static <T extends IntTest> DeferredHolder<IntTestType<?>, IntTestType<T>> create(String name, MapCodec<T> codec) {
        return HELPER.register(name, () -> new IntTestType<>(codec));
    }

    public static void staticInit() {
        CauldronTweaks.LOGGER.info("[Cauldron Tweaks] Integer Test Type Registry");
    }
}
