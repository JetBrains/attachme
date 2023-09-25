import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    id("org.jetbrains.intellij") version "1.15.0"
    id("java")
}

group = "com.attachme"
version = "1.2.4"

java {
    sourceCompatibility = JavaVersion.VERSION_11
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":agent"))
}

// See https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    plugins.set(listOf("java"))
    version.set("LATEST-EAP-SNAPSHOT")
}

tasks {
    patchPluginXml {
        changeNotes.set("")
        sinceBuild.set("223.*")
        untilBuild.set("233.*")
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




