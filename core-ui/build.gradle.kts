import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

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

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        // Common compiler options applied to all Kotlin source sets for expect / actual implementations
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.resources)
            implementation(libs.compose.ui)
            implementation(libs.androidx.annotation)

            implementation(libs.kmpalette)

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
