package io.github.pouffy.cauldrontweaks.common.block;

import io.github.pouffy.cauldrontweaks.CauldronTweaks;
import io.github.pouffy.cauldrontweaks.common.event.CauldronTickEvent;
import io.github.pouffy.cauldrontweaks.helpers.CauldronHelper;
import io.github.pouffy.cauldrontweaks.helpers.FluidHelper;
import io.github.pouffy.cauldrontweaks.helpers.LerpedFloat;
import io.github.pouffy.cauldrontweaks.helpers.blockentity.BlockEntityBehaviour;
import io.github.pouffy.cauldrontweaks.helpers.blockentity.SmartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.List;

public class CauldronBlockEntity extends SmartBlockEntity {

    protected IFluidHandler fluidCapability;
    protected final CauldronTank tank;

    protected boolean forceFluidLevelUpdate;
    protected boolean updateCapability;

    private static final int SYNC_RATE = 8;
    protected int syncCooldown;
    protected boolean queuedSync;

    // For rendering purposes only
    private LerpedFloat fluidLevel;

    public CauldronBlockEntity(BlockPos pos, BlockState blockState) {
        super(CauldronTweaks.CAULDRON.get(), pos, blockState);
        tank = new CauldronTank(this::onFluidStackChanged, this);
        forceFluidLevelUpdate = true;

        updateCapability = false;
        refreshCapability();
    }

    public int getCapacity() {
        return 1000;
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, CauldronTweaks.CAULDRON.get(), (be, context) -> {
            if (be.tank == null) be.refreshCapability();
            return be.tank;
        });
    }

    public void tick() {
        if (!shouldTick()) return;
        super.tick();
        if (syncCooldown > 0) {
            syncCooldown--;
            if (syncCooldown == 0 && queuedSync)
                sendData();
        }
        if (updateCapability) {
            updateCapability = false;
            refreshCapability();
        }
        if (fluidLevel != null) fluidLevel.tickChaser();
        if (level != null) {
            if (!getBlockState().is(Blocks.CAULDRON)) {
                FluidStack foundFluid = CauldronHelper.getFluidForCauldron(this);
                if (foundFluid != FluidStack.EMPTY) {
                    level.setBlockAndUpdate(getBlockPos(), Blocks.CAULDRON.defaultBlockState());
                    level.setBlockEntity(this);
                    tank.fill(foundFluid, IFluidHandler.FluidAction.EXECUTE);
                    sendDataImmediately();
                }
            }
        }
        if (level instanceof ServerLevel serverLevel) {
            BlockPos stalactitePos = PointedDripstoneBlock.findStalactiteTipAboveCauldron(serverLevel, getBlockPos());
            if (stalactitePos != null) {
                Fluid fluid = PointedDripstoneBlock.getCauldronFillFluidType(serverLevel, stalactitePos);
                if (fluid != Fluids.EMPTY) {
                    this.receiveStalactiteDrip(serverLevel, worldPosition, fluid);
                }
            }
        }
        if (level != null) {
            if (level.isClientSide) {
                NeoForge.EVENT_BUS.post(new CauldronTickEvent.Client(this));
            } else {
                NeoForge.EVENT_BUS.post(new CauldronTickEvent.Server(this));
            }
        }
    }

    private boolean shouldTick() {
        FluidStack foundFluid = CauldronHelper.getFluidForCauldron(this);
        return getBlockState().is(Blocks.CAULDRON) || !foundFluid.isEmpty();
    }

    public void refreshCapability() {
        fluidCapability = handlerForCapability();
        invalidateCapabilities();
    }

    private IFluidHandler handlerForCapability() {
        return tank;
    }

    protected void onFluidStackChanged(FluidStack newFluids) {
        if (level == null) return;
        if (tank != null) {
            tank.setCapacity(getCapacity());
            if (tank.getSpace() < 0) tank.drain(-tank.getSpace(), IFluidHandler.FluidAction.EXECUTE);
        }

        level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());

        if (!level.isClientSide) {
            setChanged();
            sendData();
        } else {
            if (fluidLevel == null) fluidLevel = LerpedFloat.linear().startWithValue(getFillState());
            fluidLevel.chase(getFillState(), 0.5f, LerpedFloat.Chaser.EXP);
        }
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        assert level != null;

        this.tank.readFromNBT(registries, tag.getCompound("CauldronContent"));
        if (tank.getSpace() < 0) tank.drain(-tank.getSpace(), IFluidHandler.FluidAction.EXECUTE);

        if (tag.contains("ForceFluidLevel") || fluidLevel == null) fluidLevel = LerpedFloat.linear().startWithValue(getFillState());

        if (tag.contains("ForceFluidLevel") || fluidLevel == null) fluidLevel = LerpedFloat.linear().startWithValue(getFillState());

        updateCapability = true;

        if (!clientPacket) return;

        float fillState = getFillState();

        if (tag.contains("ForceFluidLevel") || fluidLevel == null) fluidLevel = LerpedFloat.linear().startWithValue(fillState);
        fluidLevel.chase(fillState, 0.5f, LerpedFloat.Chaser.EXP);


        if (tag.contains("LazySync")) fluidLevel.chase(fluidLevel.getChaseTarget(), 0.125f, LerpedFloat.Chaser.EXP);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (tank != null) tank.setCapacity(getCapacity());
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        tag.put("CauldronContent", tank.writeToNBT(registries, new CompoundTag()));
        super.write(tag, registries, clientPacket);

        if (!clientPacket) return;

        if (forceFluidLevelUpdate) tag.putBoolean("ForceFluidLevel", true);
        if (queuedSync) tag.putBoolean("LazySync", true);
        forceFluidLevelUpdate = false;
    }

    public float getFillState() {
        return (float) getTank().getFluidAmount() / getTank().getCapacity();
    }

    public void sendDataImmediately() {
        syncCooldown = 0;
        queuedSync = false;
        sendData();
    }

    public void sendData() {
        if (syncCooldown > 0) {
            queuedSync = true;
            return;
        }
        queuedSync = false;
        syncCooldown = SYNC_RATE;
    }

    public double getContentHeight() {
        return (6.0F + getFillState() / 16.0F);
    }

    public boolean isEntityInsideContent(BlockPos pos, Entity entity) {
        return entity.getY() < pos.getY() + this.getContentHeight() && entity.getBoundingBox().maxY > pos.getY() + 0.25F;
    }

    public boolean canAccept(FluidStack newFluid) {
        if (!getFluidStack().isEmpty()) {
            return FluidHelper.isSame(newFluid, getFluidStack()) && getFluidStack().getAmount() + newFluid.getAmount() <= getTank().getCapacity();
        }
        return true;
    }

    protected void receiveStalactiteDrip(Level level, BlockPos pos, Fluid fluid) {
        if (fluid.getFluidType().handleCauldronDrip(fluid, level, pos)) return;
        if (!this.getTank().isFull()) {
            this.getTank().fill(new FluidStack(fluid, 1), IFluidHandler.FluidAction.EXECUTE);
            this.setChanged();
            this.sendData();
        }
    }

    public CauldronTank getTank() {
        return this.tank;
    }

    public LerpedFloat getFluidLevel() {
        return fluidLevel;
    }

    public FluidStack getFluidStack() {
        var inv = getTank();
        return inv == null ? FluidStack.EMPTY : inv.getFluid();
    }

    public int getLuminosity() {
        return getTank().getLuminosity();
    }
}
