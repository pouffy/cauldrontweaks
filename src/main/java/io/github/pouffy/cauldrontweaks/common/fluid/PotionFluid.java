package io.github.pouffy.cauldrontweaks.common.fluid;

import com.mojang.serialization.Codec;
import io.github.pouffy.cauldrontweaks.CauldronTweaks;
import io.github.pouffy.cauldrontweaks.helpers.PotionFluidHelper;
import io.github.pouffy.cauldrontweaks.init.CauldronDataComponents;
import io.github.pouffy.cauldrontweaks.init.CauldronFluids;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.lang.Lang;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class PotionFluid extends VirtualFluid {

    public static PotionFluid createSource(Properties properties) {
        return new PotionFluid(properties, true);
    }

    public static PotionFluid createFlowing(Properties properties) {
        return new PotionFluid(properties, false);
    }

    public PotionFluid(Properties properties, boolean source) {
        super(properties, source);
    }

    public static FluidStack of(int amount, PotionContents potionContents, BottleType bottleType) {
        FluidStack fluidStack;
        fluidStack = new FluidStack(CauldronFluids.POTION.get().getSource(), amount);
        addPotionToFluidStack(fluidStack, potionContents);
        fluidStack.set(CauldronDataComponents.POTION_FLUID_BOTTLE_TYPE, bottleType);
        return fluidStack;
    }

    public static FluidStack addPotionToFluidStack(FluidStack fs, PotionContents potionContents) {
        if (potionContents == PotionContents.EMPTY) {
            fs.remove(DataComponents.POTION_CONTENTS);
            return fs;
        }
        fs.set(DataComponents.POTION_CONTENTS, potionContents);
        return fs;
    }

    public enum BottleType implements StringRepresentable {
        REGULAR, SPLASH, LINGERING;

        public static final Codec<BottleType> CODEC = StringRepresentable.fromEnum(BottleType::values);
        public static final StreamCodec<ByteBuf, BottleType> STREAM_CODEC = CatnipStreamCodecBuilders.ofEnum(BottleType.class);

        @Override
        public @NotNull String getSerializedName() {
            return Lang.asId(name());
        }
    }

    public static class PotionFluidType extends TintedFluidType {
        public PotionFluidType(net.neoforged.neoforge.fluids.FluidType.Properties properties) {
            super(properties, CauldronTweaks.getResource("minecraft:block/water_still"), CauldronTweaks.getResource("minecraft:block/water_flow"));
        }

        @Override
        public int getTintColor(FluidStack stack) {
            return stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).getColor() | 0xff000000;
        }

        @Override
        public String getDescriptionId(FluidStack stack) {
            PotionContents contents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
            ItemLike itemFromBottleType = PotionFluidHelper.itemFromBottleType(stack.getOrDefault(CauldronDataComponents.POTION_FLUID_BOTTLE_TYPE, BottleType.REGULAR));
            return Potion.getName(contents.potion(), itemFromBottleType.asItem().getDescriptionId() + ".effect.");
        }

        @Override
        protected int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
            return NO_TINT;
        }
    }
}
