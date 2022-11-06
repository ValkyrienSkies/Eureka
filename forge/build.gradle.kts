plugins {
    idea
    kotlin("jvm")
    `maven-publish`
    id("net.minecraftforge.gradle") version ("5.1.+")
    //id "com.matthewprenger.cursegradle"
    //id "com.modrinth.minotaur"
}

//apply from: '../gradle-scripts/publish-curseforge.gradle'


repositories {
    maven("https://thedarkcolour.github.io/KotlinForForge/") {
        name = "Kotlin for Forge"
    }
}

val minecraft_version: String by project
val architectury_version: String by project
val kotlin_version: String by project
val forge_version: String by project
val forge_kotlin_version: String by project
val vs2_version: String by project
val mod_name: String by project
val mod_id: String by project
val cloth_config_version: String by project

val baseArchiveName = "${mod_name}-forge-${minecraft_version}"

base {
    archivesName.set(baseArchiveName)
}

minecraft {
    mappings("official", minecraft_version)

    accessTransformer(file("src/main/resources/META-INF/accesstransformer.cfg"))
    project.logger.debug("Forge Access Transformers are enabled for this project.")

    runs {
        create("client") {
            workingDirectory(project.file("run"))
            ideaModule("${rootProject.name}.${project.name}.main")
            taskName("Client")
            mods {
                create(mod_id) {
                    source(sourceSets.main.get())
                    source(project(":common").sourceSets.main.get())
                }
            }
        }

        create("server") {
            workingDirectory(project.file("run"))
            ideaModule("${rootProject.name}.${project.name}.main")
            taskName("Server")
            mods {
                create(mod_id) {
                    source(sourceSets.main.get())
                    source(project(":common").sourceSets.main.get())
                }
            }
        }

        create("data") {
            workingDirectory(project.file("run"))
            ideaModule("${rootProject.name}.${project.name}.main")
            args(
                "--mod",
                mod_id,
                "--all",
                "--output",
                file("src/generated/resources/"),
                "--existing",
                file("src/main/resources/")
            )
            taskName("Data")
            mods {
                create(mod_id) {
                    source(sourceSets.main.get())
                    source(project(":common").sourceSets.main.get())
                }
            }
        }
    }
}

sourceSets.main.get().resources.srcDir("src/generated/resources")

// Workaround for non-mc libraries in ForgeGradle
configurations {
    val library = maybeCreate("library")
    api.configure {
        extendsFrom(library)
    }
}

minecraft.runs.all {
    lazyToken("minecraft_classpath") {
        return@lazyToken configurations["library"].copyRecursive().resolve()
            .joinToString(File.pathSeparator) { it.absolutePath }
    }
}

dependencies {
    minecraft("net.minecraftforge:forge:${minecraft_version}-${forge_version}")
    implementation("thedarkcolour:kotlinforforge:${forge_kotlin_version}")
    compileOnly(project(":common"))
    compileOnly("org.valkyrienskies:valkyrien-dependency-downloader:4.2")

    api(fg.deobf("me.shedaniel.cloth:cloth-config:${cloth_config_version}"))
    implementation(fg.deobf("org.valkyrienskies:valkyrienskies-118-forge:${vs2_version}"))
}


tasks {

    processResources {
        from(project(":common").sourceSets.main.get().resources)
        inputs.property("version", project.version)

        val expands = mapOf(
            Pair("version", project.version),
            Pair("vs2_version", vs2_version),
            Pair("vs2_plain_version", vs2_version.substring(0, vs2_version.indexOf('+')))
        )

        filesMatching("META-INF/mods.toml") {
            expand(expands)
        }

        filesMatching("valkyrien_dependency_manifest.json") {
            expand(expands)
        }
    }

    jar {
        finalizedBy("reobfJar")
    }
}

publishing {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = "org.valkyrienskies.eureka"
                version = project.version as String
                artifactId = mod_name + "-" + project.name
                from(components["java"])
            }
        }
    }

    // See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
    repositories {
        val vs_maven_username: String? by project
        val vs_maven_password: String? by project
        val vs_maven_url: String? by project

        if (vs_maven_username != null && vs_maven_password != null) {
            println("Publishing to VS Maven")
            maven {
                url = uri(vs_maven_url ?: "https://maven.valkyrienskies.org/repository/maven-public/")
                credentials {
                    username = vs_maven_username
                    password = vs_maven_password
                }
            }
        }
        // Add repositories to publish to here.
        if (System.getenv("GITHUB_ACTOR") != null) {
            println("Publishing to Github Packages")
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/ValkyrienSkies/Eureka")
                credentials {
                    username = System.getenv("GITHUB_ACTOR")
                    password = System.getenv("GITHUB_TOKEN")
                }
            }
        }
    }
}
