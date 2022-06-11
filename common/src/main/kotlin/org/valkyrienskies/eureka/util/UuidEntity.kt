package org.valkyrienskies.eureka.util

import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import java.util.UUID
import kotlin.reflect.KProperty

class UuidEntity<T : Entity>(var entity: Any? = null) {

    fun provideUUID(uuid: UUID) {
        if (entity == null) entity = uuid
    }

    fun provideLevel(level: Level) = if (level.isClientSide && entity != null)
        entity = (level as ServerLevel).getEntity(entity as UUID)
    else null

    fun save(tag: CompoundTag, name: String) {
        tag.putUUID(name, (entity as Entity).uuid)
    }

    fun load(tag: CompoundTag, name: String) {
        provideUUID(tag.getUUID(name))
    }

    operator fun getValue(thisRef: Any, property: KProperty<*>): T? = if (entity is Entity) entity as T? else null

    operator fun setValue(thisRef: Any, property: KProperty<*>, entity: Any?) {
        this.entity = entity
    }
}
