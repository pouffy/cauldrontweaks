package io.github.pouffy.cauldrontweaks.common.data.result.item.type;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.pouffy.cauldrontweaks.CauldronTweaks;
import io.github.pouffy.cauldrontweaks.common.data.result.fluid.type.RemoveComponentsFluidResult;
import io.github.pouffy.cauldrontweaks.common.data.result.item.CauldronItemResult;
import io.github.pouffy.cauldrontweaks.common.data.result.item.CauldronItemResultType;
import io.github.pouffy.cauldrontweaks.init.CauldronItemResults;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public record RemoveComponentsItemResult(List<Holder<DataComponentType<?>>> components) implements CauldronItemResult {

    public static final MapCodec<RemoveComponentsItemResult> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BuiltInRegistries.DATA_COMPONENT_TYPE.holderByNameCodec().listOf().fieldOf("components").forGetter(RemoveComponentsItemResult::components)
    ).apply(instance, RemoveComponentsItemResult::new));

    @Override
    public CauldronItemResultType<?> getType() {
        return CauldronItemResults.REMOVE_COMPONENTS.get();
    }

    @Override
    public void alterPlayer(Player player, InteractionHand hand, ItemStack usedItem, FluidStack usedFluid) {
        List<? extends DataComponentType<?>> toRemove = this.components.stream().map(Holder::value).toList();
        toRemove.forEach(usedItem::remove);
        CauldronTweaks.LOGGER.info("Removed {} components from {}", toRemove.size(), usedItem);
    }

    @Override
    public ItemStack getItemResult(ItemStack usedItem, FluidStack usedFluid) {
        ItemStack copy = usedItem.copy();
        this.components.stream().map(Holder::value).forEach(copy::remove);
        return copy;
    }
}
