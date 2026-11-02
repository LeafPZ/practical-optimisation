import java.nio.charset.Charset

import com.diffplug.spotless.LineEnding
import net.ltgt.gradle.errorprone.errorprone

val isCiBuild = providers.environmentVariable("CI").map { it.toBoolean() }.orElse(false).get()
val isSnapshot = providers.gradleProperty("isSnapshot").map { it.toBoolean() }.orElse(false).get()

val baseVersion = project.version.toString()
project.version = if (isSnapshot) "$baseVersion-SNAPSHOT" else if (!isCiBuild) "$baseVersion.local" else baseVersion

plugins {
    alias(libs.plugins.leaf.loom)

    alias(libs.plugins.errorprone)
    alias(libs.plugins.nullaway)

    checkstyle
    alias(libs.plugins.spotless)
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

spotless {
    setEncoding(Charset.forName("UTF-8"))
    setLineEndings(LineEnding.UNIX)

    java {
        targetExclude("**/generated/**")

        eclipse().configFile(file("eclipse-formatter.xml"))
            .sortMembersEnabled(true)
            // SF,SI,SM,F,I,C,M,T = Static Fields, Static Initializers, Static Methods,
            //   Fields, Initializers, Constructors, Methods, (Nested) Types
            .sortMembersOrder("SF,F,SI,I,C,SM,M,T")
            .sortMembersDoNotSortFields(true)
            .sortMembersVisibilityOrderEnabled(true)
            // B,R,D,V = Public, Protected, Package, Private
            .sortMembersVisibilityOrder("B,R,D,V")

        cleanthat()
        // formatAnnotations()

        importOrder(
            "java|javax",
            "",
            "net.fabricmc",
            "\\#net.fabricmc",
            "$group",
            "\\#",
            "\\#$group",
            "zombie",
            "\\#zombie"
        )
        removeUnusedImports()
        forbidWildcardImports()
        forbidModuleImports()

        trimTrailingWhitespace()
        leadingTabsToSpaces()
        endWithNewline()

        toggleOffOn()
        licenseHeaderFile(file("HEADER")).yearSeparator("-")
    }
}

checkstyle {
    configFile = file("checkstyle.xml")
}

dependencies {
    zomboid(libs.zomboid)
    implementation(libs.leaf.loader)
    // implementation(libs.leaf.api)

    compileOnly(libs.jspecify)
    errorprone(libs.errorprone)
    errorprone(libs.nullaway)
}

nullaway {
    annotatedPackages.add(project.group.toString())
    // onlyNullMarked = true
    // jspecifyMode = true
}

java {
    withSourcesJar()

    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

tasks.processResources {
    val projectName: String = project.name
    val projectVersion: String = project.version.toString()
    val loaderVersion: String = libs.versions.leaf.loader.get()
    val zomboidVersion: String = libs.versions.zomboid.get()

    val props = mapOf(
        "name" to projectName,
        "version" to projectVersion,
        "loader_version" to loaderVersion,
        "zomboid_version" to zomboidVersion
    )

    inputs.properties(props)

    filesMatching("leaf.mod.json") {
        expand(props)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.errorprone {
        disableWarningsInGeneratedCode = true

        nullaway {
            error()
        }
    }
}
