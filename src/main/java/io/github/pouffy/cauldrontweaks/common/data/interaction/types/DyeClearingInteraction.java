package io.github.pouffy.cauldrontweaks.common.data.interaction.types;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import io.github.pouffy.cauldrontweaks.common.data.interaction.CauldronInteractionType;
import io.github.pouffy.cauldrontweaks.common.data.interaction.ICauldronInteraction;
import io.github.pouffy.cauldrontweaks.helpers.FluidHelper;
import io.github.pouffy.cauldrontweaks.init.CauldronInteractions;
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
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.Optional;

public record DyeClearingInteraction(Ingredient input, SizedFluidIngredient fluid, Optional<ItemStack> result) implements ICauldronInteraction {
    public static final MapCodec<DyeClearingInteraction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.fieldOf("input").forGetter(DyeClearingInteraction::input),
            SizedFluidIngredient.FLAT_CODEC.fieldOf("fluid").forGetter(DyeClearingInteraction::fluid),
            ItemStack.CODEC.optionalFieldOf("result").forGetter(DyeClearingInteraction::result)
    ).apply(instance, DyeClearingInteraction::new));

    @Override
    public CauldronInteractionType<?> getType() {
        return CauldronInteractions.CLEAR_DYE.get();
    }

    @Override
    public ItemInteractionResult interact(CauldronBlockEntity cauldron, FluidStack fluidStack, Player player, InteractionHand hand, ItemStack stack) {
        DyedItemColor color = fluidStack.get(DataComponents.DYED_COLOR);
        if (color != null) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (fluid().test(fluidStack) && input.test(stack) && (cauldron.getTank().getFluidAmount() - fluid().amount()) >= 0) {
            stack.remove(DataComponents.DYED_COLOR);
            player.level().playSound(player, cauldron.getBlockPos(), FluidHelper.getEmptySound(fluidStack), SoundSource.BLOCKS, 1.0F, 1.0F);
            cauldron.getTank().drain(fluid().amount(), IFluidHandler.FluidAction.EXECUTE);
            return ItemInteractionResult.sidedSuccess(player.level().isClientSide());
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
