package io.github.pouffy.cauldrontweaks.common.event;

import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import io.github.pouffy.cauldrontweaks.common.data.condition.CauldronCondition;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class CauldronContainersEvent extends Event {

    private final CauldronBlockEntity cauldron;

    protected CauldronContainersEvent(CauldronBlockEntity cauldron) {
        this.cauldron = cauldron;
    }

    public CauldronBlockEntity getCauldron() {
        return this.cauldron;
    }

    static class Filling extends CauldronContainersEvent {
        private final ItemStack empty;
        private ItemStack filled = ItemStack.EMPTY;
        private final List<CauldronCondition> conditions = new ArrayList<>();

        Filling(CauldronBlockEntity cauldron, ItemStack empty) {
            super(cauldron);
            this.empty = empty;
        }

        public ItemStack getEmpty() {
            return this.empty;
        }

        public ItemStack getFilled() {
            return this.filled;
        }

        public List<CauldronCondition> getConditions() {
            return this.conditions;
        }

        public void setFilled(ItemStack filled) {
            this.filled = filled;
        }

        public void withCondition(CauldronCondition... condition) {
            this.conditions.addAll(Arrays.asList(condition));
        }
    }
}
