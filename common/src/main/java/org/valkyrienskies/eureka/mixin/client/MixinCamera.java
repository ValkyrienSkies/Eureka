package org.valkyrienskies.eureka.mixin.client;

import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.eureka.EurekaEntities;
import org.valkyrienskies.vs2api.SeatEntity;

@Mixin(Camera.class)
public abstract class MixinCamera {

    @Shadow
    protected abstract void setPosition(Vec3 pos);

    @Inject(method = "setup", at = @At("TAIL"))
    public void fixPos(final BlockGetter level,
                       final Entity renderViewEntity,
                       final boolean thirdPerson,
                       final boolean thirdPersonReverse,
                       final float partialTicks,
                       final CallbackInfo ci) {

        final Entity vehicle = renderViewEntity.getVehicle();
        if (vehicle != null && vehicle.getType().equals(EurekaEntities.INSTANCE.getSEAT().get())) {
            final Vec3 r = ((SeatEntity) vehicle).calcWorldPos();
            if (r != null) {
                setPosition(r);
            }
        }
    }

}
