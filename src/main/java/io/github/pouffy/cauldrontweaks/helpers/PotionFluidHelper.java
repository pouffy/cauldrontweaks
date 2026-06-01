package io.github.pouffy.cauldrontweaks.helpers;

import com.mojang.datafixers.util.Pair;
import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import io.github.pouffy.cauldrontweaks.common.fluid.PotionFluid;
import io.github.pouffy.cauldrontweaks.init.CauldronDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

import static io.github.pouffy.cauldrontweaks.common.fluid.PotionFluid.BottleType;


public class PotionFluidHelper {
    private static final Component NO_EFFECT = Component.translatable("effect.none").withStyle(ChatFormatting.GRAY);

    public static boolean isPotionItem(ItemStack stack) {
        return stack.getItem() instanceof PotionItem && !(stack.getCraftingRemainingItem().getItem() instanceof BucketItem);
    }

    public static Pair<FluidStack, ItemStack> emptyPotion(ItemStack stack, boolean simulate) {
        FluidStack fluid = getFluidFromPotionItem(stack);
        if (!simulate)
            stack.shrink(1);
        return Pair.of(fluid, new ItemStack(Items.GLASS_BOTTLE));
    }

    public static FluidStack getFluidFromPotionItem(ItemStack stack) {
        PotionContents potion = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        BottleType bottleTypeFromItem = bottleTypeFromItem(stack.getItem());
        if (potion.is(Potions.WATER) && potion.customEffects().isEmpty() && bottleTypeFromItem == BottleType.REGULAR)
            return new FluidStack(Fluids.WATER, 250);
        FluidStack fluid = getFluidFromPotion(potion, bottleTypeFromItem, 250);
        fluid.set(CauldronDataComponents.POTION_FLUID_BOTTLE_TYPE, bottleTypeFromItem);
        return fluid;
    }

    public static FluidStack getFluidFromPotion(PotionContents potionContents, BottleType bottleType, int amount) {
        if (potionContents.is(Potions.WATER) && bottleType == BottleType.REGULAR)
            return new FluidStack(Fluids.WATER, amount);
        return PotionFluid.of(amount, potionContents, bottleType);
    }

    public static int getRequiredAmountForFilledBottle(ItemStack stack, FluidStack availableFluid) {
        return 250;
    }

    public static BottleType bottleTypeFromItem(Item item) {
        if (item == Items.LINGERING_POTION)
            return BottleType.LINGERING;
        if (item == Items.SPLASH_POTION)
            return BottleType.SPLASH;
        return BottleType.REGULAR;
    }

    public static ItemLike itemFromBottleType(BottleType type) {
        return switch (type) {
            case LINGERING -> Items.LINGERING_POTION;
            case SPLASH -> Items.SPLASH_POTION;
            default -> Items.POTION;
        };
    }

    public static ItemStack fillBottle(ItemStack stack, FluidStack availableFluid) {
        ItemStack potionStack = new ItemStack(itemFromBottleType(availableFluid.getOrDefault(CauldronDataComponents.POTION_FLUID_BOTTLE_TYPE, BottleType.REGULAR)));
        potionStack.set(DataComponents.POTION_CONTENTS, availableFluid.get(DataComponents.POTION_CONTENTS));
        return potionStack;
    }

    public static void generatePotionParticles(CauldronBlockEntity cauldron, BlockPos pos, PotionContents contents, boolean generateMultiple) {
        ParticleStatus particleStatus = Minecraft.getInstance().options.particles().get();
        if (particleStatus == ParticleStatus.MINIMAL)
            return;

        BlockState stateOfBlockAbove = cauldron.getLevel().getBlockState(pos.above());
        if (stateOfBlockAbove.canOcclude())
            return;

        var fluidLevel = cauldron.getFluidLevel();
        if (fluidLevel == null) return;
        float capHeight = 1 / 16f;
        float minPuddleHeight = 4 / 16f;
        float totalHeight = 1 - 2 * capHeight - minPuddleHeight;
        float level = fluidLevel.getValue(Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false)) + 0.0225f / 16f;
        if (level < 1 / (512f * totalHeight)) return;
        float clampedLevel = Mth.clamp(level * totalHeight, 0, totalHeight);

        RandomSource random = RandomSource.create();
        int multiplier, numberOfParticles = 1;

        if (generateMultiple) {
            multiplier = particleStatus == ParticleStatus.DECREASED ? 1 : 2;
            numberOfParticles = random.nextInt(3 * multiplier, 5 * multiplier);
        }
        else {
            if (particleStatus == ParticleStatus.DECREASED && random.nextInt(10) % 5 != 0)
                return;
            else if (random.nextInt(10) % 3 != 0)
                return;
        }

        List<MobEffectInstance> effects = new ArrayList<>();
        contents.getAllEffects().forEach(effects::add);
        for (int i = 1; i <= numberOfParticles; i++) {
            ParticleOptions options = effects.get(cauldron.getLevel().random.nextInt(effects.size())).getParticleOptions();
            Minecraft.getInstance().particleEngine.createParticle(
                    options,
                    pos.getX() + 0.45 + random.nextDouble() * 0.2,
                    pos.getY() + clampedLevel,
                    pos.getZ() + 0.45 + random.nextDouble() * 0.2,
                    0.7,
                    1.3,
                    0.7
            );
        }
    }
}
