import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    id("org.jetbrains.intellij") version "1.6.0"
    id("java")
}

group = "com.attachme"
version = "1.2.0"

java {
    sourceCompatibility = JavaVersion.VERSION_11
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":agent"))
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    plugins.set(listOf("java"))
    version.set("2022.1")
}

tasks {
    patchPluginXml {
        changeNotes.set("")
        sinceBuild.set("211.*")
        untilBuild.set("222.*")
    }

    publishPlugin {
        token.set(System.getenv("ATTACHME_PUBLISH_TOKEN"))
    }

    processResources {
        dependsOn(":agent:build")
        from("${project(":agent").buildDir}/libs") {
            rename("attachme-agent.jar", "attachme-agent-${version}.jar")
        }
        from("src/main/resources/conf.sh") {
            filter<ReplaceTokens> ("tokens" to mapOf("ATTACHME_VERSION" to version.toString()))
//            expand(mapOf("ATTACHME_VERSION" to version))
        }
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
}




