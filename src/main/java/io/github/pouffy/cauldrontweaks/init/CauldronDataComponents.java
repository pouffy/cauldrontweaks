package io.github.pouffy.cauldrontweaks.init;

import io.github.pouffy.cauldrontweaks.CauldronTweaks;
import io.github.pouffy.cauldrontweaks.common.fluid.PotionFluid;
import io.github.pouffy.cauldrontweaks.helpers.ModUtils;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CauldronDataComponents {
    public static final DeferredRegister<DataComponentType<?>> HELPER = ModUtils.createRegister(Registries.DATA_COMPONENT_TYPE);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<PotionFluid.BottleType>> POTION_FLUID_BOTTLE_TYPE = HELPER.register("bottle_type", () -> DataComponentType.<PotionFluid.BottleType>builder().persistent(PotionFluid.BottleType.CODEC).networkSynchronized(PotionFluid.BottleType.STREAM_CODEC).build());

    public static void staticInit() {
        CauldronTweaks.LOGGER.info("[Cauldron Tweaks] Data Component Registry");
    }
}
