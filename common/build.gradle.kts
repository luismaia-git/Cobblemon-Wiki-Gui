
import utilities.version
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

plugins {
    id("cwg.base")

    id("net.kyori.blossom")
    id("org.jetbrains.gradle.plugin.idea-ext")
    id("net.nemerosa.versioning") version "3.1.0"
}

architectury {
    common("neoforge", "fabric")
}

repositories {
    maven(url = "https://api.modrinth.com/maven")
    maven(url = "https://maven.neoforged.net/releases")
    maven("https://maven.impactdev.net/repository/development/")
    maven("https://maven.nucleoid.xyz/") {
        name = "nucleoid"
    }
    mavenLocal()
}

dependencies {
    implementation(libs.bundles.kotlin)
    modImplementation(libs.fabric.loader)

    modCompileOnly(libs.sgui)

    modCompileOnly(libs.cobblemon)

}


sourceSets {
    main {
        blossom {
            kotlinSources {
                property("modid", "cobblemon-wiki-gui")
                property("version", project.version())
                property("gitCommit", versioning.info.commit)
                property("branch", versioning.info.branch)
                System.getProperty("buildNumber")?.let { property("buildNumber", it) }
                property("timestamp", OffsetDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss")) + " UTC")
            }
        }
    }
}