import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency

plugins {
    id("com.android.library")
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    kotlin("kapt")
}

kotlin {
    androidTarget()
    jvm("desktop")

    sourceSets {
        androidMain {
            dependencies {
                implementation(libs.room)
                implementation(libs.koin.androidx.compose)

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
                implementation(project(":domain"))
                implementation(project(":repository"))
                implementation(project(":features:filemanager"))
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