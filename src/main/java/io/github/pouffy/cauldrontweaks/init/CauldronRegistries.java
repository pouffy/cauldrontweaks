package io.github.pouffy.cauldrontweaks.init;

import com.mojang.serialization.Lifecycle;
import io.github.pouffy.cauldrontweaks.CauldronTweaks;
import io.github.pouffy.cauldrontweaks.common.data.condition.CauldronConditionType;
import io.github.pouffy.cauldrontweaks.common.data.interaction.CauldronInteractionType;
import io.github.pouffy.cauldrontweaks.common.data.misc.int_test.IntTestType;
import io.github.pouffy.cauldrontweaks.common.data.result.fluid.CauldronFluidResultType;
import io.github.pouffy.cauldrontweaks.common.data.result.item.CauldronItemResultType;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

@EventBusSubscriber
public class CauldronRegistries {

    public static final ResourceKey<Registry<CauldronInteractionType<?>>> CAULDRON_INTERACTION_TYPE = createRegistryKey("cauldron_interaction");
    public static final Registry<CauldronInteractionType<?>> CAULDRON_INTERACTION_TYPE_REGISTRY = makeSyncedRegistry(CAULDRON_INTERACTION_TYPE);
    public static final ResourceKey<Registry<CauldronFluidResultType<?>>> CAULDRON_FLUID_RESULT_TYPE = createRegistryKey("cauldron_fluid_result");
    public static final Registry<CauldronFluidResultType<?>> CAULDRON_FLUID_RESULT_TYPE_REGISTRY = makeSyncedRegistry(CAULDRON_FLUID_RESULT_TYPE);
    public static final ResourceKey<Registry<CauldronItemResultType<?>>> CAULDRON_ITEM_RESULT_TYPE = createRegistryKey("cauldron_item_result");
    public static final Registry<CauldronItemResultType<?>> CAULDRON_ITEM_RESULT_TYPE_REGISTRY = makeSyncedRegistry(CAULDRON_ITEM_RESULT_TYPE);
    public static final ResourceKey<Registry<CauldronConditionType<?>>> CAULDRON_CONDITION_TYPE = createRegistryKey("cauldron_condition");
    public static final Registry<CauldronConditionType<?>> CAULDRON_CONDITION_TYPE_REGISTRY = makeSyncedRegistry(CAULDRON_CONDITION_TYPE);
    public static final ResourceKey<Registry<IntTestType<?>>> INT_TEST_TYPE = createRegistryKey("int_test");
    public static final Registry<IntTestType<?>> INT_TEST_TYPE_REGISTRY = makeSyncedRegistry(INT_TEST_TYPE);

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
        event.register(CAULDRON_FLUID_RESULT_TYPE_REGISTRY);
        event.register(CAULDRON_ITEM_RESULT_TYPE_REGISTRY);
        event.register(CAULDRON_CONDITION_TYPE_REGISTRY);
        event.register(INT_TEST_TYPE_REGISTRY);
    }
}
