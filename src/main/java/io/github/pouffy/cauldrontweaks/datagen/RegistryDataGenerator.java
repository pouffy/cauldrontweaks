package io.github.pouffy.cauldrontweaks.datagen;

import io.github.pouffy.cauldrontweaks.CauldronTweaks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.data.registries.RegistryPatchGenerator;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class RegistryDataGenerator extends DatapackBuiltinEntriesProvider {

    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder();

    public RegistryDataGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, RegistryPatchGenerator.createLookup(registries, BUILDER), Set.of("minecraft", CauldronTweaks.MODID));
    }
}
