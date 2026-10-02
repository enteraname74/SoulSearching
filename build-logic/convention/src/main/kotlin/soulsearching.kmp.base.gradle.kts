import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import io.github.jwharm.flatpakgradlegenerator.FlatpakGradleGeneratorTask

plugins {
    kotlin("multiplatform")
    id("com.android.kotlin.multiplatform.library")
    id("io.github.jwharm.flatpak-gradle-generator")
}

kotlin {
    jvmToolchain(21)

    android {
        compileSdk = 37
        minSdk = 26
        compilerOptions.jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }

    jvm("desktop")
    js {
        browser()
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    sourceSets {
        val commonMain by getting
        val androidMain by getting
        val desktopMain by getting
        val jsMain by getting
        val wasmJsMain by getting

        val nonAndroidMain by creating {
            dependsOn(commonMain)
        }
        val jvmMain by creating {
            dependsOn(commonMain)
        }
        val webMain by creating {
            dependsOn(nonAndroidMain)
        }

        androidMain.dependsOn(jvmMain)
        desktopMain.dependsOn(jvmMain)
        desktopMain.dependsOn(nonAndroidMain)
        jsMain.dependsOn(webMain)
        wasmJsMain.dependsOn(webMain)
    }

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
        optIn.addAll(
            "kotlin.time.ExperimentalTime",
            "kotlin.uuid.ExperimentalUuidApi",
        )
    }
}

val flatpakOnlyArch = providers.gradleProperty("flatpakOnlyArch").orElse("host")

tasks.named<FlatpakGradleGeneratorTask>("flatpakGradleGenerator") {
    outputFile.set(
        rootProject.layout.projectDirectory.file(
            "flatpak/dependencies/${flatpakOnlyArch.get()}/${project.name}.json"
        )
    )
    downloadDirectory.set("offline-repository")
    onlyArches.set(flatpakOnlyArch)
    includeConfigurations.set(
        setOf(
            "desktopCompileClasspath",
            "desktopRuntimeClasspath",
            "kspDesktop",
        )
    )

    doFirst {
        outputFile.get().asFile.parentFile.mkdirs()
    }
}
