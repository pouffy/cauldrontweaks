package io.github.pouffy.cauldrontweaks.helpers;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper;

public class FluidContainerHelper {

    public static boolean isFluidHandlerValid(ItemStack stack, IFluidHandlerItem fluidHandler) {
        if (fluidHandler.getClass() == FluidBucketWrapper.class) {
            Item item = stack.getItem();
            if (item.getClass() != BucketItem.class && !(item instanceof MilkBucketItem)) {
                return false;
            }
        }
        return true;
    }

    public static boolean canItemBeFilled(ItemStack stack) {
        if (stack.getItem() == Items.GLASS_BOTTLE)
            return true;
        if (stack.getItem() == Items.MILK_BUCKET)
            return false;

        IFluidHandlerItem capability = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (capability == null)
            return false;
        if (!isFluidHandlerValid(stack, capability))
            return false;
        for (int i = 0; i < capability.getTanks(); i++) {
            if (capability.getFluidInTank(i)
                    .getAmount() < capability.getTankCapacity(i))
                return true;
        }
        return false;
    }

    public static boolean canItemBeEmptied(ItemStack stack) {
        if (PotionFluidHelper.isPotionItem(stack))
            return true;

        IFluidHandlerItem capability = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (capability == null)
            return false;
        for (int i = 0; i < capability.getTanks(); i++) {
            if (capability.getFluidInTank(i)
                    .getAmount() > 0)
                return true;
        }
        return false;
    }

    public static int getRequiredAmountForItem(ItemStack stack, FluidStack availableFluid) {
        if (stack.getItem() == Items.GLASS_BOTTLE && canFillGlassBottleInternally(availableFluid))
            return PotionFluidHelper.getRequiredAmountForFilledBottle(stack, availableFluid);
        if (stack.getItem() == Items.BUCKET && canFillBucketInternally(availableFluid))
            return 1000;

        IFluidHandlerItem capability = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (capability == null)
            return -1;
        if (capability instanceof FluidBucketWrapper) {
            Item filledBucket = availableFluid.getFluid().getBucket();
            if (filledBucket == Items.AIR)
                return -1;
            if (!((FluidBucketWrapper) capability).getFluid().isEmpty())
                return -1;
            return 1000;
        }

        int filled = capability.fill(availableFluid, IFluidHandler.FluidAction.SIMULATE);
        return filled == 0 ? -1 : filled;
    }

    private static boolean canFillGlassBottleInternally(FluidStack availableFluid) {
        Fluid fluid = availableFluid.getFluid();
        if (fluid.isSame(Fluids.WATER))
            return true;
        //if (fluid.isSame(AllFluids.POTION.get()))
        //    return true;
        return false;
    }

    private static boolean canFillBucketInternally(FluidStack availableFluid) {
        return false;
    }

    public static ItemStack fillItem(int requiredAmount, ItemStack stack, FluidStack availableFluid) {
        FluidStack toFill = availableFluid.copy();
        toFill.setAmount(requiredAmount);
        toFill.remove(DataComponents.DYED_COLOR);
        availableFluid.shrink(requiredAmount);

        if (stack.getItem() == Items.GLASS_BOTTLE && canFillGlassBottleInternally(toFill)) {
            ItemStack fillBottle = ItemStack.EMPTY;
            Fluid fluid = toFill.getFluid();
            if (FluidHelper.isWater(fluid))
                fillBottle = PotionContents.createItemStack(Items.POTION, Potions.WATER);
            else
                fillBottle = PotionFluidHelper.fillBottle(stack, toFill);
            stack.shrink(1);
            return fillBottle;
        }

        ItemStack split = stack.copy();
        split.setCount(1);
        IFluidHandlerItem capability = split.getCapability(Capabilities.FluidHandler.ITEM);
        if (capability == null)
            return ItemStack.EMPTY;
        capability.fill(toFill, IFluidHandler.FluidAction.EXECUTE);
        ItemStack container = capability.getContainer()
                .copy();
        stack.shrink(1);
        return container;
    }

    public static Pair<FluidStack, ItemStack> emptyItem(ItemStack stack, boolean simulate) {
        FluidStack resultingFluid = FluidStack.EMPTY;
        ItemStack resultingItem = ItemStack.EMPTY;

        if (PotionFluidHelper.isPotionItem(stack))
            return PotionFluidHelper.emptyPotion(stack, simulate);

        ItemStack split = stack.copy();
        split.setCount(1);
        IFluidHandlerItem capability = split.getCapability(Capabilities.FluidHandler.ITEM);
        if (capability == null)
            return Pair.of(resultingFluid, resultingItem);
        resultingFluid = capability.drain(1000, simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE);
        resultingItem = capability.getContainer()
                .copy();
        if (!simulate)
            stack.shrink(1);

        return Pair.of(resultingFluid, resultingItem);
    }
}
