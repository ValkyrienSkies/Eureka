package org.valkyrienskies.eureka.mixin.client;

import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.eureka.EurekaEntities;

@Mixin(EntityRenderDispatcher.class)
public class MixinEntityRenderDispatcher {

    @Inject(method = "registerRenderers", at = @At("TAIL"))
    void registerRenderers(final ItemRenderer itemRenderer,
                           final ReloadableResourceManager reloadableResourceManager,
                           final CallbackInfo ci) {
        EurekaEntities.INSTANCE.registerRenderers$eureka((EntityRenderDispatcher) (Object) this, itemRenderer);
    }

}
