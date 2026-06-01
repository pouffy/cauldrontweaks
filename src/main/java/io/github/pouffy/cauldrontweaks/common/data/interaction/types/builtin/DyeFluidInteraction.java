package io.github.pouffy.cauldrontweaks.common.data.interaction.types.builtin;

import com.mojang.serialization.MapCodec;
import io.github.pouffy.cauldrontweaks.CauldronTweaks;
import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import io.github.pouffy.cauldrontweaks.common.block.CauldronTank;
import io.github.pouffy.cauldrontweaks.common.data.interaction.CauldronInteractionType;
import io.github.pouffy.cauldrontweaks.common.data.interaction.ICauldronInteraction;
import io.github.pouffy.cauldrontweaks.helpers.CauldronHelper;
import io.github.pouffy.cauldrontweaks.helpers.FluidHelper;
import io.github.pouffy.cauldrontweaks.init.CauldronInteractions;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public class DyeFluidInteraction implements ICauldronInteraction {

    public static final MapCodec<DyeFluidInteraction> CODEC = MapCodec.unit(new DyeFluidInteraction());

    @Override
    public CauldronInteractionType<?> getType() {
        return CauldronInteractions.DYE_FLUID.get();
    }

    @Override
    public ItemInteractionResult interact(CauldronBlockEntity cauldron, FluidStack fluidStack, Player player, InteractionHand hand, ItemStack stack) {
        if (fluidStack.is(CauldronTweaks.DYEABLE_FLUID) && stack.getItem() instanceof DyeItem dyeItem) {
            if (!CauldronHelper.handleItemConsume(player, hand, stack, ItemStack.EMPTY, true)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            FluidStack newStack = CauldronTank.applyDyes(fluidStack, List.of(dyeItem));
            cauldron.getTank().setFluid(newStack);
            player.level().playSound(player, cauldron.getBlockPos(), FluidHelper.getFillSound(fluidStack), SoundSource.BLOCKS, 1.0F, 1.0F);
            return ItemInteractionResult.sidedSuccess(player.level().isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
