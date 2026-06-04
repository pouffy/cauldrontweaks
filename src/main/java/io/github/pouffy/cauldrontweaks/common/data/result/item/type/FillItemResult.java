package io.github.pouffy.cauldrontweaks.common.data.result.item.type;

import io.github.pouffy.cauldrontweaks.common.data.result.item.CauldronItemResult;
import io.github.pouffy.cauldrontweaks.common.data.result.item.CauldronItemResultType;
import io.github.pouffy.cauldrontweaks.helpers.FluidContainerHelper;
import io.github.pouffy.cauldrontweaks.helpers.FluidHelper;
import io.github.pouffy.cauldrontweaks.helpers.PotionFluidHelper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

import static io.github.pouffy.cauldrontweaks.helpers.FluidContainerHelper.canFillGlassBottleInternally;

public enum FillItemResult implements CauldronItemResult {
    INSTANCE;

    @Override
    public CauldronItemResultType<?> getType() {
        return null;
    }

    @Override
    public void alterPlayer(Player player, InteractionHand hand, ItemStack usedItem, FluidStack usedFluid) {
        ItemStack result = getItemResult(usedItem.copy(), usedFluid.copy());
        if (!result.isEmpty()) {
            usedItem.shrink(1);
            player.getInventory().placeItemBackInInventory(result);
        }
    }

    @Override
    public ItemStack getItemResult(ItemStack usedItem, FluidStack usedFluid) {
        int requiredAmount = FluidContainerHelper.getRequiredAmountForItem(usedItem, usedFluid.copy());
        FluidStack toFill = usedFluid.copy();
        toFill.setAmount(requiredAmount);
        toFill.remove(DataComponents.DYED_COLOR);

        if (usedItem.getItem() == Items.GLASS_BOTTLE && canFillGlassBottleInternally(toFill)) {
            ItemStack fillBottle;
            Fluid fluid = toFill.getFluid();
            if (FluidHelper.isWater(fluid))
                fillBottle = PotionContents.createItemStack(Items.POTION, Potions.WATER);
            else
                fillBottle = PotionFluidHelper.fillBottle(usedItem, toFill);
            return fillBottle;
        }
        ItemStack split = usedItem.copy();
        split.setCount(1);
        IFluidHandlerItem capability = split.getCapability(Capabilities.FluidHandler.ITEM);
        if (capability == null)
            return ItemStack.EMPTY;
        capability.fill(toFill, IFluidHandler.FluidAction.EXECUTE);
        return capability.getContainer().copy();
    }
}
