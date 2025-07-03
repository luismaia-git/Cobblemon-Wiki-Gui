plugins {
    id("cwg.platform")
}

architectury {
    platformSetupLoomIde()
    neoForge()
}

loom {
    neoForge {}
}

repositories {
    maven(url = "https://thedarkcolour.github.io/KotlinForForge/")
    maven(url = "https://api.modrinth.com/maven")
    maven(url = "https://maven.neoforged.net/releases")

    maven("https://maven.impactdev.net/repository/development/")
    mavenLocal()

    flatDir {
        dir("lib")
    }
}

dependencies {
    neoForge(libs.neoforge)
    modLocalRuntime(libs.neoforge.debugutils)

    implementation(project(":common", configuration = "namedElements")) {
        isTransitive = false
    }
    implementation(libs.neo.kotlin.forge) {
        exclude(group = "net.neoforged.fancymodloader", module = "loader")
    }
    "developmentNeoForge"(project(":common", configuration = "namedElements")) {
        isTransitive = false
    }
    bundle(project(path = ":common", configuration = "transformProductionNeoForge")) {
        isTransitive = false
    }
    testImplementation(project(":common", configuration = "namedElements"))

    modImplementation("eu.pb4:sgui:1.9.1+1.21.1-neoforge")
    include("eu.pb4:sgui:1.9.1+1.21.1-neoforge")

    modImplementation(libs.cobblemon.neoforge)

}

tasks {
    processResources {
        inputs.property("version", rootProject.version)
        inputs.property("minecraft_version", rootProject.property("mc_version").toString())
        inputs.property("java_version", rootProject.property("java_version").toString())

        filesMatching("META-INF/neoforge.mods.toml") {
            expand(
                "version" to rootProject.version,
                "minecraft_version" to rootProject.property("mc_version").toString(),
                "java_version" to rootProject.property("java_version").toString()
            )
        }
    }
}




