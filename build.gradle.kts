plugins {
    alias(libs.plugins.leaf.loom)
}

loom {
    mods {
        create(project.name) {
            sourceSet(sourceSets.main.get())
        }
    }
    // Decompiles only the zombie classes instead of every file.
    decompilers {
        getByName("vineflower") {
            options.putAll(mapOf(
                "included-classes" to "zombie.*",
            ))
        }
    }
}

dependencies {
    zomboid(libs.zomboid)
    implementation(libs.leaf.loader)
    // implementation(libs.leaf.api)
}

java {
    withSourcesJar()

    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

tasks {
    processResources {
        val projectVersion: String = project.version.toString()
        val loaderVersion: String = libs.versions.leaf.loader.get()
        val zomboidVersion: String = libs.versions.zomboid.get()

        val props = mapOf(
            "version" to projectVersion,
            "loader_version" to loaderVersion,
            "zomboid_version" to zomboidVersion
        )

        inputs.properties(props)

        filesMatching("leaf.mod.json") {
            expand(props)
        }
    }

    jar {
        from("LICENSE") {
            rename {
                "${it}_${project.name}"
            }
        }
    }
}
