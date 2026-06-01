package io.github.pouffy.cauldrontweaks.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.stream.Stream;

public class CauldronFluidIngredient {
    public static final Codec<CauldronFluidIngredient> FLAT_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            FluidIngredient.MAP_CODEC_NONEMPTY.forGetter(CauldronFluidIngredient::ingredient),
            NeoForgeExtraCodecs.optionalFieldAlwaysWrite(ExtraCodecs.POSITIVE_INT, "amount", 1000).forGetter(CauldronFluidIngredient::amount),
            DataComponentPredicate.CODEC.optionalFieldOf("components", DataComponentPredicate.EMPTY).forGetter(CauldronFluidIngredient::components)
    ).apply(instance, CauldronFluidIngredient::new));
    public static final Codec<CauldronFluidIngredient> NESTED_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            FluidIngredient.CODEC_NON_EMPTY.fieldOf("ingredient").forGetter(CauldronFluidIngredient::ingredient),
            NeoForgeExtraCodecs.optionalFieldAlwaysWrite(ExtraCodecs.POSITIVE_INT, "amount", 1000).forGetter(CauldronFluidIngredient::amount),
            DataComponentPredicate.CODEC.optionalFieldOf("components", DataComponentPredicate.EMPTY).forGetter(CauldronFluidIngredient::components)
    ).apply(instance, CauldronFluidIngredient::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, CauldronFluidIngredient> STREAM_CODEC = StreamCodec.composite(
            FluidIngredient.STREAM_CODEC, CauldronFluidIngredient::ingredient,
            ByteBufCodecs.VAR_INT, CauldronFluidIngredient::amount,
            DataComponentPredicate.STREAM_CODEC, CauldronFluidIngredient::components,
            CauldronFluidIngredient::new);

    private final FluidIngredient ingredient;
    private final int amount;
    private final DataComponentPredicate components;
    private @Nullable FluidStack[] cachedStacks;

    public static CauldronFluidIngredient of(Fluid fluid, int amount, DataComponentPredicate components) {
        return new CauldronFluidIngredient(FluidIngredient.of(fluid), amount, components);
    }

    public static CauldronFluidIngredient of(Fluid fluid, int amount) {
        return of(fluid, amount, DataComponentPredicate.EMPTY);
    }

    public static CauldronFluidIngredient of(FluidStack stack, DataComponentPredicate components) {
        return new CauldronFluidIngredient(FluidIngredient.single(stack), stack.getAmount(), components);
    }

    public static CauldronFluidIngredient of(FluidStack stack) {
        return of(stack, DataComponentPredicate.EMPTY);
    }

    public static CauldronFluidIngredient of(TagKey<Fluid> tag, int amount, DataComponentPredicate components) {
        return new CauldronFluidIngredient(FluidIngredient.tag(tag), amount, components);
    }

    public static CauldronFluidIngredient of(TagKey<Fluid> tag, int amount) {
        return of(tag, amount, DataComponentPredicate.EMPTY);
    }

    public CauldronFluidIngredient(FluidIngredient ingredient, int amount, DataComponentPredicate components) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Size must be positive");
        } else {
            this.ingredient = ingredient;
            this.amount = amount;
            this.components = components;
        }
    }

    public FluidIngredient ingredient() {
        return this.ingredient;
    }

    public int amount() {
        return this.amount;
    }

    public DataComponentPredicate components() {
        return this.components;
    }

    public boolean test(FluidStack stack) {
        return this.ingredient.test(stack) && stack.getAmount() >= this.amount && this.components.test(stack.getComponents());
    }

    public FluidStack[] getFluids() {
        if (this.cachedStacks == null) {
            this.cachedStacks = Stream.of(this.ingredient.getStacks()).map((s) -> s.copyWithAmount(this.amount)).peek((stack) -> stack.applyComponents(this.components.asPatch())).toArray(FluidStack[]::new);
        }

        return this.cachedStacks;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof CauldronFluidIngredient other)) {
            return false;
        } else {
            return this.amount == other.amount && this.ingredient.equals(other.ingredient) && this.components == other.components;
        }
    }

    public int hashCode() {
        return Objects.hash(this.ingredient, this.amount, this.components);
    }

    public String toString() {
        int amount = this.amount;
        String data = "{"+this.components.toString()+"}";
        return amount + "x " + this.ingredient + data;
    }
}
