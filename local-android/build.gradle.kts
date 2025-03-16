import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency

plugins {
    id("com.android.library")
    alias(libs.plugins.kotlinMultiplatform)
    kotlin("kapt")
}

kotlin {
    androidTarget()

    sourceSets {
        androidMain {
            dependencies {
                implementation(libs.room)
                // Used for injecting app context in database module
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
                implementation(project(":filemanager"))
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