plugins {
    kotlin("jvm")
    `maven-publish`
    id("org.spongepowered.gradle.vanilla") version "0.2.1-SNAPSHOT"
}

val minecraft_version: String by project
val architectury_version: String by project
val vs2_version: String by project
val mod_name: String by project
val mod_id: String by project

val baseArchiveName = "${mod_name}-common-${minecraft_version}"

base {
    archivesName.set(baseArchiveName)
}

repositories {
    maven("https://maven.quiltmc.org/repository/release/") {
        name = "Quilt"
    }
}

minecraft {
    version(minecraft_version)

    accessWideners(file("src/main/resources/vs_eureka.accesswidener"))
}

dependencies {
    compileOnly("org.spongepowered:mixin:0.8.5")

    api("org.valkyrienskies:valkyrienskies-118-common:${vs2_version}:dev")
    api("org.valkyrienskies.core:vs-core:1.0.0+5afa034903")
    forgeFlower("org.quiltmc:quiltflower:1.9.0")

    //api("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.10")
    //api("org.jetbrains.kotlin:kotlin-reflect:1.6.10")
}

tasks.processResources {
    val buildProps = project.properties

    filesMatching("pack.mcmeta") {

        expand(buildProps)
    }
}

publishing {
    repositories {
        val ghpUser = (project.findProperty("gpr.user") ?: System.getenv("USERNAME")) as String?
        val ghpPassword = (project.findProperty("gpr.key") ?: System.getenv("TOKEN")) as String?
        // Publish to Github Packages
        if (ghpUser != null && ghpPassword != null) {
            println("Publishing to GitHub Packages")
            maven {
                name = "GithubPackages"
                url = uri("https://maven.pkg.github.com/ValkyrienSkies/vs-core")
                credentials {
                    username = ghpUser
                    password = ghpPassword
                }
            }
        }

        val vsMavenUsername = project.findProperty("vs_maven_username") as String?
        val vsMavenPassword = project.findProperty("vs_maven_password") as String?
        val vsMavenUrl = project.findProperty("vs_maven_url") as String?
        if (vsMavenUrl != null && vsMavenPassword != null && vsMavenUsername != null) {
            println("Publishing to VS Maven")
            maven {
                url = uri(vsMavenUrl)
                credentials {
                    username = vsMavenUsername
                    password = vsMavenPassword
                }
            }
        }
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = "org.valkyrienskies.eureka"
                artifactId = mod_name + "-" + project.name
                version = project.version as String

                from(components["java"])
            }
        }
    }
}