import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.compose")
    kotlin("kapt")
}

kotlin {
    androidTarget()
    jvm("desktop")

    sourceSets {
        androidMain {
            dependencies {
                implementation(compose.ui)
                implementation(libs.koin.androidx.compose)
                implementation(libs.androidx.core)
                implementation(libs.room)

                configurations.getByName("kapt").dependencies.add(
                    DefaultExternalModuleDependency(
                        "androidx.room",
                        "room-compiler",
                        libs.versions.room.get()
                    )
                )
            }
        }
        commonMain {
            dependencies {
                implementation(project(mapOf("path" to ":domain")))
                implementation(libs.koin.core)
            }
        }
    }
}

android {
    namespace = "com.github.enteraname74.soulsearching.data"
    compileSdk = libs.versions.android.compile.sdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.min.sdk.get().toInt()
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