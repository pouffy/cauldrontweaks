package io.github.pouffy.cauldrontweaks.helpers;

import io.github.pouffy.cauldrontweaks.common.event.CauldronFluidEvent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.fluids.FluidStack;

public class CauldronHelper {


    public static FluidStack getFluidForCauldron(BlockEntity entity) {
        var event = NeoForge.EVENT_BUS.post(new CauldronFluidEvent(entity.getLevel(), entity.getBlockPos(), entity.getBlockState()));
        return event.getFluid();
    }

    public static int cauldronLevelToAmount(int level) {
        return switch (level) {
            case 1 -> 250;
            case 2 -> 500;
            case 3 -> 1000;
            default -> 0;
        };
    }

    public static boolean handleItemConsume(Player player, InteractionHand hand, ItemStack stack, ItemStack newStack, boolean damage) {
        if (player.getItemInHand(hand) != stack) return false;
        if (!player.isCreative()) {
            if (stack.has(DataComponents.MAX_DAMAGE) && damage) {
                stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
            } else {
                stack.shrink(1);
            }
        }
        if (newStack.isEmpty()) return true;
        player.getInventory().placeItemBackInInventory(newStack);
        return true;
    }
}
