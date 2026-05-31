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

public record EmptyContainerInteraction(Ingredient filled, ItemStack empty, FluidStack fluid) implements ICauldronInteraction {

    public static final MapCodec<EmptyContainerInteraction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.fieldOf("filled").forGetter(EmptyContainerInteraction::filled),
            ItemStack.CODEC.fieldOf("empty").forGetter(EmptyContainerInteraction::empty),
            FluidStack.CODEC.fieldOf("fluid").forGetter(EmptyContainerInteraction::fluid)
    ).apply(instance, EmptyContainerInteraction::new));

    @Override
    public CauldronInteractionType<?> getType() {
        return CauldronTweaks.EMPTY_CONTAINER.get();
    }

    @Override
    public ItemInteractionResult interact(CauldronBlockEntity cauldron, FluidStack fluidStack, Player player, InteractionHand hand, ItemStack stack) {
        if (filled.test(stack) && cauldron.canAccept(fluid)) {
            ItemStack copyOfHeld = stack.copy();
            stack.shrink(1);
            ItemStack split = empty;
            cauldron.getTank().fill(fluid, IFluidHandler.FluidAction.EXECUTE);
            player.level().playSound(player, cauldron.getBlockPos(), FluidHelper.getEmptySound(fluid), SoundSource.BLOCKS, 1.0F, 1.0F);
            if (!player.isCreative()) {
                if (copyOfHeld.isEmpty())
                    player.setItemInHand(hand, split);
                else {
                    player.setItemInHand(hand, copyOfHeld);
                    player.getInventory().placeItemBackInInventory(split);
                }
            }
            return ItemInteractionResult.sidedSuccess(player.level().isClientSide());
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
