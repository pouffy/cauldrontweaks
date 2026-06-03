package io.github.pouffy.cauldrontweaks.common.data.interaction.types.builtin;

import com.mojang.serialization.MapCodec;
import io.github.pouffy.cauldrontweaks.CauldronTweaks;
import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import io.github.pouffy.cauldrontweaks.common.data.condition.CauldronCondition;
import io.github.pouffy.cauldrontweaks.common.data.condition.type.FluidCondition;
import io.github.pouffy.cauldrontweaks.common.data.condition.type.builtin.DyeItemCondition;
import io.github.pouffy.cauldrontweaks.common.data.interaction.CauldronInteractionType;
import io.github.pouffy.cauldrontweaks.common.data.interaction.ICauldronInteraction;
import io.github.pouffy.cauldrontweaks.common.data.result.fluid.CauldronFluidResult;
import io.github.pouffy.cauldrontweaks.common.data.result.fluid.type.AddComponentsFluidResult;
import io.github.pouffy.cauldrontweaks.common.data.result.item.CauldronItemResult;
import io.github.pouffy.cauldrontweaks.common.data.result.item.type.DestroyItemResult;
import io.github.pouffy.cauldrontweaks.helpers.CauldronHelper;
import io.github.pouffy.cauldrontweaks.helpers.FluidHelper;
import io.github.pouffy.cauldrontweaks.init.CauldronInteractions;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
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
    public void run(CauldronBlockEntity cauldron, FluidStack fluidStack, Player player, InteractionHand hand, ItemStack stack) {
        player.level().playSound(player, cauldron.getBlockPos(), FluidHelper.getFillSound(fluidStack), SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    @Override
    public boolean test(CauldronBlockEntity cauldron, FluidStack fluidStack, Player player, InteractionHand hand, ItemStack stack) {
        var tag = BuiltInRegistries.FLUID.getOrCreateTag(CauldronTweaks.DYEABLE_FLUID);
        return CauldronCondition.test(List.of(new FluidCondition(tag), DyeItemCondition.INSTANCE), cauldron, player, hand, stack);
    }

    @Override
    public CauldronItemResult getItemResult(ItemStack usedItem, FluidStack usedFluid, Player player) {
        return new DestroyItemResult(2, 1);
    }

    @Override
    public CauldronFluidResult getFluidResult(ItemStack usedItem, FluidStack usedFluid) {
        FluidStack copy = usedFluid.copy();
        if (usedItem.getItem() instanceof DyeItem dyeItem) {
            copy = CauldronHelper.applyDyes(copy, List.of(dyeItem));
        }
        DataComponentPatch patch = copy.getComponentsPatch();
        patch.forget((c) -> c != DataComponents.DYED_COLOR);
        return new AddComponentsFluidResult(patch);
    }
}
