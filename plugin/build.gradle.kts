import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    id("org.jetbrains.intellij") version "1.1.4"
    id("java")
}

group = "com.attachme"
version = "1.0.1"

java {
    sourceCompatibility = JavaVersion.VERSION_11
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(project(":agent"))
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    plugins.set(listOf("java"))
    version.set("2021.2")
}

tasks {
    patchPluginXml {
        changeNotes.set("")
        sinceBuild.set("211.*")
        untilBuild.set("212.*")
    }

    publishPlugin {
        token.set(System.getenv("ATTACHME_PUBLISH_TOKEN"))
    }

    processResources {
        dependsOn(":agent:build")
        from("${project(":agent").getBuildDir()}/libs") {
            rename("attachme-agent.jar", "attachme-agent-${version}.jar")
        }
        from("src/main/resources/conf.sh") {
            filter<ReplaceTokens> ("tokens" to mapOf("ATTACHME_VERSION" to version.toString()))
//            expand(mapOf("ATTACHME_VERSION" to version))
        }
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
}




