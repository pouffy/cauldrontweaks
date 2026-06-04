package io.github.pouffy.cauldrontweaks.common.data.interaction.types.builtin;

import com.mojang.serialization.MapCodec;
import io.github.pouffy.cauldrontweaks.CauldronTweaks;
import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import io.github.pouffy.cauldrontweaks.common.data.CauldronFluidIngredient;
import io.github.pouffy.cauldrontweaks.common.data.condition.CauldronCondition;
import io.github.pouffy.cauldrontweaks.common.data.condition.type.EmptyHandCondition;
import io.github.pouffy.cauldrontweaks.common.data.condition.type.FluidAmountCondition;
import io.github.pouffy.cauldrontweaks.common.data.condition.type.FluidComponentsCondition;
import io.github.pouffy.cauldrontweaks.common.data.condition.type.FluidCondition;
import io.github.pouffy.cauldrontweaks.common.data.condition.type.builtin.DyeItemCondition;
import io.github.pouffy.cauldrontweaks.common.data.interaction.CauldronInteractionType;
import io.github.pouffy.cauldrontweaks.common.data.interaction.ICauldronInteraction;
import io.github.pouffy.cauldrontweaks.common.data.misc.int_test.IntTest;
import io.github.pouffy.cauldrontweaks.common.data.result.fluid.CauldronFluidResult;
import io.github.pouffy.cauldrontweaks.common.data.result.fluid.type.DrainFluidResult;
import io.github.pouffy.cauldrontweaks.common.data.result.item.CauldronItemResult;
import io.github.pouffy.cauldrontweaks.common.data.result.item.type.NoOpItemResult;
import io.github.pouffy.cauldrontweaks.init.CauldronFluids;
import io.github.pouffy.cauldrontweaks.init.CauldronInteractions;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.IntRange;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class DrinkPotionInteraction implements ICauldronInteraction {

    public static final MapCodec<DrinkPotionInteraction> CODEC = MapCodec.unit(new DrinkPotionInteraction());

    @Override
    public CauldronInteractionType<?> getType() {
        return CauldronInteractions.DRINK_POTION.get();
    }

    @Override
    public void run(CauldronBlockEntity cauldron, FluidStack fluidStack, Player player, InteractionHand hand, ItemStack stack) {
        if (fluidStack.has(DataComponents.POTION_CONTENTS)) {
            PotionContents potionContents = fluidStack.get(DataComponents.POTION_CONTENTS);
            affectConsumer(potionContents, player);
            player.playSound(SoundEvents.GENERIC_DRINK, 1.0F, 1.0F);
            CauldronTweaks.LOGGER.info("Drank Potion {}", potionContents);
        }
    }

    @Override
    public boolean test(CauldronBlockEntity cauldron, FluidStack fluidStack, Player player, InteractionHand hand, ItemStack stack) {
        return CauldronCondition.test(List.of(EmptyHandCondition.INSTANCE, new FluidAmountCondition(IntTest.above(250)), new FluidComponentsCondition(DataComponentPredicate.EMPTY, List.of(Holder.direct(DataComponents.POTION_CONTENTS)))), cauldron, player, hand, stack);
    }

    @Override
    public CauldronItemResult getItemResult(ItemStack usedItem, FluidStack usedFluid, Player player) {
        return NoOpItemResult.INSTANCE;
    }

    @Override
    public CauldronFluidResult getFluidResult(ItemStack usedItem, FluidStack usedFluid) {
        return new DrainFluidResult(250, Optional.empty());
    }

    private void affectConsumer(PotionContents potionContents, Player player) {
        if (potionContents == null) return;
        potionContents.forEachEffect(instance -> {
            if (instance.getEffect().value().isInstantenous()) {
                instance.getEffect().value().applyInstantenousEffect(player, player, player, instance.getAmplifier(), 1.0);
            } else {
                player.addEffect(instance);
            }
        });
    }
}
