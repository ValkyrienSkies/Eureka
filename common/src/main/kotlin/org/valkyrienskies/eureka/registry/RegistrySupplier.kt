package org.valkyrienskies.eureka.registry

interface RegistrySupplier<T> {

    val name: String
    fun get(): T

}