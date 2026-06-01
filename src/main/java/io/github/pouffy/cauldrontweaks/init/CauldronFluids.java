package io.github.pouffy.cauldrontweaks.init;

import io.github.pouffy.cauldrontweaks.CauldronTweaks;
import io.github.pouffy.cauldrontweaks.common.fluid.PotionFluid;
import io.github.pouffy.cauldrontweaks.common.fluid.VirtualFluid;
import io.github.pouffy.cauldrontweaks.helpers.ModUtils;
import net.createmod.catnip.data.Couple;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Function;
import java.util.function.Supplier;

public class CauldronFluids {
    public static final DeferredRegister<Fluid> HELPER = ModUtils.createRegister(Registries.FLUID);

    private static final Couple<DeferredHolder<Fluid, VirtualFluid>> POWDER_SNOW_FLUID = create("powder_snow", Types.POWDER_SNOW, VirtualFluid::createSource, VirtualFluid::createFlowing);
    public static final DeferredHolder<Fluid, VirtualFluid> POWDER_SNOW = POWDER_SNOW_FLUID.getFirst();
    public static final DeferredHolder<Fluid, VirtualFluid> FLOWING_POWDER_SNOW = POWDER_SNOW_FLUID.getSecond();
    private static final Couple<DeferredHolder<Fluid, PotionFluid>> POTION_FLUID = create("potion", Types.POTION, PotionFluid::createSource, PotionFluid::createFlowing);
    public static final DeferredHolder<Fluid, PotionFluid> POTION = POTION_FLUID.getFirst();
    public static final DeferredHolder<Fluid, PotionFluid> FLOWING_POTION = POTION_FLUID.getSecond();

    public static <T extends VirtualFluid, R extends FluidType> Couple<DeferredHolder<Fluid, T>> create(String name, DeferredHolder<FluidType, R> type, Function<BaseFlowingFluid.Properties, T> stillConstructor, Function<BaseFlowingFluid.Properties, T> flowingConstructor) {
        DeferredHolder<Fluid, T> still = DeferredHolder.create(Registries.FLUID, CauldronTweaks.getResource(name));
        DeferredHolder<Fluid, T> flowing = DeferredHolder.create(Registries.FLUID, CauldronTweaks.getResource("flowing_"+name));
        LazySupplier<Fluid> crf = new LazySupplier<>();
        crf.setVal(HELPER.register(name, () -> stillConstructor.apply(new BaseFlowingFluid.Properties(type, crf, crf))));
        crf.setVal(HELPER.register("flowing_"+name, () -> flowingConstructor.apply(new BaseFlowingFluid.Properties(type, crf, crf))));
        return Couple.create(still, flowing);
    }

    public static void staticInit() {
        CauldronTweaks.LOGGER.info("[Cauldron Tweaks] Fluid Registry");
        Types.staticInit();
    }

    public static class LazySupplier<T> implements Supplier<T>{
        Supplier<T> val;
        @Override
        public T get() {
            if(val==null)return null;
            return val.get();
        }
        public void setVal(Supplier<T> val) {
            this.val = val;
        }
    }

    @EventBusSubscriber(Dist.CLIENT)
    public static class ClientOnly {
        @SubscribeEvent
        public static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
            event.registerFluidType(new IClientFluidTypeExtensions() {
                private static final ResourceLocation POWDER_SNOW = ResourceLocation.withDefaultNamespace("block/powder_snow");

                public ResourceLocation getStillTexture() {
                    return POWDER_SNOW;
                }

                public ResourceLocation getFlowingTexture() {
                    return POWDER_SNOW;
                }
            }, Types.POWDER_SNOW.value());
        }
    }

    public static class Types {
        public static final DeferredRegister<FluidType> TYPE_HELPER = ModUtils.createRegister(NeoForgeRegistries.Keys.FLUID_TYPES);

        public static final DeferredHolder<FluidType, FluidType> POWDER_SNOW = create("powder_snow", () -> new FluidType(FluidType.Properties.create().sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_POWDER_SNOW).sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_POWDER_SNOW)));
        public static final DeferredHolder<FluidType, PotionFluid.PotionFluidType> POTION = create("potion", () -> new PotionFluid.PotionFluidType(FluidType.Properties.create().sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL).sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)));

        public static <T extends FluidType> DeferredHolder<FluidType, T> create(String name, Supplier<T> constructor) {
            return TYPE_HELPER.register(name, constructor);
        }

        public static void staticInit() {
            CauldronTweaks.LOGGER.info("[Cauldron Tweaks] Fluid Type Registry");
        }
    }
}
