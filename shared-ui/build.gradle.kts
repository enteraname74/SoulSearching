import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    kotlin("multiplatform")
    id("com.android.application")
    id("org.jetbrains.compose")
    kotlin("plugin.serialization")
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

                implementation(libs.koin.core)
                implementation(libs.koin.compose)

                implementation(libs.kotlinx.serialization)

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
            }
        }
        androidMain {
            dependencies {
                implementation(libs.koin.androidx.compose)
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
        versionName = libs.versions.android.version.name.get()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    this.buildOutputs.all {
        val variantOutputImpl = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
        val name = "com.github.enteraname74.soulsearching_${libs.versions.android.version.name.get()}.apk"
        variantOutputImpl.outputFileName = name
    }

    buildTypes {
        debug {
            buildConfigField(
                "String",
                "VERSION_NAME",
                "\"" + libs.versions.android.version.name.get() + "-dev" + "\""
            )
            manifestPlaceholders["appName"] = "Soul Searching Dev"
            versionNameSuffix = "-dev"
            applicationIdSuffix = ".dev"
        }

        release {
            buildConfigField(
                "String",
                "VERSION_NAME",
                "\"" + libs.versions.android.version.name.get() + "\""
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
compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "SoulSearching"
            packageVersion = "1.0.0"
        }
    }
}