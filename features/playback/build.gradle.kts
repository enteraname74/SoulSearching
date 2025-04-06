import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    id("com.android.library")
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

group = "com.github.enteraname74.soulsearching.features.playback"
description = "Playback elements of the application"

kotlin {
    jvmToolchain(17)
    androidTarget()
    jvm("desktop")

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        // Common compiler options applied to all Kotlin source sets for expect / actual implementations
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    sourceSets {
        val desktopMain by getting {
            dependencies {
//                implementation(libs.jlibnotify)
                implementation(libs.coroutines.core.swing)
                implementation(libs.vlcj)
            }
        }
        commonMain.dependencies {
            implementation(project(":domain"))
            implementation(compose.ui)
            implementation(libs.koin.core)

            implementation(project(":httpclient"))
            implementation(project(":filemanager"))
            implementation(libs.bundles.ktor)
        }

        androidMain.dependencies {
            implementation(libs.bundles.androidx)
//            implementation(libs.androidx.media3.exoplayer)
            implementation(libs.koin.androidx.compose)
//            implementation(libs.ffmpeg)
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
