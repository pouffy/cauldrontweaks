package io.github.pouffy.cauldrontweaks.common.data.interaction.types.builtin;

import com.mojang.serialization.MapCodec;
import io.github.pouffy.cauldrontweaks.CauldronTweaks;
import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import io.github.pouffy.cauldrontweaks.common.data.interaction.CauldronInteractionType;
import io.github.pouffy.cauldrontweaks.common.data.interaction.ICauldronInteraction;
import io.github.pouffy.cauldrontweaks.helpers.FluidContainerHelper;
import io.github.pouffy.cauldrontweaks.helpers.FluidHelper;
import io.github.pouffy.cauldrontweaks.init.CauldronInteractions;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class BucketFillInteraction implements ICauldronInteraction {

    public static final MapCodec<BucketFillInteraction> CODEC = MapCodec.unit(new BucketFillInteraction());

    public BucketFillInteraction() {}

    @Override
    public CauldronInteractionType<?> getType() {
        return CauldronInteractions.FILL_BUCKET.get();
    }

    @Override
    public ItemInteractionResult interact(CauldronBlockEntity cauldron, FluidStack fluidStack, Player player, InteractionHand hand, ItemStack stack) {
        if (FluidContainerHelper.canItemBeFilled(stack)) {
            FluidStack fluid = cauldron.getFluidStack();
            int requiredAmountForItem = FluidContainerHelper.getRequiredAmountForItem(cauldron.getLevel(), stack, fluid.copy());
            if (fluid.isEmpty() || requiredAmountForItem == -1 || requiredAmountForItem > fluid.getAmount())
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            if (player.isCreative())
                stack = stack.copy();
            ItemStack out = FluidContainerHelper.fillItem(cauldron.getLevel(), requiredAmountForItem, stack, fluid.copy());
            FluidStack copy = fluid.copy();
            copy.setAmount(requiredAmountForItem);
            cauldron.getTank().drain(copy, IFluidHandler.FluidAction.EXECUTE);
            player.level().playSound(player, cauldron.getBlockPos(), FluidHelper.getFillSound(copy), SoundSource.BLOCKS, 1.0F, 1.0F);
            if (!player.isCreative())
                player.getInventory()
                        .placeItemBackInInventory(out);
            cauldron.notifyUpdate();
            return ItemInteractionResult.sidedSuccess(player.level().isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
