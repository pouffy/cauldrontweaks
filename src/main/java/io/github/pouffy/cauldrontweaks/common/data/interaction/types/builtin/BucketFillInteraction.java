package io.github.pouffy.cauldrontweaks.common.data.interaction.types.builtin;

import com.mojang.serialization.MapCodec;
import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import io.github.pouffy.cauldrontweaks.common.data.condition.type.builtin.CanBeFilledCondition;
import io.github.pouffy.cauldrontweaks.common.data.interaction.CauldronInteractionType;
import io.github.pouffy.cauldrontweaks.common.data.interaction.ICauldronInteraction;
import io.github.pouffy.cauldrontweaks.common.data.result.fluid.CauldronFluidResult;
import io.github.pouffy.cauldrontweaks.common.data.result.fluid.type.DrainFluidResult;
import io.github.pouffy.cauldrontweaks.common.data.result.fluid.type.NoOpFluidResult;
import io.github.pouffy.cauldrontweaks.common.data.result.item.CauldronItemResult;
import io.github.pouffy.cauldrontweaks.common.data.result.item.type.NoOpItemResult;
import io.github.pouffy.cauldrontweaks.common.data.result.item.type.TransmuteItemResult;
import io.github.pouffy.cauldrontweaks.helpers.FluidContainerHelper;
import io.github.pouffy.cauldrontweaks.helpers.FluidHelper;
import io.github.pouffy.cauldrontweaks.init.CauldronInteractions;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;
import java.util.Optional;

public class BucketFillInteraction implements ICauldronInteraction {

    public static final MapCodec<BucketFillInteraction> CODEC = MapCodec.unit(new BucketFillInteraction());

    public BucketFillInteraction() {}

    @Override
    public CauldronInteractionType<?> getType() {
        return CauldronInteractions.FILL_BUCKET.get();
    }

    @Override
    public boolean test(CauldronBlockEntity cauldron, FluidStack fluidStack, Player player, InteractionHand hand, ItemStack stack) {
        int requiredAmountForItem = FluidContainerHelper.getRequiredAmountForItem(stack, fluidStack.copy());
        return CanBeFilledCondition.INSTANCE.isValid(cauldron, player, hand, stack) && !(fluidStack.isEmpty() || requiredAmountForItem == -1 || requiredAmountForItem > fluidStack.getAmount());
    }

    @Override
    public void run(CauldronBlockEntity cauldron, FluidStack fluidStack, Player player, InteractionHand hand, ItemStack stack) {
        player.level().playSound(player, cauldron.getBlockPos(), FluidHelper.getFillSound(fluidStack), SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    @Override
    public CauldronItemResult getItemResult(ItemStack usedItem, FluidStack usedFluid, Player player) {
        if (FluidContainerHelper.canItemBeFilled(usedItem)) {
            return new TransmuteItemResult(fillResult(usedItem, usedFluid), List.of());
        }
        return NoOpItemResult.INSTANCE;
    }

    @Override
    public CauldronFluidResult getFluidResult(ItemStack usedItem, FluidStack usedFluid) {
        if (FluidContainerHelper.canItemBeFilled(usedItem)) {
            int requiredAmountForItem = FluidContainerHelper.getRequiredAmountForItem(usedItem, usedFluid.copy());
            return new DrainFluidResult(requiredAmountForItem, Optional.empty());
        }
        return NoOpFluidResult.INSTANCE;
    }

    private ItemStack fillResult(ItemStack stack, FluidStack usedFluid) {
        int requiredAmountForItem = FluidContainerHelper.getRequiredAmountForItem(stack, usedFluid.copy());
        return FluidContainerHelper.fillItem(requiredAmountForItem, stack, usedFluid);
    }
}
