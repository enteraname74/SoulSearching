import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

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

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        // Common compiler options applied to all Kotlin source sets for expect / actual implementations
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.koin.core)
            implementation(project(":domain"))
            implementation(project(":repository"))
            implementation(project(":remote"))
            implementation(project(":features:playback"))
            implementation(project(":features:filemanager"))
            implementation(project(":features:musicmanager"))
        }
        androidMain.dependencies {
            implementation(project(":local-android"))
        }
        val desktopMain by getting {
            dependencies {
                implementation(project(":local-desktop"))
            }
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