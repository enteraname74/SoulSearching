import com.github.enteraname74.buildsrc.Config
import com.github.enteraname74.buildsrc.Dependencies
import com.github.enteraname74.buildsrc.Versions
import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency

plugins {
    id("com.android.library")
//    id("kotlin-android")
    id("org.jetbrains.kotlin.multiplatform")
    kotlin("kapt")
}

kotlin {
    androidTarget()
    jvm("desktop")

    sourceSets {
        androidMain {
            dependencies {
                implementation(project(mapOf("path" to ":domain")))
                implementation("androidx.compose.ui:ui-graphics-android:1.5.4")

                implementation(Dependencies.KOIN_CORE)
                implementation(Dependencies.KOIN_COMPOSE)

                with(Dependencies.AndroidX) {
                    implementation(CORE)
                    implementation(ROOM)
                    configurations.getByName("kapt").dependencies.add(
                        DefaultExternalModuleDependency(
                            "androidx.room",
                            "room-compiler",
                            Versions.AndroidX.ROOM
                        )
                    )
                }

                with(Dependencies.Google) {
                    implementation(HILT_ANDROID)
                    configurations.getByName("kapt").dependencies.add(
                        DefaultExternalModuleDependency(
                            "com.google.dagger",
                            "hilt-compiler",
                            Versions.Google.DAGGER
                        )
                    )
                }
            }
        }
    }
}

android {
    namespace = Config.DATA_NAMESPACE
    compileSdk = Config.COMPILE_SDK

    defaultConfig {
        minSdk = Config.MIN_SDK
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