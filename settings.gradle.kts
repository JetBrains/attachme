rootProject.name = "attachme"
include("agent")
include("plugin")

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.8.0")
}