package io.github.pouffy.cauldrontweaks.common.data.interaction.types;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.pouffy.cauldrontweaks.CauldronTweaks;
import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import io.github.pouffy.cauldrontweaks.common.data.condition.CauldronCondition;
import io.github.pouffy.cauldrontweaks.common.data.condition.type.FluidComponentsCondition;
import io.github.pouffy.cauldrontweaks.common.data.interaction.CauldronInteractionType;
import io.github.pouffy.cauldrontweaks.common.data.interaction.ICauldronInteraction;
import io.github.pouffy.cauldrontweaks.common.data.result.fluid.CauldronFluidResult;
import io.github.pouffy.cauldrontweaks.common.data.result.item.CauldronItemResult;
import io.github.pouffy.cauldrontweaks.common.data.result.item.type.InheritComponentsItemResult;
import io.github.pouffy.cauldrontweaks.helpers.FluidHelper;
import io.github.pouffy.cauldrontweaks.init.CauldronInteractions;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record DyeItemInteraction(List<CauldronCondition> conditions, CauldronFluidResult fluidResult, Optional<ItemStack> result) implements ICauldronInteraction {

    public static final MapCodec<DyeItemInteraction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CauldronCondition.CODEC.listOf().fieldOf("conditions").forGetter(DyeItemInteraction::conditions),
            CauldronFluidResult.CODEC.fieldOf("fluid").forGetter(DyeItemInteraction::fluidResult),
            ItemStack.CODEC.optionalFieldOf("result").forGetter(DyeItemInteraction::result)
    ).apply(instance, DyeItemInteraction::new));

    @Override
    public CauldronInteractionType<?> getType() {
        return CauldronInteractions.DYE_ITEM.get();
    }

    @Override
    public boolean test(CauldronBlockEntity cauldron, FluidStack fluidStack, Player player, InteractionHand hand, ItemStack stack) {
        List<CauldronCondition> conditions = this.conditions == null ? new ArrayList<>() : new ArrayList<>(this.conditions);
        conditions.add(new FluidComponentsCondition(DataComponentPredicate.EMPTY, List.of(Holder.direct(DataComponents.DYED_COLOR))));
        return CauldronCondition.test(conditions, cauldron, player, hand, stack);
    }

    @Override
    public void run(CauldronBlockEntity cauldron, FluidStack fluidStack, Player player, InteractionHand hand, ItemStack stack) {
        player.level().playSound(player, cauldron.getBlockPos(), FluidHelper.getEmptySound(fluidStack), SoundSource.BLOCKS, 1.0F, 1.0F);
        CauldronTweaks.LOGGER.info("Played item dyeing sound");
    }

    @Override
    public CauldronItemResult getItemResult(ItemStack usedItem, FluidStack usedFluid, Player player) {
        return new InheritComponentsItemResult(List.of(), List.of(Holder.direct(DataComponents.DYED_COLOR)));
    }

    @Override
    public CauldronFluidResult getFluidResult(ItemStack usedItem, FluidStack usedFluid) {
        return this.fluidResult;
    }
}
