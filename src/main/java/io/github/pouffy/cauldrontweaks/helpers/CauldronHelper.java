package io.github.pouffy.cauldrontweaks.helpers;

import io.github.pouffy.cauldrontweaks.CauldronTweaks;
import io.github.pouffy.cauldrontweaks.common.event.CauldronFluidEvent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.FastColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

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
            if (newStack.isEmpty()) return true;
            player.getInventory().placeItemBackInInventory(newStack);
        }
        return true;
    }

    public static FluidStack applyDyes(FluidStack fluidStack, List<DyeItem> dyes) {
        if (!fluidStack.is(CauldronTweaks.DYEABLE_FLUID)) {
            return FluidStack.EMPTY;
        } else {
            FluidStack copied = fluidStack.copyWithAmount(1);
            int i = 0;
            int j = 0;
            int k = 0;
            int l = 0;
            int i1 = 0;
            DyedItemColor fluidColor = copied.get(DataComponents.DYED_COLOR);
            if (fluidColor != null) {
                int j1 = FastColor.ARGB32.red(fluidColor.rgb());
                int k1 = FastColor.ARGB32.green(fluidColor.rgb());
                int l1 = FastColor.ARGB32.blue(fluidColor.rgb());
                l += Math.max(j1, Math.max(k1, l1));
                i += j1;
                j += k1;
                k += l1;
                i1++;
            }

            for (DyeItem dyeitem : dyes) {
                int j3 = dyeitem.getDyeColor().getTextureDiffuseColor();
                int i2 = FastColor.ARGB32.red(j3);
                int j2 = FastColor.ARGB32.green(j3);
                int k2 = FastColor.ARGB32.blue(j3);
                l += Math.max(i2, Math.max(j2, k2));
                i += i2;
                j += j2;
                k += k2;
                i1++;
            }

            int l2 = i / i1;
            int i3 = j / i1;
            int k3 = k / i1;
            float f = (float)l / (float)i1;
            float f1 = (float)Math.max(l2, Math.max(i3, k3));
            l2 = (int)((float)l2 * f / f1);
            i3 = (int)((float)i3 * f / f1);
            k3 = (int)((float)k3 * f / f1);
            int l3 = FastColor.ARGB32.color(0, l2, i3, k3);
            copied.set(DataComponents.DYED_COLOR, new DyedItemColor(l3, true));
            return copied.copyWithAmount(fluidStack.getAmount());
        }
    }
}
