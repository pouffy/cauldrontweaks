package io.github.pouffy.cauldrontweaks.common.data.result.item.type;

import com.mojang.serialization.MapCodec;
import io.github.pouffy.cauldrontweaks.CauldronTweaks;
import io.github.pouffy.cauldrontweaks.common.data.result.item.CauldronItemResult;
import io.github.pouffy.cauldrontweaks.common.data.result.item.CauldronItemResultType;
import io.github.pouffy.cauldrontweaks.init.CauldronItemResults;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public enum NoOpItemResult implements CauldronItemResult {
    INSTANCE;

    public static final MapCodec<NoOpItemResult> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public CauldronItemResultType<?> getType() {
        return CauldronItemResults.NO_OP.get();
    }

    @Override
    public void alterPlayer(Player player, InteractionHand hand, ItemStack usedItem, FluidStack usedFluid) {
        CauldronTweaks.LOGGER.info("No-Op");
    }

    @Override
    public ItemStack getItemResult(ItemStack usedItem, FluidStack usedFluid) {
        return usedItem;
    }
}
