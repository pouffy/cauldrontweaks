package io.github.pouffy.cauldrontweaks.common.data.provider;

import com.google.common.collect.Sets;
import io.github.pouffy.cauldrontweaks.common.data.interaction.CIOutput;
import io.github.pouffy.cauldrontweaks.common.data.interaction.ICauldronInteraction;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractCauldronInteractionProvider implements DataProvider {
    protected final PackOutput.PathProvider pathProvider;
    private final CompletableFuture<HolderLookup.Provider> registries;
    private final String modid;

    public AbstractCauldronInteractionProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries, String modid) {
        this.pathProvider = packOutput.createPathProvider(PackOutput.Target.DATA_PACK, "cauldron_interactions");
        this.registries = registries;
        this.modid = modid;
    }

    protected abstract void addInteractions(CIOutput output, HolderLookup.Provider holderLookup);

    public final CompletableFuture<?> run(CachedOutput output) {
        return this.registries.thenCompose((provider) -> this.run(output, provider));
    }

    public CompletableFuture<?> run(final CachedOutput output, final HolderLookup.Provider registries) {
        Set<CompletableFuture<?>> list = new HashSet<>();
        final Set<ResourceLocation> set = Sets.newHashSet();
        this.addInteractions(new CIOutput() {
            public void accept(ResourceLocation location, ICauldronInteraction interaction) {
                if (!set.add(location)) {
                    throw new IllegalStateException("Duplicate cauldron interaction " + location);
                } else {
                    list.add(DataProvider.saveStable(output, registries, ICauldronInteraction.CODEC, interaction, pathProvider.json(location)));
                }
            }
        }, registries);

        return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
    }

    public void save(CIOutput output, ICauldronInteraction interaction, ResourceLocation recipeId) {
        output.accept(recipeId, interaction);
    }

    @Override
    public String getName() {
        return "Cauldron Interactions for %s".formatted(this.modid);
    }
}
