package org.valkyrienskies.eureka.mixin.fireworks;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.eureka.fireworks.EurekaFirework;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(Projectile.class)
public class ProjectileMixin extends Entity {

    public ProjectileMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(
            method="shoot",
            at=@At("HEAD")
    )
    public void shoot(double x, double y, double z, float velocity, float inaccuracy, CallbackInfo ci) {
        if (this instanceof EurekaFirework) {
            if (VSGameUtilsKt.getShipManagingPos(this.level, this.getX(), this.getY(), this.getZ()) != null) {
                ((EurekaFirework)this).setWasEurekaFired(true);
            }
        }
    }

    @Override
    public void defineSynchedData() {

    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {

    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {

    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return null;
    }
}
