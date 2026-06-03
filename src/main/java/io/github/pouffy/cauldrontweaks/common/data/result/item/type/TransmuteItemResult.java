package io.github.pouffy.cauldrontweaks.common.data.result.item.type;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.pouffy.cauldrontweaks.common.data.result.item.CauldronItemResult;
import io.github.pouffy.cauldrontweaks.common.data.result.item.CauldronItemResultType;
import io.github.pouffy.cauldrontweaks.init.CauldronItemResults;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public record TransmuteItemResult(ItemStack newStack, List<Holder<DataComponentType<?>>> forget) implements CauldronItemResult {

    public static final MapCodec<TransmuteItemResult> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemStack.SINGLE_ITEM_CODEC.fieldOf("item").forGetter(TransmuteItemResult::newStack),
            BuiltInRegistries.DATA_COMPONENT_TYPE.holderByNameCodec().listOf().optionalFieldOf("forget", List.of()).forGetter(TransmuteItemResult::forget)
    ).apply(instance, TransmuteItemResult::new));

    @Override
    public CauldronItemResultType<?> getType() {
        return CauldronItemResults.TRANSMUTE.get();
    }

    @Override
    public void alterPlayer(Player player, InteractionHand hand, ItemStack usedItem, FluidStack usedFluid) {
        ItemStack result = getItemResult(usedItem, usedFluid);
        if (!player.isCreative()) {
            usedItem.shrink(1);
            if (result.isEmpty()) return;
            player.getInventory().placeItemBackInInventory(result);
        }
    }

    @Override
    public ItemStack getItemResult(ItemStack usedItem, FluidStack usedFluid) {
        DataComponentPatch oldComponents = usedItem.getComponentsPatch();
        oldComponents.forget(literalBlacklist()::contains);
        ItemStack copy = new ItemStack(newStack.getItem(), 1);
        copy.applyComponents(oldComponents);
        copy.applyComponents(newStack.getComponentsPatch());
        return copy;
    }

    private List<? extends DataComponentType<?>> literalBlacklist() {
        return this.forget.stream().map(Holder::value).toList();
    }
}
