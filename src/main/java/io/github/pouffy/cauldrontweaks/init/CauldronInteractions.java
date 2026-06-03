package io.github.pouffy.cauldrontweaks.init;

import com.mojang.serialization.MapCodec;
import io.github.pouffy.cauldrontweaks.CauldronTweaks;
import io.github.pouffy.cauldrontweaks.common.data.interaction.CauldronInteractionType;
import io.github.pouffy.cauldrontweaks.common.data.interaction.ICauldronInteraction;
import io.github.pouffy.cauldrontweaks.common.data.interaction.types.*;
import io.github.pouffy.cauldrontweaks.common.data.interaction.types.builtin.BucketEmptyInteraction;
import io.github.pouffy.cauldrontweaks.common.data.interaction.types.builtin.BucketFillInteraction;
import io.github.pouffy.cauldrontweaks.common.data.interaction.types.builtin.DrinkPotionInteraction;
import io.github.pouffy.cauldrontweaks.common.data.interaction.types.builtin.DyeFluidInteraction;
import io.github.pouffy.cauldrontweaks.helpers.ModUtils;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CauldronInteractions {
    public static final DeferredRegister<CauldronInteractionType<?>> HELPER = ModUtils.createRegister(CauldronRegistries.CAULDRON_INTERACTION_TYPE);

    public static final DeferredHolder<CauldronInteractionType<?>, CauldronInteractionType<GeneralInteraction>> GENERAL = create("general", GeneralInteraction.CODEC);

    public static final DeferredHolder<CauldronInteractionType<?>, CauldronInteractionType<BucketEmptyInteraction>> EMPTY_BUCKET = create("empty_bucket", BucketEmptyInteraction.CODEC);
    public static final DeferredHolder<CauldronInteractionType<?>, CauldronInteractionType<BucketFillInteraction>> FILL_BUCKET = create("fill_bucket", BucketFillInteraction.CODEC);
    public static final DeferredHolder<CauldronInteractionType<?>, CauldronInteractionType<DyeFluidInteraction>> DYE_FLUID = create("dye_fluid", DyeFluidInteraction.CODEC);
    public static final DeferredHolder<CauldronInteractionType<?>, CauldronInteractionType<DyeItemInteraction>> DYE_ITEM = create("dye_item", DyeItemInteraction.CODEC);
    public static final DeferredHolder<CauldronInteractionType<?>, CauldronInteractionType<DyeClearingInteraction>> CLEAR_DYE = create("clear_dye", DyeClearingInteraction.CODEC);
    public static final DeferredHolder<CauldronInteractionType<?>, CauldronInteractionType<DrinkContentsInteraction>> DRINK_CONTENTS = create("drink_contents", DrinkContentsInteraction.CODEC);
    public static final DeferredHolder<CauldronInteractionType<?>, CauldronInteractionType<DrinkPotionInteraction>> DRINK_POTION = create("drink_potion", DrinkPotionInteraction.CODEC);
    
    public static <T extends ICauldronInteraction> DeferredHolder<CauldronInteractionType<?>, CauldronInteractionType<T>> create(String name, MapCodec<T> codec) {
        return HELPER.register(name, () -> new CauldronInteractionType<>(codec));
    }

    public static void staticInit() {
        CauldronTweaks.LOGGER.info("[Cauldron Tweaks] Cauldron Interaction Type Registry");
    }
}
