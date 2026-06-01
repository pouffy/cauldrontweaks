package io.github.pouffy.cauldrontweaks.common.data.interaction;

import com.mojang.serialization.Codec;
import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import io.github.pouffy.cauldrontweaks.common.data.CauldronFluidIngredient;
import io.github.pouffy.cauldrontweaks.helpers.CauldronHelper;
import io.github.pouffy.cauldrontweaks.init.CauldronRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public interface ICauldronInteraction {

    Codec<ICauldronInteraction> CODEC = Codec.lazyInitialized(CauldronRegistries.CAULDRON_INTERACTION_TYPE_REGISTRY::byNameCodec).dispatch("type", ICauldronInteraction::getType, CauldronInteractionType::codec);

    CauldronInteractionType<?> getType();

    @Nullable
    Ingredient getItemInput();
    @Nullable
    CauldronFluidIngredient getFluidInput();

    int fluidAmountChange();

    default boolean damageInput(ItemStack stack, FluidStack fluidStack) {
        return false;
    }

    default ItemInteractionResult interact(CauldronBlockEntity cauldron, FluidStack fluidStack, Player player, InteractionHand hand, ItemStack stack) {
        if (test(stack, fluidStack) && testExtra(cauldron, fluidStack, player, hand, stack)) {
            if (!CauldronHelper.handleItemConsume(player, hand, stack, getItemResult(stack, fluidStack, player), damageInput(stack, fluidStack))) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            alterTank(cauldron, fluidStack, stack);
            runExtra(cauldron, fluidStack, player, hand, stack);
            return ItemInteractionResult.sidedSuccess(player.level().isClientSide());
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    default void alterTank(CauldronBlockEntity cauldron, FluidStack fluidStack, ItemStack stack) {
        FluidStack result = getFluidResult(stack, fluidStack);
        int amountChange = fluidAmountChange(stack, fluidStack);
        if (amountChange == 0) {
            cauldron.getTank().setFluid(result);
        }
        if (amountChange < 0) {
            FluidStack copy = fluidStack.copy();
            copy.setAmount(-amountChange);
            cauldron.getTank().drain(copy, IFluidHandler.FluidAction.EXECUTE);
        } else {
            cauldron.getTank().fill(result, IFluidHandler.FluidAction.EXECUTE);
        }
    }

    void runExtra(CauldronBlockEntity cauldron, FluidStack fluidStack, Player player, InteractionHand hand, ItemStack stack);

    default boolean testExtra(CauldronBlockEntity cauldron, FluidStack fluidStack, Player player, InteractionHand hand, ItemStack stack) { return true; }

    default boolean test(ItemStack usedItem, FluidStack usedFluid) {
        boolean testResult = true;
        if (getItemInput() != null) {
            testResult = getItemInput().test(usedItem);
        }
        if (!testResult) return false;
        if (getFluidInput() != null) {
            testResult = getFluidInput().test(usedFluid);
        }
        return testResult;
    }

    ItemStack getItemResult(ItemStack usedItem, FluidStack usedFluid, Player player);

    FluidStack getFluidResult(ItemStack usedItem, FluidStack usedFluid);

    default int fluidAmountChange(ItemStack usedItem, FluidStack usedFluid) {
        return fluidAmountChange();
    }
}
