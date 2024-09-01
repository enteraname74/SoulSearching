plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
    kotlin("plugin.serialization") version "1.9.22"
}

group = "com.github.enteraname74.soulsearching.repository"
description = "Repoistory layer of the app"

kotlin {
    androidTarget()
    jvm("desktop")
    jvmToolchain(17)

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.koin.core)
                implementation(libs.coroutines.core)
                implementation(libs.coroutines.core.jvm)
                implementation(compose.ui)
                implementation(libs.jaudiotagger)
                implementation(libs.bundles.ktor)
                implementation(project(":domain"))
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