import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("com.android.library")
    alias(libs.plugins.kotlinMultiplatform)
}

group = "com.github.enteraname74.soulsearching.features.playback"
description = "File manager elements of the application"

kotlin {
    jvmToolchain(17)
    androidTarget()
    jvm("desktop")
    js {
        browser()
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        // Common compiler options applied to all Kotlin source sets for expect / actual implementations
        freeCompilerArgs.add("-Xexpect-actual-classes")
        optIn.addAll(
            "kotlin.time.ExperimentalTime",
            "kotlin.uuid.ExperimentalUuidApi"
        )
    }

    sourceSets {
        val jsMain by getting
        val wasmJsMain by getting
        val commonMain by getting

        val webMain = maybeCreate("webMain").apply {
            dependsOn(commonMain)
        }

        jsMain.dependsOn(webMain)
        wasmJsMain.dependsOn(webMain)

        val desktopMain by getting {
            dependencies {
                implementation(libs.coroutines.core.swing)
            }
        }
        commonMain.dependencies {
            implementation(project(":domain"))
            implementation(project(":core-ui"))
            implementation(libs.koin.core)
            implementation(libs.jaudiotagger)
            implementation(libs.coroutines.core)
        }
    }
}

android {
    namespace = "com.github.enteraname74.soulsearching.features.playback"
    compileSdk = libs.versions.android.compile.sdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.min.sdk.get().toInt()
    }

    buildTypes {
        create("dev-release")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
    }
}
