pluginManagement {
    repositories {
        /*maven {
            val vs_maven_url: String? by settings
            val vs_maven_username: String? by settings
            val vs_maven_password: String? by settings

            name = "Valkyrien Skies Internal"
            url = uri(vs_maven_url ?: "https://maven.valkyrienskies.org")

            if (vs_maven_username != null && vs_maven_password != null) {
                credentials {
                    username = vs_maven_username!!
                    password = vs_maven_password!!
                }
            }
        }*/
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/") {
            name = "Fabric"
        }
        maven("https://repo.spongepowered.org/repository/maven-public/") {
            name = "Sponge Snapshots"
        }
        maven("https://maven.minecraftforge.net") {
            name = "Forge"
        }
        maven("https://maven.architectury.dev/") {
            name = "Architectury"
        }
    }

    resolutionStrategy {
        eachPlugin {
            // If we request Forge, actually give it the correct artifact.
            if (requested.id.id == "net.minecraftforge.gradle") {
                useModule("${requested.id}:ForgeGradle:${requested.version}")
            }

            if (requested.id.namespace?.startsWith("org.jetbrains.kotlin") == true) {
                val kotlin_version: String by settings
                useVersion(kotlin_version)
            }
        }
    }
}

include("common")
include("fabric")
include("forge")

rootProject.name = "vs-eureka-mod"
