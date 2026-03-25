# AGENTS.md

## Project overview
- Multi-module Gradle project: `agent` and `plugin`.
- IntelliJ Platform plugin built from `plugin` using Java 21 toolchain.

## Common commands
- `./gradlew build` for full build.
- `./gradlew :plugin:runIde` to launch the IDE with the plugin.
- `./gradlew :agent:build` to build the agent jar only.

## Notes
- Use the Gradle wrapper for all tasks.
- Keep changes compatible with the IntelliJ Platform Gradle plugin setup in `plugin/build.gradle.kts`.
