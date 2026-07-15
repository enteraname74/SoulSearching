import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.androidKmpLibrary)
    alias(libs.plugins.kotlinMultiplatform)
}

group = "com.github.enteraname74.soulsearching.repository"
description = "Repository layer of the app"

kotlin {
    android {
        namespace = "com.github.enteraname74.soulsearching.repository"
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
    jvmToolchain(17)

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
        optIn.add(
            "kotlin.uuid.ExperimentalUuidApi"
        )
    }

    sourceSets {
        val commonMain by getting
        val desktopMain by getting
        val androidMain by getting
        val jsMain by getting
        val wasmJsMain by getting

        val webMain = maybeCreate("webMain").apply {
            dependsOn(commonMain)
        }

        val jvmMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation(libs.jaudiotagger)
            }
        }

        jsMain.dependsOn(webMain)
        wasmJsMain.dependsOn(webMain)

        desktopMain.dependsOn(jvmMain)
        androidMain.dependsOn(jvmMain)

        commonMain {
            dependencies {
                implementation(libs.compose.ui)
                implementation(libs.androidx.paging.common)
                implementation(libs.koin.core)

                implementation(libs.coroutines.core)

                implementation(project(":domain"))
                implementation(project(":filemanager"))
                implementation(project(":serialization"))
            }
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
