package io.github.pouffy.cauldrontweaks.common.data.interaction.types;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import io.github.pouffy.cauldrontweaks.common.data.CauldronFluidIngredient;
import io.github.pouffy.cauldrontweaks.common.data.interaction.CauldronInteractionType;
import io.github.pouffy.cauldrontweaks.common.data.interaction.ICauldronInteraction;
import io.github.pouffy.cauldrontweaks.common.fluid.DyedFluidColor;
import io.github.pouffy.cauldrontweaks.helpers.FluidHelper;
import io.github.pouffy.cauldrontweaks.init.CauldronDataComponents;
import io.github.pouffy.cauldrontweaks.init.CauldronInteractions;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record DyeItemInteraction(Ingredient input, CauldronFluidIngredient fluid, Optional<ItemStack> result) implements ICauldronInteraction {

    public static final MapCodec<DyeItemInteraction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.fieldOf("input").forGetter(DyeItemInteraction::input),
            CauldronFluidIngredient.FLAT_CODEC.fieldOf("fluid").forGetter(DyeItemInteraction::fluid),
            ItemStack.CODEC.optionalFieldOf("result").forGetter(DyeItemInteraction::result)
    ).apply(instance, DyeItemInteraction::new));

    @Override
    public CauldronInteractionType<?> getType() {
        return CauldronInteractions.DYE_ITEM.get();
    }

    @Override
    public void runExtra(CauldronBlockEntity cauldron, FluidStack fluidStack, Player player, InteractionHand hand, ItemStack stack) {
        player.level().playSound(player, cauldron.getBlockPos(), FluidHelper.getEmptySound(fluidStack), SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    @Override
    public boolean testExtra(CauldronBlockEntity cauldron, FluidStack fluidStack, Player player, InteractionHand hand, ItemStack stack) {
        return fluidStack.has(CauldronDataComponents.DYED_COLOR);
    }

    @Override
    public ItemStack getItemResult(ItemStack usedItem, FluidStack usedFluid, Player player) {
        DyedFluidColor color = usedFluid.get(CauldronDataComponents.DYED_COLOR);
        if (result.isPresent()) {
            return result.get();
        }
        if (color != null) {
            usedItem.set(DataComponents.DYED_COLOR, new DyedItemColor(color.rgb(), false));
            return usedItem;
        }
        return this.result().orElse(ItemStack.EMPTY);
    }

    @Override
    public FluidStack getFluidResult(ItemStack usedItem, FluidStack usedFluid) {
        return usedFluid;
    }

    @Override
    public @Nullable Ingredient getItemInput() {
        return this.input();
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
