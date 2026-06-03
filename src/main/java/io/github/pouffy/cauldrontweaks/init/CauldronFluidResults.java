package io.github.pouffy.cauldrontweaks.init;

import com.mojang.serialization.MapCodec;
import io.github.pouffy.cauldrontweaks.CauldronTweaks;
import io.github.pouffy.cauldrontweaks.common.data.result.fluid.CauldronFluidResult;
import io.github.pouffy.cauldrontweaks.common.data.result.fluid.CauldronFluidResultType;
import io.github.pouffy.cauldrontweaks.common.data.result.fluid.type.*;
import io.github.pouffy.cauldrontweaks.helpers.ModUtils;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CauldronFluidResults {
    public static final DeferredRegister<CauldronFluidResultType<?>> HELPER = ModUtils.createRegister(CauldronRegistries.CAULDRON_FLUID_RESULT_TYPE);

    public static final DeferredHolder<CauldronFluidResultType<?>, CauldronFluidResultType<NoOpFluidResult>> NO_OP = create("no_op", NoOpFluidResult.CODEC);
    public static final DeferredHolder<CauldronFluidResultType<?>, CauldronFluidResultType<DrainFluidResult>> DRAIN = create("drain", DrainFluidResult.CODEC);
    public static final DeferredHolder<CauldronFluidResultType<?>, CauldronFluidResultType<FillFluidResult>> FILL = create("fill", FillFluidResult.CODEC);
    public static final DeferredHolder<CauldronFluidResultType<?>, CauldronFluidResultType<AddComponentsFluidResult>> ADD_COMPONENTS = create("add_components", AddComponentsFluidResult.CODEC);
    public static final DeferredHolder<CauldronFluidResultType<?>, CauldronFluidResultType<RemoveComponentsFluidResult>> REMOVE_COMPONENTS = create("remove_components", RemoveComponentsFluidResult.CODEC);

    public static <T extends CauldronFluidResult> DeferredHolder<CauldronFluidResultType<?>, CauldronFluidResultType<T>> create(String name, MapCodec<T> codec) {
        return HELPER.register(name, () -> new CauldronFluidResultType<>(codec));
    }

    public static void staticInit() {
        CauldronTweaks.LOGGER.info("[Cauldron Tweaks] Cauldron Fluid Result Type Registry");
    }
}
