import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("com.android.library")
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

group = "com.github.enteraname74.soulsearching.coreui"
description = "Core UI elements of the application"

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
    }

    sourceSets {
        val jsMain by getting
        val wasmJsMain by getting
        val commonMain by getting
        val desktopMain by getting

        val nonAndroidMain by creating {
            dependsOn(commonMain)
        }

        val webMain = maybeCreate("webMain").apply {
            dependsOn(commonMain)
            dependsOn(nonAndroidMain)
        }

        jsMain.dependsOn(webMain)
        wasmJsMain.dependsOn(webMain)

        desktopMain.dependsOn(nonAndroidMain)

        commonMain.dependencies {
            implementation(libs.androidx.annotation)

            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.resources)
            implementation(libs.compose.ui)
            implementation(libs.compose.navigationevent)

            implementation(libs.kmpalette)

            implementation(libs.markdown.renderer.core)
            implementation(libs.markdown.renderer.m3)


            implementation(project(":domain"))
        }

        androidMain.dependencies {
            implementation(libs.bundles.androidx)
            implementation(libs.bundles.accompanist)
            implementation(libs.compose.cloudy)
        }
    }
}

compose.resources {
    publicResClass = true
    nameOfResClass = "CoreRes"
    generateResClass = always
}

android {
    namespace = "com.github.enteraname74.soulsearching.coreui"
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
