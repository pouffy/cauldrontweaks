package io.github.pouffy.cauldrontweaks.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CauldronBlock.class)
public class CauldronBlockMixin {

    @Inject(method = "canReceiveStalactiteDrip", at = @At("HEAD"), cancellable = true)
    public void canReceiveStalactiteDrip(Fluid p_153551_, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

    @WrapMethod(method = "receiveStalactiteDrip")
    public void receiveStalactiteDrip(BlockState p_152940_, Level p_152941_, BlockPos p_152942_, Fluid p_152943_, Operation<Void> original) {
    }
}
