package io.github.pouffy.cauldrontweaks.helpers;

import com.mojang.datafixers.util.Pair;
import io.github.pouffy.cauldrontweaks.CauldronTweaks;
import io.github.pouffy.cauldrontweaks.common.fluid.PotionFluid;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

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
        fluid.set(CauldronTweaks.POTION_FLUID_BOTTLE_TYPE, bottleTypeFromItem);
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
        ItemStack potionStack = new ItemStack(itemFromBottleType(availableFluid.getOrDefault(CauldronTweaks.POTION_FLUID_BOTTLE_TYPE, BottleType.REGULAR)));
        potionStack.set(DataComponents.POTION_CONTENTS, availableFluid.get(DataComponents.POTION_CONTENTS));
        return potionStack;
    }

    public static void generatePotionParticles(Level level, BlockPos pos, int color, boolean generateMultiple) {
        ParticleStatus particleStatus = Minecraft.getInstance().options.particles().get();
        if (particleStatus == ParticleStatus.MINIMAL)
            return;

        BlockState stateOfBlockAbove = level.getBlockState(pos.above());
        if (stateOfBlockAbove.canOcclude())
            return;

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

        float red = (color >> 16 & 255) / 255.0f;
        float green = (color >> 8 & 255) / 255.0f;
        float blue = (color & 255) / 255.0f;

        for (int i = 1; i <= numberOfParticles; i++) {
            Particle particle = Minecraft.getInstance().particleEngine.createParticle(
                    ParticleTypes.EFFECT,
                    pos.getX() + 0.45 + random.nextDouble() * 0.2,
                    pos.getY() + 1.0,
                    pos.getZ() + 0.45 + random.nextDouble() * 0.2,
                    0.7,
                    1.3,
                    0.7
            );

            assert particle != null;
            particle.setColor(red, green, blue);
        }
    }
}
