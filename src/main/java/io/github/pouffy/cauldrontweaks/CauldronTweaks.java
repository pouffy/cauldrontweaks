package io.github.pouffy.cauldrontweaks;

import com.mojang.logging.LogUtils;
import io.github.pouffy.cauldrontweaks.common.events.InteractionEvents;
import io.github.pouffy.cauldrontweaks.datagen.CauldronDataGenerator;
import io.github.pouffy.cauldrontweaks.helpers.RegistryAccessJsonReloadListener;
import io.github.pouffy.cauldrontweaks.init.*;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import org.slf4j.Logger;

@Mod(CauldronTweaks.MODID)
public class CauldronTweaks {
    private static CauldronTweaks INSTANCE;
    public static final String MODID = "cauldrontweaks";
    public static final Logger LOGGER = LogUtils.getLogger();
    private final IEventBus modEventBus;

    public static final TagKey<Fluid> CAULDRON_BLACKLIST = TagKey.create(Registries.FLUID, getResource("cauldron_blacklist"));
    public static final TagKey<Fluid> DYEABLE_FLUID = TagKey.create(Registries.FLUID, getResource("dyeable_fluid"));
    public static final TagKey<Fluid> CLEARS_DYE = TagKey.create(Registries.FLUID, getResource("clears_dye"));

    public CauldronTweaks(IEventBus modEventBus, ModContainer modContainer) {
        this.modEventBus = modEventBus;
        INSTANCE = this;
        NeoForgeMod.enableMilkFluid();

        NeoForge.EVENT_BUS.register(new InteractionEvents());
        NeoForge.EVENT_BUS.addListener(TagsUpdatedEvent.class, (event) -> afterDataReloadOrDataSync(event.getRegistryAccess()));

        CauldronLootParamSets.staticInit();
        CauldronBlockEntities.staticInit();
        CauldronDataComponents.staticInit();
        CauldronFluids.staticInit();

        CauldronIntTests.staticInit();
        CauldronInteractions.staticInit();
        CauldronConditions.staticInit();
        CauldronFluidResults.staticInit();
        CauldronItemResults.staticInit();

        if (FMLEnvironment.dist == Dist.CLIENT) {
            CauldronTweaksClient.init();
        }

        modEventBus.addListener(CauldronDataGenerator::gatherData);
    }

    public static IEventBus getEventBus() {
        return INSTANCE.modEventBus;
    }

    private static void afterDataReloadOrDataSync(RegistryAccess registryAccess) {
        RegistryAccessJsonReloadListener.runReloads(registryAccess);
    }

    public static ResourceLocation getResource(String name) {
        if (name.contains(":")) {
            return ResourceLocation.tryParse(name);
        }
        return ResourceLocation.fromNamespaceAndPath(MODID, name);
    }

    public static ResourceLocation getResource(String namespace, String name) {
        return ResourceLocation.fromNamespaceAndPath(namespace, name);
    }
}
