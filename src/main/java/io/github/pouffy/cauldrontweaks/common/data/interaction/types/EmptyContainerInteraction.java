package io.github.pouffy.cauldrontweaks.common.data.interaction.types;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import io.github.pouffy.cauldrontweaks.common.data.CauldronFluidIngredient;
import io.github.pouffy.cauldrontweaks.common.data.interaction.CauldronInteractionType;
import io.github.pouffy.cauldrontweaks.common.data.interaction.ICauldronInteraction;
import io.github.pouffy.cauldrontweaks.helpers.FluidHelper;
import io.github.pouffy.cauldrontweaks.init.CauldronInteractions;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public record EmptyContainerInteraction(Ingredient filled, ItemStack empty, FluidStack fluid) implements ICauldronInteraction {

    public static final MapCodec<EmptyContainerInteraction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.fieldOf("filled").forGetter(EmptyContainerInteraction::filled),
            ItemStack.CODEC.fieldOf("empty").forGetter(EmptyContainerInteraction::empty),
            FluidStack.CODEC.fieldOf("fluid").forGetter(EmptyContainerInteraction::fluid)
    ).apply(instance, EmptyContainerInteraction::new));

    @Override
    public CauldronInteractionType<?> getType() {
        return CauldronInteractions.EMPTY_CONTAINER.get();
    }

    @Override
    public void runExtra(CauldronBlockEntity cauldron, FluidStack fluidStack, Player player, InteractionHand hand, ItemStack stack) {
        player.level().playSound(player, cauldron.getBlockPos(), FluidHelper.getEmptySound(fluid), SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    @Override
    public boolean testExtra(CauldronBlockEntity cauldron, FluidStack fluidStack, Player player, InteractionHand hand, ItemStack stack) {
        return cauldron.canAccept(getFluidResult(stack, fluidStack));
    }

    @Override
    public ItemStack getItemResult(ItemStack usedItem, FluidStack usedFluid, Player player) {
        return this.empty();
    }

    @Override
    public FluidStack getFluidResult(ItemStack usedItem, FluidStack usedFluid) {
        return this.fluid();
    }

    @Override
    public @Nullable Ingredient getItemInput() {
        return this.filled();
    }

    @Override
    public @Nullable CauldronFluidIngredient getFluidInput() {
        return null;
    }

    @Override
    public int fluidAmountChange() {
        return fluid().getAmount();
    }
}
