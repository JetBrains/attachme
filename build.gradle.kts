import org.gradle.buildconfiguration.tasks.UpdateDaemonJvm

tasks.named<UpdateDaemonJvm>("updateDaemonJvm") {
    languageVersion.set(JavaLanguageVersion.of(21))
    toolchainPlatforms.empty()
}
