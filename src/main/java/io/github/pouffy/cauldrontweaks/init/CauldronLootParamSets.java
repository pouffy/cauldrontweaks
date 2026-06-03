package io.github.pouffy.cauldrontweaks.init;

import io.github.pouffy.cauldrontweaks.CauldronTweaks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.function.Consumer;

public class CauldronLootParamSets {

    public static final LootContextParamSet CAULDRON = register(
            "cauldron",
            builder -> builder.required(LootContextParams.BLOCK_ENTITY)
                    .required(LootContextParams.BLOCK_STATE)
                    .required(LootContextParams.ORIGIN)
                    .required(LootContextParams.TOOL)
                    .required(LootContextParams.THIS_ENTITY)
    );

    public static void staticInit() {
        CauldronTweaks.LOGGER.info("[Cauldron Tweaks] Loot Context Param Sets Registry");
    }

    private static LootContextParamSet register(String name, Consumer<LootContextParamSet.Builder> consumer) {
        LootContextParamSet.Builder builder = new LootContextParamSet.Builder();
        consumer.accept(builder);
        LootContextParamSet paramSet = builder.build();
        ResourceLocation resourcelocation = CauldronTweaks.getResource(name);
        LootContextParamSet unregistered = LootContextParamSets.REGISTRY.put(resourcelocation, paramSet);
        if (unregistered != null) {
            throw new IllegalStateException("Loot table parameter set " + resourcelocation + " is already registered");
        } else {
            return paramSet;
        }
    }
}
