package io.github.pouffy.cauldrontweaks.common.data.interaction.types;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.pouffy.cauldrontweaks.CauldronTweaks;
import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import io.github.pouffy.cauldrontweaks.common.data.interaction.CauldronInteractionType;
import io.github.pouffy.cauldrontweaks.common.data.interaction.ICauldronInteraction;
import io.github.pouffy.cauldrontweaks.helpers.CauldronHelper;
import io.github.pouffy.cauldrontweaks.helpers.FluidHelper;
import io.github.pouffy.cauldrontweaks.init.CauldronInteractions;
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
        return CauldronInteractions.EMPTY_CONTAINER.get();
    }

    @Override
    public ItemInteractionResult interact(CauldronBlockEntity cauldron, FluidStack fluidStack, Player player, InteractionHand hand, ItemStack stack) {
        if (filled.test(stack) && cauldron.canAccept(fluid)) {
            if (!CauldronHelper.handleItemConsume(player, hand, stack, empty, false)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            cauldron.getTank().fill(fluid, IFluidHandler.FluidAction.EXECUTE);
            player.level().playSound(player, cauldron.getBlockPos(), FluidHelper.getEmptySound(fluid), SoundSource.BLOCKS, 1.0F, 1.0F);
            return ItemInteractionResult.sidedSuccess(player.level().isClientSide());
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
