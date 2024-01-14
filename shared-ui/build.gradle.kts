import com.github.enteraname74.buildsrc.Config
import com.github.enteraname74.buildsrc.Dependencies
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("com.android.application")
    id("org.jetbrains.compose")
}

kotlin {

    androidTarget()
    jvm("desktop")

    compilerOptions {
        // Common compiler options applied to all Kotlin source sets for expect / actual implementations
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    sourceSets {
        val desktopMain by getting {
            dependencies {
                implementation(project(":local-db"))
                implementation(compose.desktop.common)
                implementation(Dependencies.AndroidX.RUNTIME_DESKTOP)
                implementation("org.jetbrains.compose.desktop:desktop:1.6.0-alpha01")
            }
        }
        commonMain {
            dependencies {
                implementation(project(":domain"))
                implementation(Dependencies.KOIN_CORE)
                implementation(Dependencies.KOIN_COMPOSE)

                implementation(Dependencies.KMPALETTE_CORE)
                implementation("org.jetbrains.compose.components:components-resources:1.5.11")
                implementation("com.russhwolf:multiplatform-settings:1.1.1")

                with(Dependencies.AndroidX) {
                    implementation(RUNTIME)
                    implementation(FOUNDATION)
                    implementation(UI)
                    implementation(MATERIAL)
                    implementation(MATERIAL3)
                    implementation(MATERIAL_ICON_EXTENDED)
                }
            }
        }
        androidMain {
            dependencies {
                implementation(project(":local-db"))

                with(Dependencies.AndroidX) {
                    implementation(KOIN_COMPOSE)
                    implementation(ACTIVITY_COMPOSE)
                    implementation(CORE)
                    implementation(LIFECYCLE)
                    implementation(UI)
                    implementation(UI_TOOLING)
                    implementation(ROOM)
                    implementation(APPCOMPAT_RESOURCES)
                    implementation(APPCOMPAT)
                    implementation(NAVIGATION_COMPOSE)
                    implementation(MEDIA)
                }

                with(Dependencies.Google) {
                    implementation(ACCOMPANIST_PAGER)
                    implementation(ACCOMPANIST_PAGER_INDICATORS)
                    implementation(GSON)
                    implementation(ACCOMPANIST_SYSTEMUICONTROLLER)
                }
            }
        }
    }
}

android {
    namespace = Config.APP_NAMESPACE
    compileSdk = Config.COMPILE_SDK

    defaultConfig {
        applicationId = Config.APP_ID
        minSdk = Config.MIN_SDK
        targetSdk = Config.TARGET_SDK
        versionCode = Config.VERSION_CODE
        versionName = Config.ANDROID_VERSION_NAME

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    this.buildOutputs.all {
        val variantOutputImpl = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
        val name = "${Config.APP_ID}_${Config.ANDROID_VERSION_NAME}.apk"
        variantOutputImpl.outputFileName = name
    }

    buildTypes {
        debug {
            buildConfigField(
                "String",
                "VERSION_NAME",
                "\"" + Config.ANDROID_VERSION_NAME + "-dev" + "\""
            )
            manifestPlaceholders["appName"] = "Soul Searching Dev"
            versionNameSuffix = "-dev"
            applicationIdSuffix = ".dev"
        }

        release {
            buildConfigField("String", "VERSION_NAME", "\"" + Config.ANDROID_VERSION_NAME + "\"")
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
        kotlinCompilerExtensionVersion = "1.5.5"
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
            packageVersion = Config.DESKTOP_VERSION_NAME
        }
    }
}