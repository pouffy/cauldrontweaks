package io.github.pouffy.cauldrontweaks.common.data.condition.type;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import io.github.pouffy.cauldrontweaks.common.data.condition.CauldronCondition;
import io.github.pouffy.cauldrontweaks.common.data.condition.CauldronConditionType;
import io.github.pouffy.cauldrontweaks.init.CauldronConditions;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;

public record FluidCondition(HolderSet<Fluid> set) implements CauldronCondition {

    public static final MapCodec<FluidCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            RegistryCodecs.homogeneousList(Registries.FLUID).fieldOf("set").forGetter(FluidCondition::set)
    ).apply(instance, FluidCondition::new));

    @Override
    public CauldronConditionType<?> getType() {
        return CauldronConditions.FLUID.get();
    }

    @Override
    public boolean isValid(CauldronBlockEntity cauldron, Player player, InteractionHand hand, ItemStack stack) {
        return cauldron.getFluidStack().is(this.set);
    }
}
