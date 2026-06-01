package io.github.pouffy.cauldrontweaks.common;

import io.github.pouffy.cauldrontweaks.CauldronTweaks;
import io.github.pouffy.cauldrontweaks.common.data.interaction.types.*;
import io.github.pouffy.cauldrontweaks.common.fluid.PotionFluid;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber
public class CommonEvents {
    @SubscribeEvent
    public static void registerFluids(RegisterEvent event) {

    }
}
