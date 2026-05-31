package io.github.pouffy.cauldrontweaks.datagen.server.tags;

import io.github.pouffy.cauldrontweaks.CauldronTweaks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class CauldronFluidTagsProvider extends FluidTagsProvider {

    public CauldronFluidTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, provider, CauldronTweaks.MODID, existingFileHelper);
    }

    protected void addTags(HolderLookup.Provider provider) {
        this.tag(CauldronTweaks.DYEABLE_FLUID).add(Fluids.WATER, Fluids.FLOWING_WATER);
    }
}
