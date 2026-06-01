package io.github.pouffy.cauldrontweaks.common.events;

import io.github.pouffy.cauldrontweaks.CauldronTweaks;
import io.github.pouffy.cauldrontweaks.common.block.CauldronTank;
import io.github.pouffy.cauldrontweaks.common.event.CauldronFluidEvent;
import io.github.pouffy.cauldrontweaks.common.event.InsideCauldronEvent;
import io.github.pouffy.cauldrontweaks.helpers.CauldronHelper;
import io.github.pouffy.cauldrontweaks.helpers.blockentity.SmartFluidTank;
import io.github.pouffy.cauldrontweaks.init.CauldronFluids;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidStack;

@EventBusSubscriber
public class CauldronEvents {

    @SubscribeEvent
    public static void cauldronFluids(CauldronFluidEvent event) {
        BlockState state = event.getState();
        if (state.is(Blocks.WATER_CAULDRON)) {
            event.setFluid(new FluidStack(Fluids.WATER, CauldronHelper.cauldronLevelToAmount(state.getValue(LayeredCauldronBlock.LEVEL))));
        }
        if (state.is(Blocks.LAVA_CAULDRON)) {
            event.setFluid(new FluidStack(Fluids.LAVA, 1000));
        }
        if (state.is(Blocks.POWDER_SNOW_CAULDRON)) {
            event.setFluid(new FluidStack(CauldronFluids.POWDER_SNOW, 1000));
        }
    }

    @SubscribeEvent
    public static void insideCauldron(InsideCauldronEvent event) {
        Entity entity = event.getEntity();
        Level level = entity.level();
        SmartFluidTank tank = event.getTank();
        if (event.getFluid().is(Tags.Fluids.LAVA)) {
            entity.lavaHurt();
        }

        if (event.getFluid().is(CauldronFluids.POWDER_SNOW)) {
            if (level.isClientSide) {
                RandomSource randomsource = level.getRandom();
                boolean flag = entity.xOld != entity.getX() || entity.zOld != entity.getZ();
                if (flag && randomsource.nextBoolean()) {
                    level.addParticle(ParticleTypes.SNOWFLAKE, entity.getX(), entity.getY() + 1, entity.getZ(), Mth.randomBetween(randomsource, -1.0F, 1.0F) * 0.083333336F, 0.05F, Mth.randomBetween(randomsource, -1.0F, 1.0F) * 0.083333336F);
                }
            }
            entity.makeStuckInBlock(event.getBlockState(), new Vec3(0.9F, 1.5, 0.9F));
            entity.setIsInPowderSnow(true);
            if (!level.isClientSide) {
                if (entity.isOnFire()) {
                    tank.setFluid(FluidStack.EMPTY);
                }
                entity.setSharedFlagOnFire(false);
            }
        }
    }
}
