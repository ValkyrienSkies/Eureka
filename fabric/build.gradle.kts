import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    idea
    kotlin("jvm")
    `maven-publish`
    id("fabric-loom") version "0.10-SNAPSHOT"
    //id "com.matthewprenger.cursegradle"
    //id "com.modrinth.minotaur"
}

val minecraft_version: String by project
val architectury_version: String by project
val fabric_api_version: String by project
val fabric_loader_version: String by project
val vs2_version: String by project
val mod_name: String by project
val mod_id: String by project
val cloth_config_version: String by project

// TODO make this work
//apply from: '../gradle-scripts/publish-curseforge.gradle'

val baseArchiveName = "${mod_name}-fabric-${minecraft_version}"

base {
    archivesName.set(baseArchiveName)
}

loom {
    // TODO
    accessWidenerPath.set(File(project(":common").projectDir, "src/main/resources/vs_eureka.accesswidener"))

    runs {
        named("client") {
            client()
            configName = "Fabric Client"
            ideConfigGenerated(true)
            runDir("run")
        }
        named("server") {
            server()
            configName = "Fabric Server"
            ideConfigGenerated(true)
            runDir("run")
        }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${minecraft_version}")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-1.18.2:2022.09.04@zip")
    })

    modImplementation("net.fabricmc:fabric-loader:${fabric_loader_version}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${fabric_api_version}")
    modImplementation("net.fabricmc:fabric-language-kotlin:1.8.3+kotlin.1.7.10")

    // Mod menu
    modImplementation("com.terraformersmc:modmenu:1.16.23")
    modApi("me.shedaniel.cloth:cloth-config:${cloth_config_version}") {
        exclude(group = "net.fabricmc.fabric-api")
    }
    //modImplementation("org.valkyrienskies:valkyrienskies-118-fabric:${vs2_version}") {
    //  exclude(group = "net.fabricmc.fabric-api")
    //}

    implementation(project(":common"))
    implementation("org.valkyrienskies:valkyrien-dependency-downloader:4.2")
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

        filesMatching("fabric.mod.json") {
            expand(expands)
        }

        filesMatching("valkyrien_dependency_manifest.json") {
            expand(expands)
        }
    }

    withType<KotlinCompile> {
        source(project(":common").sourceSets.main.get().allSource)
    }

    jar {
        from("LICENSE") {
            rename { "${it}_${mod_name}" }
        }

        classifier = "dev"
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
