package io.github.pouffy.cauldrontweaks.common.data.result.item;

import com.mojang.serialization.Codec;
import io.github.pouffy.cauldrontweaks.init.CauldronRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public interface CauldronItemResult {

    Codec<CauldronItemResult> CODEC = Codec.lazyInitialized(CauldronRegistries.CAULDRON_ITEM_RESULT_TYPE_REGISTRY::byNameCodec).dispatch("type", CauldronItemResult::getType, CauldronItemResultType::codec);

    CauldronItemResultType<?> getType();

    void alterPlayer(Player player, InteractionHand hand, ItemStack usedItem, FluidStack usedFluid);

    ItemStack getItemResult(ItemStack usedItem, FluidStack usedFluid);
}
