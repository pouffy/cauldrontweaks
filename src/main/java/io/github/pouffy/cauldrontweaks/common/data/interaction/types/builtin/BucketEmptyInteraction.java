package io.github.pouffy.cauldrontweaks.common.data.interaction.types.builtin;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import io.github.pouffy.cauldrontweaks.common.data.CauldronFluidIngredient;
import io.github.pouffy.cauldrontweaks.common.data.interaction.CauldronInteractionType;
import io.github.pouffy.cauldrontweaks.common.data.interaction.ICauldronInteraction;
import io.github.pouffy.cauldrontweaks.helpers.FluidContainerHelper;
import io.github.pouffy.cauldrontweaks.helpers.FluidHelper;
import io.github.pouffy.cauldrontweaks.init.CauldronInteractions;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class BucketEmptyInteraction implements ICauldronInteraction {

    public static final MapCodec<BucketEmptyInteraction> CODEC = MapCodec.unit(new BucketEmptyInteraction());

    public BucketEmptyInteraction() {}

    @Override
    public CauldronInteractionType<?> getType() {
        return CauldronInteractions.EMPTY_BUCKET.get();
    }

    @Override
    public void runExtra(CauldronBlockEntity cauldron, FluidStack fluidStack, Player player, InteractionHand hand, ItemStack stack) {
        player.level().playSound(player, cauldron.getBlockPos(), FluidHelper.getEmptySound(getFluidResult(stack, fluidStack)), SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    @Override
    public boolean testExtra(CauldronBlockEntity cauldron, FluidStack fluidStack, Player player, InteractionHand hand, ItemStack stack) {
        return FluidContainerHelper.canItemBeEmptied(stack) && cauldron.canAccept(getFluidResult(stack, fluidStack));
    }

    @Override
    public ItemStack getItemResult(ItemStack usedItem, FluidStack usedFluid, Player player) {
        if (FluidContainerHelper.canItemBeEmptied(usedItem)) {
            Pair<FluidStack, ItemStack> emptyingResult = FluidContainerHelper.emptyItem(usedItem, true);
            return emptyingResult.getSecond();
        }
        return usedItem;
    }

    @Override
    public FluidStack getFluidResult(ItemStack usedItem, FluidStack usedFluid) {
        if (FluidContainerHelper.canItemBeEmptied(usedItem)) {
            Pair<FluidStack, ItemStack> emptyingResult = FluidContainerHelper.emptyItem(usedItem, true);
            return emptyingResult.getFirst();
        }
        return FluidStack.EMPTY;
    }

    @Override
    public int fluidAmountChange(ItemStack usedItem, FluidStack usedFluid) {
        if (FluidContainerHelper.canItemBeEmptied(usedItem)) {
            Pair<FluidStack, ItemStack> emptyingResult = FluidContainerHelper.emptyItem(usedItem, true);
            return emptyingResult.getFirst().getAmount();
        }
        return fluidAmountChange();
    }

    @Override
    public Ingredient getItemInput() {
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
