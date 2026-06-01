package io.github.pouffy.cauldrontweaks.common.data.interaction.types.builtin;

import com.mojang.serialization.MapCodec;
import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import io.github.pouffy.cauldrontweaks.common.data.CauldronFluidIngredient;
import io.github.pouffy.cauldrontweaks.common.data.interaction.CauldronInteractionType;
import io.github.pouffy.cauldrontweaks.common.data.interaction.ICauldronInteraction;
import io.github.pouffy.cauldrontweaks.init.CauldronFluids;
import io.github.pouffy.cauldrontweaks.init.CauldronInteractions;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class DrinkPotionInteraction implements ICauldronInteraction {

    public static final MapCodec<DrinkPotionInteraction> CODEC = MapCodec.unit(new DrinkPotionInteraction());

    @Override
    public CauldronInteractionType<?> getType() {
        return CauldronInteractions.DRINK_POTION.get();
    }

    @Override
    public void runExtra(CauldronBlockEntity cauldron, FluidStack fluidStack, Player player, InteractionHand hand, ItemStack stack) {
        if (fluidStack.has(DataComponents.POTION_CONTENTS)) {
            PotionContents potionContents = fluidStack.get(DataComponents.POTION_CONTENTS);
            affectConsumer(potionContents, player);
            player.playSound(SoundEvents.GENERIC_DRINK, 1.0F, 1.0F);
        }
    }

    @Override
    public boolean testExtra(CauldronBlockEntity cauldron, FluidStack fluidStack, Player player, InteractionHand hand, ItemStack stack) {
        boolean handTest;
        if (hand == InteractionHand.OFF_HAND) {
            handTest = player.isShiftKeyDown();
        } else {
            handTest = player.getItemInHand(hand).isEmpty();
        }
        return handTest && fluidStack.has(DataComponents.POTION_CONTENTS) && fluidStack.getAmount() >= 250;
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
    public Ingredient getItemInput() {
        return null;
    }

    @Override
    public @Nullable CauldronFluidIngredient getFluidInput() {
        return CauldronFluidIngredient.of(CauldronFluids.POTION.get(), 250);
    }

    @Override
    public int fluidAmountChange() {
        return -250;
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
