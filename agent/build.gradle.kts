plugins {
    java
}

group = "com.attachme"
version = "1.0"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    testImplementation("junit:junit:4.13.1")
    testImplementation("com.spotify:docker-client:8.16.0")
}

tasks.jar {
    archiveFileName.set("attachme-agent.jar")
    manifest {
        attributes(
            "Premain-Class" to "com.attachme.agent.Agent",
            "Can-Redefine-Classes" to "true",
            "Can-Retransform-Classes" to "true",
            "Can-Set-Native-Method-Prefix" to "true",
            "Implementation-Title" to "ClassLogger",
            "Implementation-Version" to project.version.toString()
        )
    }
}

tasks.test {
    useJUnit()
    dependsOn(tasks.named("assemble"))
}