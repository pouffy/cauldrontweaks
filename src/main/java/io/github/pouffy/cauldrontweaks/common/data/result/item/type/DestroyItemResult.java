package io.github.pouffy.cauldrontweaks.common.data.result.item.type;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.pouffy.cauldrontweaks.common.data.result.item.CauldronItemResult;
import io.github.pouffy.cauldrontweaks.common.data.result.item.CauldronItemResultType;
import io.github.pouffy.cauldrontweaks.init.CauldronItemResults;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public record DestroyItemResult(int damageAmount, float chance) implements CauldronItemResult {

    public static final MapCodec<DestroyItemResult> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("damage", 0).forGetter(DestroyItemResult::damageAmount),
            ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("chance", 1.0f).forGetter(DestroyItemResult::chance)
    ).apply(instance, DestroyItemResult::new));

    @Override
    public CauldronItemResultType<?> getType() {
        return CauldronItemResults.DESTROY.get();
    }

    @Override
    public void alterPlayer(Player player, InteractionHand hand, ItemStack usedItem, FluidStack usedFluid) {
        if (!player.isCreative() && player.level().random.nextFloat() < chance) {
            if (usedItem.has(DataComponents.MAX_DAMAGE) && damageAmount > 0) {
                usedItem.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
            } else {
                usedItem.shrink(1);
            }
        }
    }

    @Override
    public ItemStack getItemResult(ItemStack usedItem, FluidStack usedFluid) {
        return usedItem;
    }
}
