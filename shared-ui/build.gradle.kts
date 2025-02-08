import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    id("com.android.application")
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinSerialization)
}

group = "com.github.enteraname74.soulsearching"
description = "Application's elements"

kotlin {

    androidTarget()
    jvm("desktop")

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        // Common compiler options applied to all Kotlin source sets for expect / actual implementations
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    sourceSets {
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.common)
                implementation(libs.coroutines.core.swing)
                implementation(libs.vlcj)
            }
        }
        commonMain {
            dependencies {
                implementation(project(":domain"))
                implementation(project(":core-ui"))
                implementation(project(":shared-di"))
                implementation(project(":features:playback"))
                implementation(project(":features:filemanager"))
                implementation(project(":features:musicmanager"))

                implementation(libs.bundles.koin)

                implementation(libs.kotlinx.serialization)
                implementation(libs.kotlinx.serialization.json)

                implementation(libs.kmpalette)
                implementation(libs.components.resources)
                implementation(libs.multiplatform.settings)

                implementation(compose.ui)
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.materialIconsExtended)
                implementation(compose.material3)
                implementation(compose.components.resources)

                implementation(libs.jaudiotagger)
                implementation(libs.androidx.annotation)
                implementation(libs.bundles.voyager)

                implementation(libs.coroutines.core)

                implementation(libs.file.kit)
                implementation(libs.qr.kit)

                implementation(libs.bundles.coil)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.koin.androidx.compose)
                implementation(libs.koin.androidx.workmanager)

                implementation(libs.bundles.androidx)
                implementation(libs.room)

                implementation(libs.bundles.accompanist)
                implementation(libs.gson)
            }
        }
    }
}

android {
    namespace = "com.github.soulsearching"
    compileSdk = libs.versions.android.compile.sdk.get().toInt()

    defaultConfig {
        applicationId = "com.github.enteraname74.soulsearching"
        minSdk = libs.versions.android.min.sdk.get().toInt()
        targetSdk = libs.versions.android.target.sdk.get().toInt()
        versionCode = libs.versions.android.version.code.get().toInt()
        versionName = libs.versions.application.version.name.get()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    this.buildOutputs.all {
        val variantOutputImpl = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
        val name = "com.github.enteraname74.soulsearching_${libs.versions.application.version.name.get()}.apk"
        variantOutputImpl.outputFileName = name
    }

    buildTypes {
        debug {
            buildConfigField(
                "String",
                "VERSION_NAME",
                "\"" + libs.versions.application.version.name.get() + "-dev" + "\""
            )
            manifestPlaceholders["appName"] = "SSDDebug"
            versionNameSuffix = "-dev"
            applicationIdSuffix = ".dev"
        }
        create("dev-release") {
            buildConfigField(
                "String",
                "VERSION_NAME",
                "\"" + libs.versions.application.version.name.get() + "-dev.release" + "\""
            )
            manifestPlaceholders["appName"] = "SSDRelease"
            versionNameSuffix = "-dev.release"
            applicationIdSuffix = ".dev.release"
            signingConfig = signingConfigs.getByName("debug")
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        release {
            buildConfigField(
                "String",
                "VERSION_NAME",
                "\"" + libs.versions.application.version.name.get() + "\""
            )
            manifestPlaceholders["appName"] = "Soul Searching"
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}