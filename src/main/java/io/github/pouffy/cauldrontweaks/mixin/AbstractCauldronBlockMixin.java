package io.github.pouffy.cauldrontweaks.mixin;

import io.github.pouffy.cauldrontweaks.CauldronTweaks;
import io.github.pouffy.cauldrontweaks.common.block.CauldronBlockEntity;
import io.github.pouffy.cauldrontweaks.common.event.InsideCauldronEvent;
import io.github.pouffy.cauldrontweaks.helpers.blockentity.ISmartBlockEntity;
import io.github.pouffy.cauldrontweaks.helpers.blockentity.SmartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractCauldronBlock.class)
public abstract class AbstractCauldronBlockMixin extends Block implements ISmartBlockEntity<CauldronBlockEntity> {

    public AbstractCauldronBlockMixin(Properties properties) {
        super(properties);
    }

    public Class<CauldronBlockEntity> getBlockEntityClass() {
        return CauldronBlockEntity.class;
    }

    public BlockEntityType<CauldronBlockEntity> getBlockEntityType() {
        return CauldronTweaks.CAULDRON.get();
    }

    @Inject(method = "onRemove", at = @At("HEAD"))
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston, CallbackInfo ci) {
        if (!state.hasBlockEntity()) return;
        if (state.is(newState.getBlock()) && newState.hasBlockEntity()) return;
        if (level.getBlockEntity(pos) instanceof SmartBlockEntity be) be.destroy();
        level.removeBlockEntity(pos);
    }

    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    public void useItemOn(ItemStack p_316791_, BlockState p_316287_, Level p_316530_, BlockPos p_316585_, Player p_316671_, InteractionHand p_316186_, BlockHitResult p_316294_, CallbackInfoReturnable<ItemInteractionResult> cir) {
        cir.setReturnValue(ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION);
    }

    @Inject(method = "canReceiveStalactiteDrip", at = @At("HEAD"), cancellable = true)
    public void canReceiveStalactiteDrip(Fluid p_153551_, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

    @Override
    public boolean hasDynamicLightEmission(BlockState state) {
        return true;
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        var at = level.getBlockEntity(pos);
        if (at == null || !at.hasLevel() || !(at instanceof CauldronBlockEntity cauldron)) return 0;
        return cauldron.getLuminosity();
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        var at = level.getBlockEntity(pos);
        if (at == null || !at.hasLevel() || !(at instanceof CauldronBlockEntity cauldron)) return;
        if (cauldron.isEntityInsideContent(pos, entity)) {
            NeoForge.EVENT_BUS.post(new InsideCauldronEvent(entity, cauldron));
        }
    }
}
