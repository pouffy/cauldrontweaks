package io.github.pouffy.cauldrontweaks.common.data.interaction.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.pouffy.cauldrontweaks.CauldronTweaks;
import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import io.github.pouffy.cauldrontweaks.common.data.interaction.CauldronInteractionType;
import io.github.pouffy.cauldrontweaks.common.data.interaction.ICauldronInteraction;
import io.github.pouffy.cauldrontweaks.helpers.FluidHelper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public record DyeItemInteraction(Ingredient input, int drainAmount) implements ICauldronInteraction {

    public static final MapCodec<DyeItemInteraction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.fieldOf("input").forGetter(DyeItemInteraction::input),
            Codec.INT.optionalFieldOf("drain_amount", 0).forGetter(DyeItemInteraction::drainAmount)
    ).apply(instance, DyeItemInteraction::new));


    @Override
    public CauldronInteractionType<?> getType() {
        return CauldronTweaks.DYE_ITEM.get();
    }

    @Override
    public ItemInteractionResult interact(CauldronBlockEntity cauldron, FluidStack fluidStack, Player player, InteractionHand hand, ItemStack stack) {
        DyedItemColor color = fluidStack.get(DataComponents.DYED_COLOR);
        if (color != null && (cauldron.getTank().getFluidAmount() - drainAmount) >= 0 && input.test(stack)) {
            stack.set(DataComponents.DYED_COLOR, color);
            player.level().playSound(player, cauldron.getBlockPos(), FluidHelper.getEmptySound(fluidStack), SoundSource.BLOCKS, 1.0F, 1.0F);
            cauldron.getTank().drain(drainAmount, IFluidHandler.FluidAction.EXECUTE);
            return ItemInteractionResult.sidedSuccess(player.level().isClientSide());
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
