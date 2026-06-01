package io.github.pouffy.cauldrontweaks.common.data.interaction.types.builtin;

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

public class BucketFillInteraction implements ICauldronInteraction {

    public static final MapCodec<BucketFillInteraction> CODEC = MapCodec.unit(new BucketFillInteraction());

    public BucketFillInteraction() {}

    @Override
    public CauldronInteractionType<?> getType() {
        return CauldronInteractions.FILL_BUCKET.get();
    }

    @Override
    public void runExtra(CauldronBlockEntity cauldron, FluidStack fluidStack, Player player, InteractionHand hand, ItemStack stack) {
        player.level().playSound(player, cauldron.getBlockPos(), FluidHelper.getFillSound(fluidStack), SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    @Override
    public boolean testExtra(CauldronBlockEntity cauldron, FluidStack fluidStack, Player player, InteractionHand hand, ItemStack stack) {
        int requiredAmountForItem = FluidContainerHelper.getRequiredAmountForItem(stack, fluidStack.copy());
        return FluidContainerHelper.canItemBeFilled(stack) && !(fluidStack.isEmpty() || requiredAmountForItem == -1 || requiredAmountForItem > fluidStack.getAmount());
    }

    @Override
    public ItemStack getItemResult(ItemStack usedItem, FluidStack usedFluid, Player player) {
        if (FluidContainerHelper.canItemBeFilled(usedItem)) {
            int requiredAmountForItem = FluidContainerHelper.getRequiredAmountForItem(usedItem, usedFluid.copy());
            if (player.isCreative())
                usedItem = usedItem.copy();
            return FluidContainerHelper.fillItem(requiredAmountForItem, usedItem, usedFluid.copy());
        }
        return usedItem;
    }

    @Override
    public FluidStack getFluidResult(ItemStack usedItem, FluidStack usedFluid) {
        return usedFluid;
    }

    @Override
    public int fluidAmountChange(ItemStack usedItem, FluidStack usedFluid) {
        if (FluidContainerHelper.canItemBeFilled(usedItem)) {
            int requiredAmountForItem = FluidContainerHelper.getRequiredAmountForItem(usedItem, usedFluid.copy());
            return -requiredAmountForItem;
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
