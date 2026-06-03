package io.github.pouffy.cauldrontweaks.common.data.interaction.types;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import io.github.pouffy.cauldrontweaks.common.data.condition.CauldronCondition;
import io.github.pouffy.cauldrontweaks.common.data.interaction.CauldronInteractionType;
import io.github.pouffy.cauldrontweaks.common.data.interaction.ICauldronInteraction;
import io.github.pouffy.cauldrontweaks.common.data.result.fluid.CauldronFluidResult;
import io.github.pouffy.cauldrontweaks.common.data.result.item.CauldronItemResult;
import io.github.pouffy.cauldrontweaks.init.CauldronInteractions;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public record GeneralInteraction(List<CauldronCondition> conditions, CauldronItemResult itemResult, CauldronFluidResult fluidResult) implements ICauldronInteraction {

    public static final MapCodec<GeneralInteraction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CauldronCondition.CODEC.listOf().fieldOf("conditions").forGetter(GeneralInteraction::conditions),
            CauldronItemResult.CODEC.fieldOf("result").forGetter(GeneralInteraction::itemResult),
            CauldronFluidResult.CODEC.fieldOf("fluid_result").forGetter(GeneralInteraction::fluidResult)
    ).apply(instance, GeneralInteraction::new));

    @Override
    public CauldronInteractionType<?> getType() {
        return CauldronInteractions.GENERAL.get();
    }

    @Override
    public boolean test(CauldronBlockEntity cauldron, FluidStack fluidStack, Player player, InteractionHand hand, ItemStack stack) {
        return CauldronCondition.test(this.conditions, cauldron, player, hand, stack);
    }

    @Override
    public void run(CauldronBlockEntity cauldron, FluidStack fluidStack, Player player, InteractionHand hand, ItemStack stack) {

    }

    @Override
    public CauldronItemResult getItemResult(ItemStack usedItem, FluidStack usedFluid, Player player) {
        return this.itemResult;
    }

    @Override
    public CauldronFluidResult getFluidResult(ItemStack usedItem, FluidStack usedFluid) {
        return this.fluidResult;
    }
}
