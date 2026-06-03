package io.github.pouffy.cauldrontweaks.init;

import com.mojang.serialization.MapCodec;
import io.github.pouffy.cauldrontweaks.CauldronTweaks;
import io.github.pouffy.cauldrontweaks.common.data.condition.CauldronCondition;
import io.github.pouffy.cauldrontweaks.common.data.condition.CauldronConditionType;
import io.github.pouffy.cauldrontweaks.common.data.condition.type.*;
import io.github.pouffy.cauldrontweaks.common.data.condition.type.builtin.CanBeEmptiedCondition;
import io.github.pouffy.cauldrontweaks.common.data.condition.type.builtin.CanBeFilledCondition;
import io.github.pouffy.cauldrontweaks.common.data.condition.type.builtin.DyeItemCondition;
import io.github.pouffy.cauldrontweaks.helpers.ModUtils;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CauldronConditions {
    public static final DeferredRegister<CauldronConditionType<?>> HELPER = ModUtils.createRegister(CauldronRegistries.CAULDRON_CONDITION_TYPE);

    //Fluid
    public static final DeferredHolder<CauldronConditionType<?>, CauldronConditionType<FluidAmountCondition>> FLUID_AMOUNT = create("fluid_amount", FluidAmountCondition.CODEC);
    public static final DeferredHolder<CauldronConditionType<?>, CauldronConditionType<FluidCondition>> FLUID = create("fluid", FluidCondition.CODEC);
    public static final DeferredHolder<CauldronConditionType<?>, CauldronConditionType<FluidComponentsCondition>> FLUID_COMPONENTS = create("fluid_components", FluidComponentsCondition.CODEC);
    public static final DeferredHolder<CauldronConditionType<?>, CauldronConditionType<RejectFluidComponentsCondition>> REJECT_FLUID_COMPONENTS = create("reject_fluid_components", RejectFluidComponentsCondition.CODEC);
    public static final DeferredHolder<CauldronConditionType<?>, CauldronConditionType<FluidDyeCondition>> FLUID_DYE = create("fluid_dye", FluidDyeCondition.CODEC);
    //Item
    public static final DeferredHolder<CauldronConditionType<?>, CauldronConditionType<ItemCondition>> ITEM = create("item", ItemCondition.CODEC);
    public static final DeferredHolder<CauldronConditionType<?>, CauldronConditionType<ItemComponentsCondition>> ITEM_COMPONENTS = create("item_components", ItemComponentsCondition.CODEC);
    public static final DeferredHolder<CauldronConditionType<?>, CauldronConditionType<EmptyHandCondition>> EMPTY_HAND = create("empty_hand", EmptyHandCondition.CODEC);
    public static final DeferredHolder<CauldronConditionType<?>, CauldronConditionType<DyeItemCondition>> DYE_ITEM = create("dye_item", DyeItemCondition.CODEC);
    public static final DeferredHolder<CauldronConditionType<?>, CauldronConditionType<CanBeEmptiedCondition>> CAN_BE_EMPTIED = create("can_be_emptied", CanBeEmptiedCondition.CODEC);
    public static final DeferredHolder<CauldronConditionType<?>, CauldronConditionType<CanBeFilledCondition>> CAN_BE_FILLED = create("can_be_filled", CanBeFilledCondition.CODEC);

    public static <T extends CauldronCondition> DeferredHolder<CauldronConditionType<?>, CauldronConditionType<T>> create(String name, MapCodec<T> codec) {
        return HELPER.register(name, () -> new CauldronConditionType<>(codec));
    }

    public static void staticInit() {
        CauldronTweaks.LOGGER.info("[Cauldron Tweaks] Cauldron Condition Type Registry");
    }
}
