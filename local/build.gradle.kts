import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    id("com.android.library")
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
}

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
        androidMain {
            dependencies {
                // Used for injecting app context in database module
                implementation(libs.koin.androidx.compose)
                implementation(libs.androidx.room.sqlite.wrapper)
            }
        }
        commonMain {
            dependencies {
                implementation(project(":domain"))
                implementation(project(":repository"))
                implementation(project(":filemanager"))
                implementation(libs.koin.core)

                implementation(libs.androidx.paging.common)

                implementation(libs.androidx.room.runtime)
                implementation(libs.androidx.room.paging)
                implementation(libs.androidx.sqlite.bundled)
            }
        }
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspDesktop", libs.androidx.room.compiler)
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