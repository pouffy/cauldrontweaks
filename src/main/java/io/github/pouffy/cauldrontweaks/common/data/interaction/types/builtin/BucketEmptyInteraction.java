package io.github.pouffy.cauldrontweaks.common.data.interaction.types.builtin;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import io.github.pouffy.cauldrontweaks.CauldronTweaks;
import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import io.github.pouffy.cauldrontweaks.common.data.interaction.CauldronInteractionType;
import io.github.pouffy.cauldrontweaks.common.data.interaction.ICauldronInteraction;
import io.github.pouffy.cauldrontweaks.helpers.FluidContainerHelper;
import io.github.pouffy.cauldrontweaks.helpers.FluidHelper;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class BucketEmptyInteraction implements ICauldronInteraction {

    public static final MapCodec<BucketEmptyInteraction> CODEC = MapCodec.unit(new BucketEmptyInteraction());

    public BucketEmptyInteraction() {}

    @Override
    public CauldronInteractionType<?> getType() {
        return CauldronTweaks.EMPTY_BUCKET.get();
    }

    @Override
    public ItemInteractionResult interact(CauldronBlockEntity cauldron, FluidStack fluidStack, Player player, InteractionHand hand, ItemStack stack) {
        if (FluidContainerHelper.canItemBeEmptied(stack)) {
            Pair<FluidStack, ItemStack> emptyingResult = FluidContainerHelper.emptyItem(cauldron.getLevel(), stack, true);
            FluidStack fluid = emptyingResult.getFirst();
            if (cauldron.canAccept(fluid)) {
                ItemStack copyOfHeld = stack.copy();
                emptyingResult = FluidContainerHelper.emptyItem(cauldron.getLevel(), copyOfHeld, false);
                cauldron.getTank().fill(fluid, IFluidHandler.FluidAction.EXECUTE);
                player.level().playSound(player, cauldron.getBlockPos(), FluidHelper.getEmptySound(fluid), SoundSource.BLOCKS, 1.0F, 1.0F);
                if (!player.isCreative()) {
                    if (copyOfHeld.isEmpty())
                        player.setItemInHand(hand, emptyingResult.getSecond());
                    else {
                        player.setItemInHand(hand, copyOfHeld);
                        player.getInventory()
                                .placeItemBackInInventory(emptyingResult.getSecond());
                    }
                }
                return ItemInteractionResult.sidedSuccess(player.level().isClientSide);
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
