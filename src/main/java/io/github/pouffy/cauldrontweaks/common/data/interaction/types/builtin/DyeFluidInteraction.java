package io.github.pouffy.cauldrontweaks.common.data.interaction.types.builtin;

import com.mojang.serialization.MapCodec;
import io.github.pouffy.cauldrontweaks.CauldronTweaks;
import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import io.github.pouffy.cauldrontweaks.common.data.CauldronFluidIngredient;
import io.github.pouffy.cauldrontweaks.common.data.interaction.CauldronInteractionType;
import io.github.pouffy.cauldrontweaks.common.data.interaction.ICauldronInteraction;
import io.github.pouffy.cauldrontweaks.common.fluid.DyedFluidColor;
import io.github.pouffy.cauldrontweaks.helpers.FluidHelper;
import io.github.pouffy.cauldrontweaks.init.CauldronInteractions;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DyeFluidInteraction implements ICauldronInteraction {

    public static final MapCodec<DyeFluidInteraction> CODEC = MapCodec.unit(new DyeFluidInteraction());

    @Override
    public CauldronInteractionType<?> getType() {
        return CauldronInteractions.DYE_FLUID.get();
    }

    @Override
    public void runExtra(CauldronBlockEntity cauldron, FluidStack fluidStack, Player player, InteractionHand hand, ItemStack stack) {
        player.level().playSound(player, cauldron.getBlockPos(), FluidHelper.getFillSound(fluidStack), SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    @Override
    public boolean testExtra(CauldronBlockEntity cauldron, FluidStack fluidStack, Player player, InteractionHand hand, ItemStack stack) {
        return fluidStack.is(CauldronTweaks.DYEABLE_FLUID) && stack.getItem() instanceof DyeItem;
    }

    @Override
    public ItemStack getItemResult(ItemStack usedItem, FluidStack usedFluid, Player player) {
        return ItemStack.EMPTY;
    }

    @Override
    public FluidStack getFluidResult(ItemStack usedItem, FluidStack usedFluid) {
        if (usedItem.getItem() instanceof DyeItem dyeItem) {
            return DyedFluidColor.applyDyes(usedFluid, List.of(dyeItem));
        }
        return usedFluid;
    }

    @Override
    public @Nullable Ingredient getItemInput() {
        return null;
    }

    @Override
    public @Nullable CauldronFluidIngredient getFluidInput() {
        return null;
    }

    @Override
    public int fluidAmountChange() {
        return 0;
    }
}
