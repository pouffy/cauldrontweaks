package io.github.pouffy.cauldrontweaks;

import com.mojang.logging.LogUtils;
import io.github.pouffy.cauldrontweaks.client.CauldronRenderer;
import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import io.github.pouffy.cauldrontweaks.common.data.interaction.CauldronInteractionType;
import io.github.pouffy.cauldrontweaks.common.data.interaction.types.*;
import io.github.pouffy.cauldrontweaks.common.data.interaction.types.builtin.BucketEmptyInteraction;
import io.github.pouffy.cauldrontweaks.common.data.interaction.types.builtin.BucketFillInteraction;
import io.github.pouffy.cauldrontweaks.common.data.interaction.types.builtin.DrinkPotionInteraction;
import io.github.pouffy.cauldrontweaks.common.data.interaction.types.builtin.DyeFluidInteraction;
import io.github.pouffy.cauldrontweaks.common.event.CauldronTickEvent;
import io.github.pouffy.cauldrontweaks.common.events.InteractionEvents;
import io.github.pouffy.cauldrontweaks.common.fluid.PotionFluid;
import io.github.pouffy.cauldrontweaks.common.fluid.VirtualFluid;
import io.github.pouffy.cauldrontweaks.datagen.CauldronDataGenerator;
import io.github.pouffy.cauldrontweaks.helpers.PotionFluidHelper;
import io.github.pouffy.cauldrontweaks.helpers.RegistryAccessJsonReloadListener;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.*;
import org.slf4j.Logger;

@Mod(CauldronTweaks.MODID)
public class CauldronTweaks {
    public static final String MODID = "cauldrontweaks";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final TagKey<Fluid> CAULDRON_BLACKLIST = TagKey.create(Registries.FLUID, getResource("cauldron_blacklist"));
    public static final TagKey<Fluid> DYEABLE_FLUID = TagKey.create(Registries.FLUID, getResource("dyeable_fluid"));

    public static final DeferredHolder<FluidType, FluidType> POWDER_SNOW_TYPE = DeferredHolder.create(NeoForgeRegistries.Keys.FLUID_TYPES, getResource("powder_snow"));
    public static final DeferredHolder<Fluid, VirtualFluid> POWDER_SNOW = DeferredHolder.create(Registries.FLUID, getResource("powder_snow"));
    public static final DeferredHolder<Fluid, VirtualFluid> FLOWING_POWDER_SNOW = DeferredHolder.create(Registries.FLUID, getResource("flowing_powder_snow"));
    public static final DeferredHolder<FluidType, PotionFluid.PotionFluidType> POTION_TYPE = DeferredHolder.create(NeoForgeRegistries.Keys.FLUID_TYPES, getResource("potion"));
    public static final DeferredHolder<Fluid, PotionFluid> POTION = DeferredHolder.create(Registries.FLUID, getResource("potion"));
    public static final DeferredHolder<Fluid, PotionFluid> FLOWING_POTION = DeferredHolder.create(Registries.FLUID, getResource("flowing_potion"));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CauldronBlockEntity>> CAULDRON = DeferredHolder.create(Registries.BLOCK_ENTITY_TYPE, getResource("cauldron"));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<PotionFluid.BottleType>> POTION_FLUID_BOTTLE_TYPE = DeferredHolder.create(Registries.DATA_COMPONENT_TYPE, getResource("bottle_type"));


    public static final ResourceKey<Registry<CauldronInteractionType<?>>> CAULDRON_INTERACTION_TYPE = createRegistryKey("cauldron_interaction_types");
    public static final Registry<CauldronInteractionType<?>> CAULDRON_INTERACTION_TYPE_REGISTRY = makeSyncedRegistry(CAULDRON_INTERACTION_TYPE);

