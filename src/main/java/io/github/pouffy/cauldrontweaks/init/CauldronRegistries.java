package io.github.pouffy.cauldrontweaks.init;

import com.mojang.serialization.Lifecycle;
import io.github.pouffy.cauldrontweaks.CauldronTweaks;
import io.github.pouffy.cauldrontweaks.common.data.interaction.CauldronInteractionType;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

@EventBusSubscriber
public class CauldronRegistries {

    public static final ResourceKey<Registry<CauldronInteractionType<?>>> CAULDRON_INTERACTION_TYPE = createRegistryKey("cauldron_interaction_types");
    public static final Registry<CauldronInteractionType<?>> CAULDRON_INTERACTION_TYPE_REGISTRY = makeSyncedRegistry(CAULDRON_INTERACTION_TYPE);

    private static <T> ResourceKey<Registry<T>> createRegistryKey(String name) {
        return ResourceKey.createRegistryKey(CauldronTweaks.getResource(name));
    }
    /**
     * Creates a {@link Registry} that get synchronised to clients.
     *
     * @param <T> the entry of the registry.
     */
    private static <T> Registry<T> makeSyncedRegistry(ResourceKey<Registry<T>> registryKey) {
        return new RegistryBuilder<>(registryKey).sync(true).create();
    }
    /**
     * Creates a simple {@link Registry} that <B>won't</B> be synced to clients.
     *
     * @param <T> the entry of the registry.
     */
    private static <T> Registry<T> makeRegistry(ResourceKey<Registry<T>> registryKey) {
        return new RegistryBuilder<>(registryKey).create();
    }
    private static <T> Registry<T> registerSimpleWithIntrusiveHolders(ResourceKey<? extends Registry<T>> registryKey) {
        return new MappedRegistry<>(registryKey, Lifecycle.stable(), true);
    }


    @SubscribeEvent
    public static void newRegistry(NewRegistryEvent event) {
        event.register(CAULDRON_INTERACTION_TYPE_REGISTRY);
    }
}
