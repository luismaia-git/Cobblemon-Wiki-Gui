plugins {
	id("cwg.root")
	id ("net.nemerosa.versioning") version "3.1.0"
}

version = "${project.property("mod_version")}+${project.property("mc_version")}"