    public static final DeferredHolder<CauldronInteractionType<?>, CauldronInteractionType<FillContainerInteraction>> FILL_CONTAINER = DeferredHolder.create(CAULDRON_INTERACTION_TYPE, getResource("fill_container"));
    public static final DeferredHolder<CauldronInteractionType<?>, CauldronInteractionType<EmptyContainerInteraction>> EMPTY_CONTAINER = DeferredHolder.create(CAULDRON_INTERACTION_TYPE, getResource("empty_container"));
    public static final DeferredHolder<CauldronInteractionType<?>, CauldronInteractionType<BucketEmptyInteraction>> EMPTY_BUCKET = DeferredHolder.create(CAULDRON_INTERACTION_TYPE, getResource("empty_bucket"));
    public static final DeferredHolder<CauldronInteractionType<?>, CauldronInteractionType<BucketFillInteraction>> FILL_BUCKET = DeferredHolder.create(CAULDRON_INTERACTION_TYPE, getResource("fill_bucket"));
    public static final DeferredHolder<CauldronInteractionType<?>, CauldronInteractionType<DyeFluidInteraction>> DYE_FLUID = DeferredHolder.create(CAULDRON_INTERACTION_TYPE, getResource("dye_fluid"));
    public static final DeferredHolder<CauldronInteractionType<?>, CauldronInteractionType<DyeItemInteraction>> DYE_ITEM = DeferredHolder.create(CAULDRON_INTERACTION_TYPE, getResource("dye_item"));
    public static final DeferredHolder<CauldronInteractionType<?>, CauldronInteractionType<DrinkContentsInteraction>> DRINK_CONTENTS = DeferredHolder.create(CAULDRON_INTERACTION_TYPE, getResource("drink_contents"));
    public static final DeferredHolder<CauldronInteractionType<?>, CauldronInteractionType<DrinkPotionInteraction>> DRINK_POTION = DeferredHolder.create(CAULDRON_INTERACTION_TYPE, getResource("drink_potion"));

    public CauldronTweaks(IEventBus modEventBus, ModContainer modContainer) {
        NeoForgeMod.enableMilkFluid();
        NeoForge.EVENT_BUS.register(new InteractionEvents());
        modEventBus.addListener(CauldronDataGenerator::gatherData);

        NeoForge.EVENT_BUS.addListener(TagsUpdatedEvent.class, (event) -> afterDataReloadOrDataSync(event.getRegistryAccess()));
    }

    private static void afterDataReloadOrDataSync(RegistryAccess registryAccess) {
        RegistryAccessJsonReloadListener.runReloads(registryAccess);
    }

    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        static void blockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(CAULDRON.get(), CauldronRenderer::new);
        }

        @SubscribeEvent
        static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
            event.registerFluidType(new IClientFluidTypeExtensions() {
                private static final ResourceLocation POWDER_SNOW = ResourceLocation.withDefaultNamespace("block/powder_snow");

                public ResourceLocation getStillTexture() {
                    return POWDER_SNOW;
                }

                public ResourceLocation getFlowingTexture() {
                    return POWDER_SNOW;
                }
            }, POWDER_SNOW_TYPE.value());
        }

        @SubscribeEvent
        static void cauldronTick(CauldronTickEvent.Client event) {
            CauldronBlockEntity cauldron = event.getCauldron();
            FluidStack fluidStack = cauldron.getFluidStack();
            if (cauldron.getLevel().getGameTime() % 20L == 0L && !fluidStack.isEmpty()) {
                if (fluidStack.has(DataComponents.POTION_CONTENTS)) {
                    PotionContents contents = fluidStack.get(DataComponents.POTION_CONTENTS);
                    PotionFluidHelper.generatePotionParticles(cauldron.getLevel(), cauldron.getBlockPos(), contents.getColor(), false);
                }
                if (fluidStack.has(DataComponents.DYED_COLOR)) {
                    DyedItemColor color = fluidStack.get(DataComponents.DYED_COLOR);
                }
            }
        }
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

    public static String makeDescriptionId(String type, String name) {
        return type + "." + MODID + "." + name;
    }

    private static <T> ResourceKey<Registry<T>> createRegistryKey(String name) {
        return ResourceKey.createRegistryKey(getResource(name));
    }

    private static <T> Registry<T> makeSyncedRegistry(ResourceKey<Registry<T>> registryKey) {
        return new RegistryBuilder<>(registryKey).sync(true).create();
    }
}
