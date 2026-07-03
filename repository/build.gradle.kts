import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("com.android.library")
    alias(libs.plugins.kotlinMultiplatform)
}

group = "com.github.enteraname74.soulsearching.repository"
description = "Repository layer of the app"

kotlin {
    androidTarget()
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
        val desktopMain by getting {
            dependencies {
                implementation(libs.jaudiotagger)
            }
        }
        val jsMain by getting
        val wasmJsMain by getting
        val webMain = maybeCreate("webMain").apply {
            dependsOn(commonMain)
        }

        jsMain.dependsOn(webMain)
        wasmJsMain.dependsOn(webMain)

        androidMain.dependencies {
            implementation(libs.jaudiotagger)
        }
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

android {
    namespace = "com.github.enteraname74.soulsearching.repository"
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
