import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.androidKmpLibrary)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

group = "com.github.enteraname74.soulsearching.features.filemanager"
description = "File manager elements of the application"

kotlin {
    jvmToolchain(17)
    android {
        namespace = "com.github.enteraname74.soulsearching.features.filemanager"
        compileSdk = libs.versions.android.compile.sdk.get().toInt()
        minSdk = libs.versions.android.min.sdk.get().toInt()
        compilerOptions.jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
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

        optIn.add(
            "kotlin.uuid.ExperimentalUuidApi"
        )
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
            implementation(libs.compose.ui)
            implementation(libs.compose.resources)
            implementation(libs.koin.core)

            implementation(libs.coroutines.core)

            implementation(project(":domain"))
        }

        androidMain.dependencies {
            implementation(libs.bundles.androidx)
        }
    }
}
