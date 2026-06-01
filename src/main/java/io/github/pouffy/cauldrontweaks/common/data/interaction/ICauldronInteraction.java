package io.github.pouffy.cauldrontweaks.common.data.interaction;

import com.mojang.serialization.Codec;
import io.github.pouffy.cauldrontweaks.CauldronTweaks;
import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import io.github.pouffy.cauldrontweaks.init.CauldronRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public interface ICauldronInteraction {

    Codec<ICauldronInteraction> CODEC = Codec.lazyInitialized(CauldronRegistries.CAULDRON_INTERACTION_TYPE_REGISTRY::byNameCodec).dispatch("type", ICauldronInteraction::getType, CauldronInteractionType::codec);

    CauldronInteractionType<?> getType();

    ItemInteractionResult interact(CauldronBlockEntity cauldron, FluidStack fluidStack, Player player, InteractionHand hand, ItemStack stack);
}
