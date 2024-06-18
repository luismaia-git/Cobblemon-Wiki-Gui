plugins {
	id("java")
	id("fabric-loom") version("1.6-SNAPSHOT")
	kotlin("jvm") version ("1.8.20")
	kotlin("plugin.serialization") version "1.5.21"
}

group = property("maven_group")!!
version = property("mod_version")!!


// base {
//	archivesName = project.archives_base_name
//}

repositories {
	mavenLocal()
	mavenCentral()
	maven("https://maven.impactdev.net/repository/development/")
	maven("https://api.modrinth.com/maven")
	maven("https://oss.sonatype.org/content/repositories/snapshots")
	maven("https://maven.nucleoid.xyz/") {
		name = "nucleoid"
	}

}

fabricApi {
	configureDataGeneration()
}

dependencies {
	// To change the versions see the gradle.properties file
	minecraft("com.mojang:minecraft:${property("minecraft_version")}")
	mappings("net.fabricmc:yarn:${property("yarn_mappings")}:v2")
	modImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")

	// Fabric API. This is technically optional, but you probably want it anyway.
	modImplementation ("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")

	// Fabric Kotlin
	modImplementation("net.fabricmc:fabric-language-kotlin:${property("fabric_kotlin_version")}")

	// Cobblemon
	modImplementation("com.cobblemon:fabric:${property("cobblemon_version")}")

	// Permission API
	modImplementation("me.lucko:fabric-permissions-api:${property("permissions_api_version")}")

	modImplementation("eu.pb4:sgui:1.2.2+1.20")
	include("eu.pb4:sgui:1.2.2+1.20")

	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.1")
	implementation(kotlin("stdlib"))


}

tasks {
	processResources {
		inputs.property("version", project.version)

		filesMatching("fabric.mod.json") {
			expand(mutableMapOf("version" to project.version))
		}
	}

	jar {
		from("LICENSE")
	}

	compileKotlin {
		kotlinOptions.jvmTarget = "17"
	}

}


