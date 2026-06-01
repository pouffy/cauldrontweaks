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

public record FillContainerInteraction(Ingredient empty, ItemStack filled, CauldronFluidIngredient fluid) implements ICauldronInteraction {

    public static final MapCodec<FillContainerInteraction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.fieldOf("empty").forGetter(FillContainerInteraction::empty),
            ItemStack.CODEC.fieldOf("filled").forGetter(FillContainerInteraction::filled),
            CauldronFluidIngredient.FLAT_CODEC.fieldOf("fluid").forGetter(FillContainerInteraction::fluid)
    ).apply(instance, FillContainerInteraction::new));

    @Override
    public CauldronInteractionType<?> getType() {
        return CauldronInteractions.FILL_CONTAINER.get();
    }

    @Override
    public void runExtra(CauldronBlockEntity cauldron, FluidStack fluidStack, Player player, InteractionHand hand, ItemStack stack) {
        player.level().playSound(player, cauldron.getBlockPos(), FluidHelper.getFillSound(fluidStack), SoundSource.BLOCKS, 1.0F, 1.0F);
        cauldron.notifyUpdate();
    }

    @Override
    public boolean testExtra(CauldronBlockEntity cauldron, FluidStack fluidStack, Player player, InteractionHand hand, ItemStack stack) {
        int requiredAmountForItem = fluid.amount();
        return !(fluidStack.isEmpty() || requiredAmountForItem == -1 || requiredAmountForItem > fluidStack.getAmount());
    }

    @Override
    public ItemStack getItemResult(ItemStack usedItem, FluidStack usedFluid, Player player) {
        return this.filled();
    }

    @Override
    public FluidStack getFluidResult(ItemStack usedItem, FluidStack usedFluid) {
        return usedFluid;
    }

    @Override
    public @Nullable Ingredient getItemInput() {
        return this.empty();
    }

    @Override
    public @Nullable CauldronFluidIngredient getFluidInput() {
        return this.fluid();
    }

    @Override
    public int fluidAmountChange() {
        return -this.fluid().amount();
    }
}
