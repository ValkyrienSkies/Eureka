import java.text.SimpleDateFormat
import java.util.Date

val minecraft_version: String by project
val mod_name: String by project

plugins {
    idea
    kotlin("jvm")
}

allprojects {
    repositories {
        mavenCentral()
        maven("https://maven.shedaniel.me/") { name = "Cloth Config" }
        maven("https://repo.spongepowered.org/repository/maven-public/") {
            name = "Sponge / Mixin"
        }
        maven("https://maven.blamejared.com") {
            name = "BlameJared Maven (CrT / Bookshelf)"
        }
        maven("https://maven.parchmentmc.org") {
            name = "ParchmentMC"
        }
        maven("https://maven.valkyrienskies.org") {
            name = "Valkyrien Skies"
        }
        mavenLocal()
    }

    tasks.withType<GenerateModuleMetadata> {
        enabled = false
    }
}


subprojects {
    // Apply checkstyle and ktlint to check the code style of every sub project
    apply(plugin = "org.jetbrains.kotlin.jvm")

    extensions.configure<JavaPluginExtension> {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
        withJavadocJar()
        withSourcesJar()
    }

    tasks {
        jar {
            manifest {
                attributes(
                    "Specification-Title" to mod_name,
                    "Specification-Vendor" to "Valkyrien Skies",
                    "Specification-Version" to archiveVersion,
                    "Implementation-Title" to project.name,
                    "Implementation-Version" to archiveVersion,
                    "Implementation-Vendor" to "Valkyrien Skies",
                    "Implementation-Timestamp" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date()),
                    "Timestamp" to System.currentTimeMillis(),
                    "Built-On-Java" to "${System.getProperty("java.vm.version")} (${System.getProperty("java.vm.vendor")})",
                    "Build-On-Minecraft" to minecraft_version
                )
            }
        }
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(17)
    }
}

// Determine the version
version = if (project.hasProperty("CustomReleaseVersion")) {
    project.property("CustomReleaseVersion") as String
} else {
    // Yes, I know there is a gradle plugin to detect git version.
    // But its made by Palantir 0_0.
    val gitRevision = "git rev-parse HEAD".execute()
    "1.0.0+" + gitRevision.substring(0, 10)
}

// region Util functions

fun String.execute(envp: Array<String>? = null, dir: File = projectDir): String {
    val process = Runtime.getRuntime().exec(this, envp, projectDir)
    return process.inputStream.reader().readText()
}

// endregion