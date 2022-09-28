package org.valkyrienskies.eureka.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.world.entity.LivingEntity;
import org.valkyrienskies.mod.common.entity.ShipMountingEntity;

@Mixin(PlayerModel.class)
public abstract class PlayerEntityModelMixin<T extends LivingEntity> extends HumanoidModel<T> {

    protected PlayerEntityModelMixin(float scale) {
        super(scale);
    }

    @SuppressWarnings("unchecked")
    @Inject(method = "setupAnim", at = @At(value = "HEAD"))
    public void setupAnim(T livingEntity, float swing, float g, float tick, float i, float j, CallbackInfo info) {
        if (livingEntity.getVehicle() instanceof ShipMountingEntity) {
            this.riding = false;
        }
    }

}
