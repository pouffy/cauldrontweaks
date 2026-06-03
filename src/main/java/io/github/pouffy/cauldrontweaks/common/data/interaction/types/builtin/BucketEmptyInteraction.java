package io.github.pouffy.cauldrontweaks.common.data.interaction.types.builtin;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import io.github.pouffy.cauldrontweaks.common.data.condition.type.builtin.CanBeEmptiedCondition;
import io.github.pouffy.cauldrontweaks.common.data.interaction.CauldronInteractionType;
import io.github.pouffy.cauldrontweaks.common.data.interaction.ICauldronInteraction;
import io.github.pouffy.cauldrontweaks.common.data.result.fluid.CauldronFluidResult;
import io.github.pouffy.cauldrontweaks.common.data.result.fluid.type.FillFluidResult;
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

public class BucketEmptyInteraction implements ICauldronInteraction {

    public static final MapCodec<BucketEmptyInteraction> CODEC = MapCodec.unit(new BucketEmptyInteraction());

    public BucketEmptyInteraction() {}

    @Override
    public CauldronInteractionType<?> getType() {
        return CauldronInteractions.EMPTY_BUCKET.get();
    }

    @Override
    public boolean test(CauldronBlockEntity cauldron, FluidStack fluidStack, Player player, InteractionHand hand, ItemStack stack) {
        Pair<FluidStack, ItemStack> emptyingResult = FluidContainerHelper.emptyItem(stack, true);
        return CanBeEmptiedCondition.INSTANCE.isValid(cauldron, player, hand, stack) && cauldron.canAccept(emptyingResult.getFirst());
    }

    @Override
    public void run(CauldronBlockEntity cauldron, FluidStack fluidStack, Player player, InteractionHand hand, ItemStack stack) {
        player.level().playSound(player, cauldron.getBlockPos(), FluidHelper.getEmptySound(emptyResult(stack).getFirst()), SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    @Override
    public CauldronItemResult getItemResult(ItemStack usedItem, FluidStack usedFluid, Player player) {
        if (FluidContainerHelper.canItemBeEmptied(usedItem)) {
            return new TransmuteItemResult(emptyResult(usedItem).getSecond(), List.of());
        }
        return NoOpItemResult.INSTANCE;
    }

    @Override
    public CauldronFluidResult getFluidResult(ItemStack usedItem, FluidStack usedFluid) {
        if (FluidContainerHelper.canItemBeEmptied(usedItem)) {
            return new FillFluidResult(emptyResult(usedItem).getFirst());
        }
        return NoOpFluidResult.INSTANCE;
    }

    private Pair<FluidStack, ItemStack> emptyResult(ItemStack stack) {
        return FluidContainerHelper.emptyItem(stack, true);
    }
}
