package org.valkyrienskies.eureka.client

import net.minecraft.client.renderer.entity.EntityRenderDispatcher
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity

class EmptyRenderer(dispatcher: EntityRenderDispatcher, _ignore: ItemRenderer) :
    EntityRenderer<Entity>(dispatcher) {

    override fun getTextureLocation(entity: Entity): ResourceLocation? = null
}
