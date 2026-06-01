package io.github.pouffy.cauldrontweaks;

import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import io.github.pouffy.cauldrontweaks.common.event.CauldronTickEvent;
import io.github.pouffy.cauldrontweaks.helpers.PotionFluidHelper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.DyedItemColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.fluids.FluidStack;

@EventBusSubscriber(modid = CauldronTweaks.MODID, value = Dist.CLIENT)
public class CauldronTweaksClient {

    public static void init() {

    }

    @SubscribeEvent
    public static void cauldronTick(CauldronTickEvent.Client event) {
        CauldronBlockEntity cauldron = event.getCauldron();
        FluidStack fluidStack = cauldron.getFluidStack();
        if (cauldron.getLevel().getGameTime() % 20L == 0L && !fluidStack.isEmpty()) {
            if (fluidStack.has(DataComponents.POTION_CONTENTS)) {
                PotionContents contents = fluidStack.get(DataComponents.POTION_CONTENTS);
                PotionFluidHelper.generatePotionParticles(cauldron.getLevel(), cauldron.getBlockPos(), contents.getColor(), false);
            }
            if (fluidStack.has(DataComponents.DYED_COLOR)) {
                DyedItemColor color = fluidStack.get(DataComponents.DYED_COLOR);
            }
        }
    }
}
