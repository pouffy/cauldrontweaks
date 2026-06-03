package io.github.pouffy.cauldrontweaks.common.data.condition.type;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import io.github.pouffy.cauldrontweaks.common.data.condition.CauldronCondition;
import io.github.pouffy.cauldrontweaks.common.data.condition.CauldronConditionType;
import io.github.pouffy.cauldrontweaks.init.CauldronConditions;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.FastColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.neoforged.neoforge.fluids.FluidStack;

public record FluidDyeCondition(DyeColor color) implements CauldronCondition {

    public static final MapCodec<FluidDyeCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DyeColor.CODEC.fieldOf("color").forGetter(FluidDyeCondition::color)
    ).apply(instance, FluidDyeCondition::new));

    @Override
    public CauldronConditionType<?> getType() {
        return CauldronConditions.FLUID_DYE.get();
    }

    @Override
    public boolean isValid(CauldronBlockEntity cauldron, Player player, InteractionHand hand, ItemStack stack) {
        FluidStack contained = cauldron.getFluidStack();
        if (contained.has(DataComponents.DYED_COLOR)) {
            DyedItemColor color = contained.get(DataComponents.DYED_COLOR);
            return color != null && this.color.getTextureDiffuseColor() == FastColor.ARGB32.opaque(color.rgb());
        }
        return false;
    }
}
