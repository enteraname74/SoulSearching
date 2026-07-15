import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.androidKmpLibrary)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
}

group = "com.github.enteraname74.soulsearching.remote"
description = "Remote data access"

kotlin {
    jvmToolchain(17)
    android {
        namespace = "com.github.enteraname74.soulsearching.remote"
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
        optIn.add("kotlin.uuid.ExperimentalUuidApi")
    }

    sourceSets {
        val commonMain by getting
        val androidMain by getting
        val desktopMain by getting
        val jsMain by getting
        val wasmJsMain by getting
        val jvmMain = maybeCreate("jvmMain").apply {
            dependsOn(commonMain)

            dependencies {
                implementation(libs.ktor.client.cio)
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
            implementation(project(":domain"))
            implementation(project(":repository"))
            implementation(libs.ktor.client.auth)
            implementation(libs.ktor.client.content.negoctiation)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.resources)
            implementation(libs.ktor.client.websockets)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.serialization.logging)
            implementation(libs.koin.core)
            implementation(libs.kotlinx.serialization.json)
        }
    }
}
