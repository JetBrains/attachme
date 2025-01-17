import org.apache.tools.ant.filters.ReplaceTokens
import org.jetbrains.intellij.platform.gradle.extensions.intellijPlatform

plugins {
    id("org.jetbrains.intellij.platform") version "2.2.1"
    id("java")
}

group = "com.attachme"
version = "1.2.10"

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
        intellijIdeaCommunity("251.14649.49")
        bundledPlugin("com.intellij.java")
    }
}

dependencies {
    implementation(project(":agent"))
}

tasks {
    patchPluginXml {
        changeNotes.set("")
        sinceBuild.set("251")
        untilBuild.set("251.*")
    }

    publishPlugin {
        token.set(System.getenv("ATTACHME_PUBLISH_TOKEN"))
    }

    processResources {
        dependsOn(":agent:build")
        from("${project(":agent").layout.buildDirectory}/libs") {
            rename("attachme-agent.jar", "attachme-agent-${version}.jar")
        }
        from("src/main/resources/conf.sh") {
            filter<ReplaceTokens> ("tokens" to mapOf("ATTACHME_VERSION" to version.toString()))
        }
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
}




