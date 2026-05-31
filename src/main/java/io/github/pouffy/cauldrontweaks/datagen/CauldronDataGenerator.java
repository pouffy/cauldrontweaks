package io.github.pouffy.cauldrontweaks.datagen;

import io.github.pouffy.cauldrontweaks.datagen.server.CauldronInteractionsProvider;
import io.github.pouffy.cauldrontweaks.datagen.server.tags.CauldronFluidTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

public class CauldronDataGenerator {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();
        ExistingFileHelper helper = event.getExistingFileHelper();

        final boolean includeClient = event.includeClient();
        final boolean includeServer = event.includeServer();

        var registryDataDatagen = new RegistryDataGenerator(output, provider);
        var registryProvider = registryDataDatagen.getRegistryProvider();

        generator.addProvider(includeServer, registryDataDatagen);

        var fluidTags = new CauldronFluidTagsProvider(output, registryProvider, helper);

        var cauldronInteractions = new CauldronInteractionsProvider(output, registryProvider);

        generator.addProvider(includeServer, cauldronInteractions);
        generator.addProvider(includeServer, fluidTags);
    }
}
