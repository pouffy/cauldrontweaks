package io.github.pouffy.cauldrontweaks.common.data.interaction.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import io.github.pouffy.cauldrontweaks.common.data.CauldronFluidIngredient;
import io.github.pouffy.cauldrontweaks.common.data.interaction.CauldronInteractionType;
import io.github.pouffy.cauldrontweaks.common.data.interaction.ICauldronInteraction;
import io.github.pouffy.cauldrontweaks.init.CauldronInteractions;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.EffectCure;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

public record DrinkContentsInteraction(CauldronFluidIngredient fluid, Holder<SoundEvent> eatSound, Optional<FoodProperties> foodProperties, Optional<CureProperties> cureProperties) implements ICauldronInteraction {
    public static final MapCodec<DrinkContentsInteraction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CauldronFluidIngredient.FLAT_CODEC.fieldOf("fluid").forGetter(DrinkContentsInteraction::fluid),
            SoundEvent.CODEC.fieldOf("sound").forGetter(DrinkContentsInteraction::eatSound),
            FoodProperties.DIRECT_CODEC.optionalFieldOf("food").forGetter(DrinkContentsInteraction::foodProperties),
            CureProperties.CODEC.optionalFieldOf("cure").forGetter(DrinkContentsInteraction::cureProperties)
    ).apply(instance, DrinkContentsInteraction::new));

    @Override
    public CauldronInteractionType<?> getType() {
        return CauldronInteractions.DRINK_CONTENTS.get();
    }

    @Override
    public void runExtra(CauldronBlockEntity cauldron, FluidStack fluidStack, Player player, InteractionHand hand, ItemStack stack) {
        foodProperties.ifPresent(props -> {
            player.getFoodData().eat(props);
            addEatEffect(player, props);
        });
        cureProperties.ifPresent(props -> props.affectConsumer(cauldron.getLevel(), player));
        player.playSound(eatSound.value(), 1.0F, 1.0F);
    }

    @Override
    public boolean testExtra(CauldronBlockEntity cauldron, FluidStack fluidStack, Player player, InteractionHand hand, ItemStack stack) {
        boolean handTest;
        if (hand == InteractionHand.OFF_HAND) {
            handTest = player.isShiftKeyDown();
        } else {
            handTest = player.getItemInHand(hand).isEmpty();
        }
        return handTest;
    }

    @Override
    public ItemStack getItemResult(ItemStack usedItem, FluidStack usedFluid, Player player) {
        return usedItem;
    }

    @Override
    public FluidStack getFluidResult(ItemStack usedItem, FluidStack usedFluid) {
        return usedFluid;
    }

    @Override
    public @Nullable Ingredient getItemInput() {
        return null;
    }

    @Override
    public @Nullable CauldronFluidIngredient getFluidInput() {
        return this.fluid();
    }

    @Override
    public int fluidAmountChange() {
        return -this.fluid.amount();
    }

    private void addEatEffect(Player player, FoodProperties properties) {
        if (!player.level().isClientSide()) {
            for(FoodProperties.PossibleEffect foodproperties$possibleeffect : properties.effects()) {
                if (player.level().random.nextFloat() < foodproperties$possibleeffect.probability()) {
                    player.addEffect(foodproperties$possibleeffect.effect());
                }
            }
        }

    }

    public record CureProperties(EffectCure cure, int amount, float chance) {
        public static final Codec<CureProperties> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EffectCure.CODEC.fieldOf("cure").forGetter(CureProperties::cure),
                Codec.INT.optionalFieldOf("amount", 1).forGetter(CureProperties::amount),
                Codec.FLOAT.optionalFieldOf("chance", 1.0F).forGetter(CureProperties::chance)
        ).apply(instance, CureProperties::new));

        @SuppressWarnings("SuspiciousMethodCalls")
        public void affectConsumer(Level level, Player consumer) {
            Iterator<MobEffectInstance> itr = consumer.getActiveEffects().iterator();
            ArrayList<Holder<MobEffect>> compatibleEffects = new ArrayList<>();

            while (itr.hasNext()) {
                MobEffectInstance effect = itr.next();
                if (effect.getCures().contains(this.cure())) {
                    compatibleEffects.add(effect.getEffect());
                }
            }

            if (!compatibleEffects.isEmpty()) {
                for (int i = 0; i <= amount; i++) {
                    MobEffectInstance selectedEffect = consumer.getEffect(compatibleEffects.get(level.random.nextInt(compatibleEffects.size())));
                    if (level.random.nextFloat() < this.chance()) {
                        if (selectedEffect != null && !EventHooks.onEffectRemoved(consumer, selectedEffect, this.cure())) {
                            consumer.removeEffect(selectedEffect.getEffect());
                        }
                        compatibleEffects.remove(selectedEffect);
                    }
                }
            }
        }
    }
}
