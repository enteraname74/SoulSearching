plugins {
    id("com.android.library")
    alias(libs.plugins.kotlinMultiplatform)
}

group = "com.github.enteraname74.soulsearching.repository"
description = "Repository layer of the app"

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

                implementation(libs.jaudiotagger)

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