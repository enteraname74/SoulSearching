plugins {
    `kotlin-dsl`
    id("io.github.jwharm.flatpak-gradle-generator") version "1.8.0"
}

group = "com.github.enteraname74.soulsearching.buildlogic"

dependencies {
    implementation(libs.gradle)
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlin.get()}")
    implementation("org.jetbrains.kotlin.plugin.compose:org.jetbrains.kotlin.plugin.compose.gradle.plugin:${libs.versions.kotlin.get()}")
    implementation("org.jetbrains.compose:compose-gradle-plugin:${libs.versions.compose.multiplatform.get()}")
    implementation("com.google.devtools.ksp:symbol-processing-gradle-plugin:${libs.versions.ksp.get()}")
    implementation("androidx.room3:room3-gradle-plugin:${libs.versions.room.get()}")
    implementation("io.github.jwharm.flatpak-gradle-generator:io.github.jwharm.flatpak-gradle-generator.gradle.plugin:1.8.0")
}

val flatpakOnlyArch = providers.gradleProperty("flatpakOnlyArch").orElse("host")

tasks.flatpakGradleGenerator {
    outputFile = rootProject.file(
        "../flatpak/dependencies/${flatpakOnlyArch.get()}/build-logic.json"
    )
    downloadDirectory = "offline-repository"
    onlyArches = flatpakOnlyArch
    includeConfigurations = setOf("compileClasspath", "runtimeClasspath")

    doFirst {
        outputFile.get().asFile.parentFile.mkdirs()
    }
}
