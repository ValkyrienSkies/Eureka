package org.valkyrienskies.eureka.fabric.mixin.client.main;

import net.minecraft.client.main.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.eureka.fabric.AutoDependenciesFabric;

@Mixin(Main.class)
public class MixinMain {

    @Inject(
        at = @At("HEAD"),
        method = "main"
    )
    private static void beforeMain(String[] args, CallbackInfo ci) {
        AutoDependenciesFabric.checkDependencies();
    }

}
