import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    id("com.android.library")
}

group = "com.github.enteraname74.soulsearching.shareddi"
description = "Shared DI elements of the application"

kotlin {
    androidTarget()
    jvm("desktop")
    jvmToolchain(17)

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
    }

    sourceSets {
        val commonMain by getting
        val androidMain by getting
        val desktopMain by getting {
            dependencies {
                implementation(libs.coroutines.core.swing)
            }
        }
        val jsMain by getting
        val wasmJsMain by getting
        val jvmMain by creating {
            dependsOn(commonMain)

            dependencies {
                implementation(libs.jaudiotagger)
            }
        }
        val webMain = maybeCreate("webMain").apply {
            dependsOn(commonMain)
        }

        androidMain.dependsOn(jvmMain)
        desktopMain.dependsOn(jvmMain)
        jsMain.dependsOn(webMain)
        wasmJsMain.dependsOn(webMain)

        commonMain.dependencies {
            implementation(libs.koin.core)
            implementation(libs.coroutines.core)

            implementation(project(":domain"))
            implementation(project(":repository"))
            implementation(project(":remote"))
            implementation(project(":playback"))
            implementation(project(":filemanager"))
            implementation(project(":musicmanager"))
            implementation(project(":local"))

        }
    }
}

android {
    namespace = "com.github.enteraname74.soulsearching.shareddi"
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

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}