package io.github.pouffy.cauldrontweaks.common.data.interaction.types;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.pouffy.cauldrontweaks.CauldronTweaks;
import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import io.github.pouffy.cauldrontweaks.common.data.interaction.CauldronInteractionType;
import io.github.pouffy.cauldrontweaks.common.data.interaction.ICauldronInteraction;
import io.github.pouffy.cauldrontweaks.helpers.FluidHelper;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public record FillContainerInteraction(Ingredient empty, ItemStack filled, SizedFluidIngredient fluid) implements ICauldronInteraction {

    public static final MapCodec<FillContainerInteraction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.fieldOf("empty").forGetter(FillContainerInteraction::empty),
            ItemStack.CODEC.fieldOf("filled").forGetter(FillContainerInteraction::filled),
            SizedFluidIngredient.FLAT_CODEC.fieldOf("fluid").forGetter(FillContainerInteraction::fluid)
    ).apply(instance, FillContainerInteraction::new));

    @Override
    public CauldronInteractionType<?> getType() {
        return CauldronTweaks.FILL_CONTAINER.get();
    }

    @Override
    public ItemInteractionResult interact(CauldronBlockEntity cauldron, FluidStack fluidStack, Player player, InteractionHand hand, ItemStack stack) {
        if (empty.test(stack) && fluid.test(fluidStack)) {
            int requiredAmountForItem = fluid.amount();
            if (fluidStack.isEmpty() || requiredAmountForItem == -1 || requiredAmountForItem > fluidStack.getAmount())
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            if (player.isCreative())
                stack = stack.copy();
            stack.shrink(1);
            ItemStack out = filled.copy();
            FluidStack copy = fluidStack.copy();
            copy.setAmount(requiredAmountForItem);
            cauldron.getTank().drain(copy, IFluidHandler.FluidAction.EXECUTE);
            player.level().playSound(player, cauldron.getBlockPos(), FluidHelper.getFillSound(fluidStack), SoundSource.BLOCKS, 1.0F, 1.0F);
            if (!player.isCreative())
                player.getInventory()
                        .placeItemBackInInventory(out);
            cauldron.notifyUpdate();
            return ItemInteractionResult.sidedSuccess(player.level().isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
