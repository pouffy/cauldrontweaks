package io.github.pouffy.cauldrontweaks.common.data.interaction.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.pouffy.cauldrontweaks.CauldronTweaks;
import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import io.github.pouffy.cauldrontweaks.common.data.condition.CauldronCondition;
import io.github.pouffy.cauldrontweaks.common.data.interaction.CauldronInteractionType;
import io.github.pouffy.cauldrontweaks.common.data.interaction.ICauldronInteraction;
import io.github.pouffy.cauldrontweaks.common.data.result.fluid.CauldronFluidResult;
import io.github.pouffy.cauldrontweaks.common.data.result.item.CauldronItemResult;
import io.github.pouffy.cauldrontweaks.common.data.result.item.type.NoOpItemResult;
import io.github.pouffy.cauldrontweaks.init.CauldronInteractions;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.EffectCure;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public record DrinkContentsInteraction(List<CauldronCondition> conditions, CauldronFluidResult fluidResult, Holder<SoundEvent> eatSound, Optional<FoodProperties> foodProperties, Optional<CureProperties> cureProperties) implements ICauldronInteraction {
    public static final MapCodec<DrinkContentsInteraction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CauldronCondition.CODEC.listOf().fieldOf("conditions").forGetter(DrinkContentsInteraction::conditions),
            CauldronFluidResult.CODEC.fieldOf("result").forGetter(DrinkContentsInteraction::fluidResult),
            SoundEvent.CODEC.fieldOf("sound").forGetter(DrinkContentsInteraction::eatSound),
            FoodProperties.DIRECT_CODEC.optionalFieldOf("food").forGetter(DrinkContentsInteraction::foodProperties),
            CureProperties.CODEC.optionalFieldOf("cure").forGetter(DrinkContentsInteraction::cureProperties)
    ).apply(instance, DrinkContentsInteraction::new));

    @Override
    public CauldronInteractionType<?> getType() {
        return CauldronInteractions.DRINK_CONTENTS.get();
    }

    @Override
    public boolean test(CauldronBlockEntity cauldron, FluidStack fluidStack, Player player, InteractionHand hand, ItemStack stack) {
        return CauldronCondition.test(this.conditions, cauldron, player, hand, stack);
    }

    @Override
    public void run(CauldronBlockEntity cauldron, FluidStack fluidStack, Player player, InteractionHand hand, ItemStack stack) {
        foodProperties.ifPresent(props -> {
            player.getFoodData().eat(props);
            addEatEffect(player, props);
        });
        cureProperties.ifPresent(props -> props.affectConsumer(cauldron.getLevel(), player));
        player.playSound(eatSound.value(), 1.0F, 1.0F);
    }

    @Override
    public CauldronItemResult getItemResult(ItemStack usedItem, FluidStack usedFluid, Player player) {
        return NoOpItemResult.INSTANCE;
    }

    @Override
    public CauldronFluidResult getFluidResult(ItemStack usedItem, FluidStack usedFluid) {
        return this.fluidResult;
    }

    private void addEatEffect(Player player, FoodProperties properties) {
        int effects = 0;
        if (!player.level().isClientSide()) {
            for(FoodProperties.PossibleEffect foodproperties$possibleeffect : properties.effects()) {
                if (player.level().random.nextFloat() < foodproperties$possibleeffect.probability()) {
                    player.addEffect(foodproperties$possibleeffect.effect());
                    effects++;
                }
            }
        }
        CauldronTweaks.LOGGER.info("Added {} food effects", effects);
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
            int effects = 0;
            if (!compatibleEffects.isEmpty()) {
                for (int i = 0; i <= amount; i++) {
                    MobEffectInstance selectedEffect = consumer.getEffect(compatibleEffects.get(level.random.nextInt(compatibleEffects.size())));
                    if (level.random.nextFloat() < this.chance()) {
                        if (selectedEffect != null && !EventHooks.onEffectRemoved(consumer, selectedEffect, this.cure())) {
                            consumer.removeEffect(selectedEffect.getEffect());
                            compatibleEffects.remove(selectedEffect);
                            effects++;
                        }
                    }
                }
            }
            CauldronTweaks.LOGGER.info("Cleared {} effects through cure: {}", effects, this.cure.name());
        }
    }
}
