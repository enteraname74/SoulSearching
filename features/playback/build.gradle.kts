import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

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
        val jsMain by getting
        val wasmJsMain by getting
        val nonAndroidMain by creating {
            dependsOn(commonMain.get())
        }

        val webMain = maybeCreate("webMain").apply {
            dependsOn(nonAndroidMain)
        }

        jsMain.dependsOn(webMain)
        wasmJsMain.dependsOn(webMain)

        val desktopMain by getting {
            dependencies {
                //                implementation(libs.jlibnotify)
                implementation(libs.dbus)
                implementation(libs.dbus.transport.native.unixsocket)
                implementation(libs.coroutines.core.swing)
                implementation(libs.vlcj)
            }
        }

        desktopMain.dependsOn(nonAndroidMain)

        commonMain.dependencies {
            implementation(libs.compose.ui)
            implementation(libs.koin.core)

            implementation(libs.androidx.paging.compose)
            implementation(libs.androidx.paging.common)

            implementation(project(":domain"))
            implementation(project(":filemanager"))
        }

        androidMain.dependencies {
            implementation(libs.bundles.androidx)
            implementation(libs.androidx.media3.exoplayer)
            implementation(libs.androidx.media3.datasource)
            implementation(libs.androidx.media3.session)
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
