package io.github.pouffy.cauldrontweaks.common.data.interaction;

import com.mojang.serialization.Codec;
import io.github.pouffy.cauldrontweaks.CauldronTweaks;
import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import io.github.pouffy.cauldrontweaks.common.data.condition.CauldronCondition;
import io.github.pouffy.cauldrontweaks.common.data.result.fluid.CauldronFluidResult;
import io.github.pouffy.cauldrontweaks.common.data.result.item.CauldronItemResult;
import io.github.pouffy.cauldrontweaks.init.CauldronRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public interface ICauldronInteraction {

    Codec<ICauldronInteraction> CODEC = Codec.lazyInitialized(CauldronRegistries.CAULDRON_INTERACTION_TYPE_REGISTRY::byNameCodec).dispatch("type", ICauldronInteraction::getType, CauldronInteractionType::codec);

    CauldronInteractionType<?> getType();

    default ItemInteractionResult interact(CauldronBlockEntity cauldron, FluidStack fluidStack, Player player, InteractionHand hand, ItemStack stack) {
        if (test(cauldron, fluidStack, player, hand, stack)) {
            CauldronTweaks.LOGGER.info("All Conditions met, running results...");
            CauldronTweaks.LOGGER.info("Running Fluid Result");
            this.getFluidResult(stack, cauldron.getFluidStack()).alterTank(cauldron, stack);
            CauldronTweaks.LOGGER.info("Fluid Result Run... Running Item Result");
            this.getItemResult(stack, cauldron.getFluidStack(), player).alterPlayer(player, hand, stack, cauldron.getFluidStack());
            CauldronTweaks.LOGGER.info("Item Result Run... Running Extra Logic");
            run(cauldron, fluidStack, player, hand, stack);
            CauldronTweaks.LOGGER.info("Extra Logic Run... Interaction Success");
            return ItemInteractionResult.sidedSuccess(player.level().isClientSide());
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    void run(CauldronBlockEntity cauldron, FluidStack fluidStack, Player player, InteractionHand hand, ItemStack stack);

    boolean test(CauldronBlockEntity cauldron, FluidStack fluidStack, Player player, InteractionHand hand, ItemStack stack);

    CauldronItemResult getItemResult(ItemStack usedItem, FluidStack usedFluid, Player player);

    CauldronFluidResult getFluidResult(ItemStack usedItem, FluidStack usedFluid);
}
