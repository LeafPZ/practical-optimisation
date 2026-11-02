package com.example.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import zombie.core.Core;
import zombie.iso.IsoPuddles;

import static com.example.ExampleMod.LOGGER;

@Mixin(Core.class)
public class ExampleMixin {
    @Inject(method = "getVersion", at = @At("RETURN"), cancellable = true)
    private void getVersion(CallbackInfoReturnable<String> cir) {
        LOGGER.println("Changing the internal game version.");
        cir.setReturnValue("43.123.69");
    }

    @ModifyExpressionValue(
            method = "initShaders",
            at = @At(
                    value = "INVOKE",
                    target = "Lzombie/iso/IsoPuddles;getInstance()Lzombie/iso/IsoPuddles;"
            )
    )
    private IsoPuddles initShaders(IsoPuddles instance) {
        LOGGER.warn("IsoPuddles::initShaders instance effect name: " + instance.effect.getName());
        return instance;
    }
}
