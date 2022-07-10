package org.valkyrienskies.eureka.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.eureka.EurekaEntities;

@Mixin(ClientPacketListener.class)
public class MixinClientPacketListener {

    @Shadow
    private ClientLevel level;

    @Shadow
    private Minecraft minecraft;

    @Inject(method = "handleAddEntity",
            at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/util/thread/BlockableEventLoop;)V"), cancellable = true)
    void handleSeat(final ClientboundAddEntityPacket packet, final CallbackInfo ci) {
        if (packet.getType().equals(EurekaEntities.INSTANCE.getSEAT().get())) {
            ci.cancel();
            final double d = packet.getX();
            final double e = packet.getY();
            final double f = packet.getZ();
            final Entity entity = EurekaEntities.INSTANCE.getSEAT().get().create(level);
            final int i = packet.getId();
            entity.setPacketCoordinates(d, e, f);
            entity.moveTo(d, e, f);
            entity.xRot = (float) (packet.getxRot() * 360) / 256.0f;
            entity.yRot = (float) (packet.getyRot() * 360) / 256.0f;
            entity.setId(i);
            entity.setUUID(packet.getUUID());
            this.level.putNonPlayerEntity(i, entity);
        }
    }
}
