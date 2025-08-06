import org.apache.tools.ant.filters.ReplaceTokens
import org.jetbrains.intellij.platform.gradle.extensions.intellijPlatform

plugins {
    id("org.jetbrains.intellij.platform") version "2.7.0"
    id("java")
}

group = "com.attachme"
version = "1.2.11"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity("2025.2")
        bundledPlugin("com.intellij.java")
    }
}

dependencies {
    implementation(project(":agent"))
}

tasks {
    patchPluginXml {
        changeNotes.set("")
        sinceBuild.set("252")
        untilBuild.set("252.*")
    }

    publishPlugin {
        token.set(System.getenv("ATTACHME_PUBLISH_TOKEN"))
    }

    named<ProcessResources>("processResources") {
        dependsOn(":agent:build")

        // Explicitly declare inputs and outputs
        inputs.files(fileTree("src/main/resources/conf.sh"))
        inputs.files(project(":agent").layout.buildDirectory.dir("libs"))
        inputs.property("version", version)

        outputs.dir(layout.buildDirectory.dir("resources/main"))

        // Task configuration
        from(project(":agent").layout.buildDirectory.dir("libs")) {
            rename("attachme-agent.jar", "attachme-agent-${version}.jar")
        }

        from("src/main/resources/conf.sh") {
            filter<ReplaceTokens>(mapOf("tokens" to mapOf("ATTACHME_VERSION" to version.toString())))
        }

        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }

}

