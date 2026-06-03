package io.github.pouffy.cauldrontweaks.init;

import com.mojang.serialization.MapCodec;
import io.github.pouffy.cauldrontweaks.CauldronTweaks;
import io.github.pouffy.cauldrontweaks.common.data.result.item.CauldronItemResult;
import io.github.pouffy.cauldrontweaks.common.data.result.item.CauldronItemResultType;
import io.github.pouffy.cauldrontweaks.common.data.result.item.type.*;
import io.github.pouffy.cauldrontweaks.helpers.ModUtils;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CauldronItemResults {
    public static final DeferredRegister<CauldronItemResultType<?>> HELPER = ModUtils.createRegister(CauldronRegistries.CAULDRON_ITEM_RESULT_TYPE);

    public static final DeferredHolder<CauldronItemResultType<?>, CauldronItemResultType<NoOpItemResult>> NO_OP = create("no_op", NoOpItemResult.CODEC);
    public static final DeferredHolder<CauldronItemResultType<?>, CauldronItemResultType<DestroyItemResult>> DESTROY = create("destroy", DestroyItemResult.CODEC);
    public static final DeferredHolder<CauldronItemResultType<?>, CauldronItemResultType<InheritComponentsItemResult>> INHERIT_COMPONENTS = create("inherit_components", InheritComponentsItemResult.CODEC);
    public static final DeferredHolder<CauldronItemResultType<?>, CauldronItemResultType<RemoveComponentsItemResult>> REMOVE_COMPONENTS = create("remove_components", RemoveComponentsItemResult.CODEC);
    public static final DeferredHolder<CauldronItemResultType<?>, CauldronItemResultType<TransmuteItemResult>> TRANSMUTE = create("transmute", TransmuteItemResult.CODEC);

    public static <T extends CauldronItemResult> DeferredHolder<CauldronItemResultType<?>, CauldronItemResultType<T>> create(String name, MapCodec<T> codec) {
        return HELPER.register(name, () -> new CauldronItemResultType<>(codec));
    }

    public static void staticInit() {
        CauldronTweaks.LOGGER.info("[Cauldron Tweaks] Cauldron Item Result Type Registry");
    }
}
