package io.github.pouffy.cauldrontweaks.common.data.interaction.types.builtin;

import com.mojang.serialization.MapCodec;
import io.github.pouffy.cauldrontweaks.CauldronTweaks;
import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import io.github.pouffy.cauldrontweaks.common.data.interaction.CauldronInteractionType;
import io.github.pouffy.cauldrontweaks.common.data.interaction.ICauldronInteraction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class DrinkPotionInteraction implements ICauldronInteraction {

    public static final MapCodec<DrinkPotionInteraction> CODEC = MapCodec.unit(new DrinkPotionInteraction());

    @Override
    public CauldronInteractionType<?> getType() {
        return CauldronTweaks.DRINK_POTION.get();
    }

    @Override
    public ItemInteractionResult interact(CauldronBlockEntity cauldron, FluidStack fluidStack, Player player, InteractionHand hand, ItemStack stack) {
        if (fluidStack.has(DataComponents.POTION_CONTENTS) && stack.isEmpty() && fluidStack.getAmount() >= 250) {
            PotionContents potionContents = fluidStack.get(DataComponents.POTION_CONTENTS);
            affectConsumer(potionContents, player);
            player.playSound(SoundEvents.GENERIC_DRINK, 1.0F, 1.0F);
            FluidStack copy = fluidStack.copy();
            copy.setAmount(250);
            cauldron.getTank().drain(copy, IFluidHandler.FluidAction.EXECUTE);
            return ItemInteractionResult.sidedSuccess(player.level().isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
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
