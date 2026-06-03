package io.github.pouffy.cauldrontweaks.datagen.server;

import io.github.pouffy.cauldrontweaks.CauldronTweaks;
import io.github.pouffy.cauldrontweaks.common.data.condition.CauldronCondition;
import io.github.pouffy.cauldrontweaks.common.data.condition.type.EmptyHandCondition;
import io.github.pouffy.cauldrontweaks.common.data.condition.type.FluidAmountCondition;
import io.github.pouffy.cauldrontweaks.common.data.condition.type.FluidCondition;
import io.github.pouffy.cauldrontweaks.common.data.condition.type.ItemCondition;
import io.github.pouffy.cauldrontweaks.common.data.interaction.CIOutput;
import io.github.pouffy.cauldrontweaks.common.data.interaction.types.*;
import io.github.pouffy.cauldrontweaks.common.data.interaction.types.builtin.BucketEmptyInteraction;
import io.github.pouffy.cauldrontweaks.common.data.interaction.types.builtin.BucketFillInteraction;
import io.github.pouffy.cauldrontweaks.common.data.interaction.types.builtin.DrinkPotionInteraction;
import io.github.pouffy.cauldrontweaks.common.data.interaction.types.builtin.DyeFluidInteraction;
import io.github.pouffy.cauldrontweaks.common.data.misc.int_test.IntTest;
import io.github.pouffy.cauldrontweaks.common.data.provider.AbstractCauldronInteractionProvider;
import io.github.pouffy.cauldrontweaks.common.data.result.fluid.type.DrainFluidResult;
import io.github.pouffy.cauldrontweaks.common.data.result.fluid.type.FillFluidResult;
import io.github.pouffy.cauldrontweaks.common.data.result.item.type.TransmuteItemResult;
import io.github.pouffy.cauldrontweaks.init.CauldronFluids;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.EffectCures;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class CauldronInteractionsProvider extends AbstractCauldronInteractionProvider {

    public CauldronInteractionsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registries, CauldronTweaks.MODID);
    }

    @Override
    protected void addInteractions(CIOutput output, HolderLookup.Provider holderLookup) {
        builtin(output);
        buckets(output, holderLookup);
        itemDyeing(output);
        contentsDrinking(output, holderLookup);
    }

    private void builtin(CIOutput output) {
        this.save(output, new BucketEmptyInteraction(), CauldronTweaks.getResource("empty_bucket"));
        this.save(output, new BucketFillInteraction(), CauldronTweaks.getResource("fill_bucket"));
        this.save(output, new DyeFluidInteraction(), CauldronTweaks.getResource("dye_fluid"));
        this.save(output, new DrinkPotionInteraction(), CauldronTweaks.getResource("drink_potion"));
    }

    private void itemDyeing(CIOutput output) {
        this.save(output, new DyeItemInteraction(List.of(new ItemCondition(Ingredient.of(ItemTags.DYEABLE)), new FluidAmountCondition(IntTest.above(100))), new DrainFluidResult(100, Optional.empty()), Optional.empty()), CauldronTweaks.getResource("dyeing/dyeable"));
        this.save(output, new DyeClearingInteraction(List.of(new ItemCondition(Ingredient.of(ItemTags.DYEABLE)), new FluidAmountCondition(IntTest.above(100))), new DrainFluidResult(100, Optional.empty()), Optional.empty()), CauldronTweaks.getResource("undyeing/dyeable"));
    }

    private void buckets(CIOutput output, HolderLookup.Provider holderLookup) {
        this.save(output, new GeneralInteraction(getBucketFillConditions(holderLookup, NeoForgeMod.MILK.get(), NeoForgeMod.FLOWING_MILK.get()), new TransmuteItemResult(Items.MILK_BUCKET.getDefaultInstance(), List.of()), new DrainFluidResult(1000, Optional.empty())), CauldronTweaks.getResource("filling/milk_bucket"));
        this.save(output, new GeneralInteraction(getBucketFillConditions(holderLookup, CauldronFluids.POWDER_SNOW.get(), CauldronFluids.FLOWING_POWDER_SNOW.get()), new TransmuteItemResult(Items.POWDER_SNOW_BUCKET.getDefaultInstance(), List.of()), new DrainFluidResult(1000, Optional.empty())), CauldronTweaks.getResource("filling/powder_snow_bucket"));
        this.save(output, new GeneralInteraction(List.of(new ItemCondition(Ingredient.of(Items.MILK_BUCKET)), new FluidAmountCondition(IntTest.exact(0))), new TransmuteItemResult(Items.BUCKET.getDefaultInstance(), List.of()), new FillFluidResult(new FluidStack(NeoForgeMod.MILK, 1000))), CauldronTweaks.getResource("emptying/milk_bucket"));
        this.save(output, new GeneralInteraction(List.of(new ItemCondition(Ingredient.of(Items.POWDER_SNOW_BUCKET)), new FluidAmountCondition(IntTest.exact(0))), new TransmuteItemResult(Items.BUCKET.getDefaultInstance(), List.of()), new FillFluidResult(new FluidStack(CauldronFluids.POWDER_SNOW, 1000))), CauldronTweaks.getResource("emptying/powder_snow_bucket"));
    }

    private void contentsDrinking(CIOutput output, HolderLookup.Provider holderLookup) {
        HolderGetter<SoundEvent> sounds = holderLookup.asGetterLookup().lookupOrThrow(Registries.SOUND_EVENT);
        Holder.Reference<SoundEvent> milkDrink = sounds.getOrThrow(ResourceKey.create(Registries.SOUND_EVENT, SoundEvents.GENERIC_DRINK.getLocation()));
        this.save(output, new DrinkContentsInteraction(List.of(new FluidAmountCondition(IntTest.above(100)), createFluid(holderLookup, NeoForgeMod.MILK.get(), NeoForgeMod.FLOWING_MILK.get()), EmptyHandCondition.INSTANCE), new DrainFluidResult(100, Optional.empty()), milkDrink, Optional.empty(), Optional.of(new DrinkContentsInteraction.CureProperties(EffectCures.MILK, 1, 1))), CauldronTweaks.getResource("drink_contents/milk"));
    }

    private static @NotNull List<CauldronCondition> getBucketFillConditions(HolderLookup.Provider holderLookup, Fluid... fluids) {
        return List.of(new ItemCondition(Ingredient.of(Items.BUCKET)), new FluidAmountCondition(IntTest.exact(1000)), createFluid(holderLookup, fluids));
    }

    private static FluidCondition createFluid(HolderLookup.Provider holderLookup, Fluid... fluids) {
        HolderGetter<Fluid> getter = holderLookup.asGetterLookup().lookupOrThrow(Registries.FLUID);
        List<Holder<Fluid>> holders = new ArrayList<>();
        for (var fluid : fluids) {
            ResourceKey<Fluid> key = ResourceKey.create(Registries.FLUID, BuiltInRegistries.FLUID.getKey(fluid));
            holders.add(getter.getOrThrow(key));
        }
        return new FluidCondition(HolderSet.direct(holders));
    }
}
