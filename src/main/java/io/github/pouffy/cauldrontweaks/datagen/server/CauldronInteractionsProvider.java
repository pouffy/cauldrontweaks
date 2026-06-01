package io.github.pouffy.cauldrontweaks.datagen.server;

import io.github.pouffy.cauldrontweaks.CauldronTweaks;
import io.github.pouffy.cauldrontweaks.common.data.interaction.CIOutput;
import io.github.pouffy.cauldrontweaks.common.data.interaction.types.*;
import io.github.pouffy.cauldrontweaks.common.data.interaction.types.builtin.BucketEmptyInteraction;
import io.github.pouffy.cauldrontweaks.common.data.interaction.types.builtin.BucketFillInteraction;
import io.github.pouffy.cauldrontweaks.common.data.interaction.types.builtin.DrinkPotionInteraction;
import io.github.pouffy.cauldrontweaks.common.data.interaction.types.builtin.DyeFluidInteraction;
import io.github.pouffy.cauldrontweaks.common.data.provider.AbstractCauldronInteractionProvider;
import io.github.pouffy.cauldrontweaks.init.CauldronFluids;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.EffectCures;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class CauldronInteractionsProvider extends AbstractCauldronInteractionProvider {

    public CauldronInteractionsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registries, CauldronTweaks.MODID);
    }

    @Override
    protected void addInteractions(CIOutput output, HolderLookup.Provider holderLookup) {
        builtin(output);
        itemFilling(output);
        itemEmptying(output);
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
        this.save(output, new DyeItemInteraction(Ingredient.of(ItemTags.DYEABLE), 100), CauldronTweaks.getResource("dyeing/dyeable"));
        this.save(output, new DyeClearingInteraction(Ingredient.of(ItemTags.DYEABLE), SizedFluidIngredient.of(CauldronTweaks.CLEARS_DYE, 100), Optional.empty()), CauldronTweaks.getResource("undyeing/dyeable"));
    }

    private void itemFilling(CIOutput output) {
        this.save(output, new FillContainerInteraction(Ingredient.of(Items.BUCKET), Items.MILK_BUCKET.getDefaultInstance(), SizedFluidIngredient.of(new FluidStack(NeoForgeMod.MILK, 1000))), CauldronTweaks.getResource("filling/milk_bucket"));
        this.save(output, new FillContainerInteraction(Ingredient.of(Items.BUCKET), Items.POWDER_SNOW_BUCKET.getDefaultInstance(), SizedFluidIngredient.of(new FluidStack(CauldronFluids.POWDER_SNOW, 1000))), CauldronTweaks.getResource("filling/powder_snow_bucket"));
    }

    private void itemEmptying(CIOutput output) {
        this.save(output, new EmptyContainerInteraction(Ingredient.of(Items.MILK_BUCKET), Items.BUCKET.getDefaultInstance(), new FluidStack(NeoForgeMod.MILK, 1000)), CauldronTweaks.getResource("emptying/milk_bucket"));
        this.save(output, new EmptyContainerInteraction(Ingredient.of(Items.POWDER_SNOW_BUCKET), Items.BUCKET.getDefaultInstance(), new FluidStack(CauldronFluids.POWDER_SNOW, 1000)), CauldronTweaks.getResource("emptying/powder_snow_bucket"));
    }

    private void contentsDrinking(CIOutput output, HolderLookup.Provider holderLookup) {
        HolderGetter<SoundEvent> sounds = holderLookup.asGetterLookup().lookupOrThrow(Registries.SOUND_EVENT);
        Holder.Reference<SoundEvent> milkDrink = sounds.getOrThrow(ResourceKey.create(Registries.SOUND_EVENT, SoundEvents.GENERIC_DRINK.getLocation()));
        this.save(output, new DrinkContentsInteraction(SizedFluidIngredient.of(new FluidStack(NeoForgeMod.MILK, 100)), milkDrink, Optional.empty(), Optional.of(new DrinkContentsInteraction.CureProperties(EffectCures.MILK, 1, 1))), CauldronTweaks.getResource("drink_contents/milk"));

    }
}
