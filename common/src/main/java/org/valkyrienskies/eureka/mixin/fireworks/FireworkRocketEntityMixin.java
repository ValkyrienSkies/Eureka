package org.valkyrienskies.eureka.mixin.fireworks;

import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.eureka.EurekaConfig;
import org.valkyrienskies.eureka.combat.FireworkExplosion;
import org.valkyrienskies.eureka.combat.StarConfig;
import org.valkyrienskies.eureka.fireworks.EurekaFirework;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(FireworkRocketEntity.class)
public abstract class FireworkRocketEntityMixin extends Entity implements EurekaFirework {
    @Unique
    protected boolean wasEurekaFired = false;
    @Shadow
    private static final EntityDataAccessor<ItemStack> DATA_ID_FIREWORKS_ITEM = SynchedEntityData.defineId(FireworkRocketEntity.class, EntityDataSerializers.ITEM_STACK);

    public FireworkRocketEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(
            method = "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;DDDZ)V",
            at = @At("TAIL")
    )
    private void FireworkRocketEntity(Level level, ItemStack itemStack, double d, double e, double f, boolean bl, CallbackInfo ci) {
        //This doesn't actually affect dispensers because mojank
        //It should, however, make modded firework shooters work
        if (VSGameUtilsKt.getShipManagingPos(level, new BlockPos(d, e, f)) != null) {
            wasEurekaFired = true;
        }
    }

    @Inject(
            method = "dealExplosionDamage",
            at = @At("TAIL")
    )
    private void dealExplosionDamage(CallbackInfo ci) {
        if (wasEurekaFired) {
            ItemStack itemStack = this.entityData.get(DATA_ID_FIREWORKS_ITEM);
            CompoundTag compoundTag = itemStack.isEmpty() ? null : itemStack.getTagElement("Fireworks");
            ListTag explosions = compoundTag.getList("Explosions", 10);
            for (int j = 0; j < explosions.size(); ++j) {
                FireworkRocketItem.Shape type = FireworkRocketItem.Shape.byId(explosions.getCompound(j).getByte("Type"));
                boolean sparkle = explosions.getCompound(j).getBoolean("Flicker");
                boolean trail = explosions.getCompound(j).getBoolean("Trail");
                explode(type, sparkle, trail);
            }
        }
    }

    @Unique
    private void explode(FireworkRocketItem.Shape shape, boolean sparkle, boolean trail){

        ExplosionDamageCalculator damageCalculator = new ExplosionDamageCalculator();

        StarConfig config = EurekaConfig.SERVER.getFireworks().get(shape);

        float size = config.getSize();
        boolean causesFire = config.getCausesFire();
        double power = config.getPower();
        Vec3 direction = config.getDirection() ? this.getDeltaMovement() : null;
        double angle = config.getAngle();
        double knockback = config.getKnockback();
        Explosion.BlockInteraction interaction = config.getInteraction();

        if (trail) {
            causesFire = true;
        }
        if (sparkle) {
            knockback *= 2.0;
        }

        FireworkExplosion explosion = new FireworkExplosion(this.level, this, null, damageCalculator, this.getX(), this.getY(), this.getZ(), size, power, direction, angle, causesFire, interaction);
        explosion.explode();
        final double origX = explosion.getX();
        final double origY = explosion.getY();
        final double origZ = explosion.getZ();

        double finalKnockback = knockback;
        VSGameUtilsKt.transformToNearbyShipsAndWorld(this.level, origX, origY, origZ, explosion.radius, (x, y, z) -> {
            explosion.setX(x);
            explosion.setY(y);
            explosion.setZ(z);
            explosion.explode();
            explosion.doExplodeForce(finalKnockback);
        });
        explosion.setX(origX);
        explosion.setY(origY);
        explosion.setZ(origZ);
        explosion.finalizeExplosion();
    }

    @Override
    public boolean getWasEurekaFired() {
        return wasEurekaFired;
    }

    @Override
    public void setWasEurekaFired(boolean bl) {
        wasEurekaFired = bl;
    }
}
