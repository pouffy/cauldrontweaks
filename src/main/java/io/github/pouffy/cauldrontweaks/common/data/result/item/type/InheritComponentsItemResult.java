package io.github.pouffy.cauldrontweaks.common.data.result.item.type;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.pouffy.cauldrontweaks.common.data.result.item.CauldronItemResult;
import io.github.pouffy.cauldrontweaks.common.data.result.item.CauldronItemResultType;
import io.github.pouffy.cauldrontweaks.init.CauldronItemResults;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public record InheritComponentsItemResult(List<Holder<DataComponentType<?>>> blacklist, List<Holder<DataComponentType<?>>> whitelist) implements CauldronItemResult {

    public static final MapCodec<InheritComponentsItemResult> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BuiltInRegistries.DATA_COMPONENT_TYPE.holderByNameCodec().listOf().optionalFieldOf("blacklist", List.of()).forGetter(InheritComponentsItemResult::blacklist),
            BuiltInRegistries.DATA_COMPONENT_TYPE.holderByNameCodec().listOf().optionalFieldOf("whitelist", List.of()).forGetter(InheritComponentsItemResult::blacklist)
    ).apply(instance, InheritComponentsItemResult::new));

    @Override
    public CauldronItemResultType<?> getType() {
        return CauldronItemResults.INHERIT_COMPONENTS.get();
    }

    @Override
    public void alterPlayer(Player player, InteractionHand hand, ItemStack usedItem, FluidStack usedFluid) {

    }

    @Override
    public ItemStack getItemResult(ItemStack usedItem, FluidStack usedFluid) {
        DataComponentPatch fluidComponents = getComponentsPatch(usedFluid);
        ItemStack copy = usedItem.copy();
        copy.applyComponents(fluidComponents);
        return copy;
    }

    private List<? extends DataComponentType<?>> literalBlacklist() {
        return this.blacklist.stream().map(Holder::value).toList();
    }

    private List<? extends DataComponentType<?>> literalWhitelist() {
        return this.whitelist.stream().map(Holder::value).toList();
    }

    private DataComponentPatch getComponentsPatch(FluidStack old) {
        DataComponentPatch fluidComponents = old.getComponentsPatch();
        if (!this.whitelist().isEmpty()) {
            fluidComponents.forget((c) -> !literalWhitelist().contains(c));
            return fluidComponents;
        }
        if (!this.blacklist().isEmpty()) {
            fluidComponents.forget(literalBlacklist()::contains);
            return fluidComponents;
        }
        return fluidComponents;
    }
}
