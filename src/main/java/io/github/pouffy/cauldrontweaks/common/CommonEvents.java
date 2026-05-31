package io.github.pouffy.cauldrontweaks.common;

import io.github.pouffy.cauldrontweaks.CauldronTweaks;
import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import io.github.pouffy.cauldrontweaks.common.data.interaction.CauldronInteractionType;
import io.github.pouffy.cauldrontweaks.common.data.interaction.types.*;
import io.github.pouffy.cauldrontweaks.common.data.interaction.types.builtin.BucketEmptyInteraction;
import io.github.pouffy.cauldrontweaks.common.data.interaction.types.builtin.BucketFillInteraction;
import io.github.pouffy.cauldrontweaks.common.data.interaction.types.builtin.DrinkPotionInteraction;
import io.github.pouffy.cauldrontweaks.common.data.interaction.types.builtin.DyeFluidInteraction;
import io.github.pouffy.cauldrontweaks.common.fluid.PotionFluid;
import io.github.pouffy.cauldrontweaks.common.fluid.VirtualFluid;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@EventBusSubscriber
public class CommonEvents {
    @SubscribeEvent
    public static void registerFluids(RegisterEvent event) {
        event.register(NeoForgeRegistries.Keys.FLUID_TYPES, (helper) -> {
            helper.register(CauldronTweaks.POWDER_SNOW_TYPE.unwrapKey().orElseThrow(), new FluidType(FluidType.Properties.create().sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_POWDER_SNOW).sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_POWDER_SNOW)));
            helper.register(CauldronTweaks.POTION_TYPE.unwrapKey().orElseThrow(), new PotionFluid.PotionFluidType(FluidType.Properties.create().sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL).sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)));
        });
        event.register(Registries.FLUID, (helper) -> {
            BaseFlowingFluid.Properties powderSnow = new BaseFlowingFluid.Properties(CauldronTweaks.POWDER_SNOW_TYPE, CauldronTweaks.POWDER_SNOW, CauldronTweaks.FLOWING_POWDER_SNOW);
            helper.register(CauldronTweaks.POWDER_SNOW.unwrapKey().orElseThrow(), VirtualFluid.createSource(powderSnow));
            helper.register(CauldronTweaks.FLOWING_POWDER_SNOW.unwrapKey().orElseThrow(), VirtualFluid.createFlowing(powderSnow));
            BaseFlowingFluid.Properties potion = new BaseFlowingFluid.Properties(CauldronTweaks.POTION_TYPE, CauldronTweaks.POTION, CauldronTweaks.FLOWING_POTION);
            helper.register(CauldronTweaks.POTION.unwrapKey().orElseThrow(), PotionFluid.createSource(potion));
            helper.register(CauldronTweaks.FLOWING_POTION.unwrapKey().orElseThrow(), PotionFluid.createFlowing(potion));
        });
        event.register(Registries.DATA_COMPONENT_TYPE, (helper) -> {
            helper.register(CauldronTweaks.POTION_FLUID_BOTTLE_TYPE.unwrapKey().orElseThrow(), DataComponentType.<PotionFluid.BottleType>builder().persistent(PotionFluid.BottleType.CODEC).networkSynchronized(PotionFluid.BottleType.STREAM_CODEC).build());
        });
    }

    @SubscribeEvent
    public static void registerBlockEntities(RegisterEvent event) {
        event.register(Registries.BLOCK_ENTITY_TYPE, (helper) -> {
            List<Block> validBlocks = new ArrayList<>();
            Consumer<Block> addCauldron = validBlocks::add;
            addCauldron.accept(Blocks.CAULDRON);
            addCauldron.accept(Blocks.WATER_CAULDRON);
            addCauldron.accept(Blocks.LAVA_CAULDRON);
            addCauldron.accept(Blocks.POWDER_SNOW_CAULDRON);
            helper.register(CauldronTweaks.CAULDRON.unwrapKey().orElseThrow(), BlockEntityType.Builder.of(CauldronBlockEntity::new, validBlocks.toArray(new Block[0])).build(null));
        });
    }

    @SubscribeEvent
    public static void registerCauldronStuff(RegisterEvent event) {
        event.register(CauldronTweaks.CAULDRON_INTERACTION_TYPE, (helper) -> {
            helper.register(CauldronTweaks.EMPTY_CONTAINER.unwrapKey().orElseThrow(), new CauldronInteractionType<>(EmptyContainerInteraction.CODEC));
            helper.register(CauldronTweaks.FILL_CONTAINER.unwrapKey().orElseThrow(), new CauldronInteractionType<>(FillContainerInteraction.CODEC));
            helper.register(CauldronTweaks.EMPTY_BUCKET.unwrapKey().orElseThrow(), new CauldronInteractionType<>(BucketEmptyInteraction.CODEC));
            helper.register(CauldronTweaks.FILL_BUCKET.unwrapKey().orElseThrow(), new CauldronInteractionType<>(BucketFillInteraction.CODEC));
            helper.register(CauldronTweaks.DYE_FLUID.unwrapKey().orElseThrow(), new CauldronInteractionType<>(DyeFluidInteraction.CODEC));
            helper.register(CauldronTweaks.DYE_ITEM.unwrapKey().orElseThrow(), new CauldronInteractionType<>(DyeItemInteraction.CODEC));
            helper.register(CauldronTweaks.DRINK_CONTENTS.unwrapKey().orElseThrow(), new CauldronInteractionType<>(DrinkContentsInteraction.CODEC));
            helper.register(CauldronTweaks.DRINK_POTION.unwrapKey().orElseThrow(), new CauldronInteractionType<>(DrinkPotionInteraction.CODEC));
        });
    }

    @SubscribeEvent
    public static void newRegistry(NewRegistryEvent event) {
        event.register(CauldronTweaks.CAULDRON_INTERACTION_TYPE_REGISTRY);
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        CauldronBlockEntity.registerCapabilities(event);
    }
}
