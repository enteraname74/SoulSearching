import com.github.enteraname74.buildsrc.Config
import com.github.enteraname74.buildsrc.Dependencies
import com.github.enteraname74.buildsrc.Versions
import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency
import org.gradle.kotlin.dsl.kotlin
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("com.android.application")
    id("org.jetbrains.compose")
    id("dagger.hilt.android.plugin")
    kotlin("kapt")
}

kapt {
    correctErrorTypes = true
}

hilt {
    enableAggregatingTask = true
}

kotlin {

    androidTarget()
    jvm("desktop")

    sourceSets {
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
        commonMain {
            dependencies {
                implementation(project(":domain"))
                with(Dependencies.AndroidX) {
                    implementation(RUNTIME)
                    implementation(FOUNDATION)
                    implementation(UI)
                    implementation(MATERIAL)
                    implementation(MATERIAL3)
                }
            }
        }
        androidMain {
            dependencies {
                runtimeOnly(project(":local-android"))

                with(Dependencies.AndroidX) {
                    implementation(ACTIVITY_COMPOSE)
                    implementation(CORE)
                    implementation(LIFECYCLE)
                    implementation(UI)
                    implementation(UI_TOOLING)
                    implementation(MATERIAL3)
                    implementation(MATERIAL)
                    implementation(MATERIAL_ICON_EXTENDED)
                    implementation(ROOM)
                    implementation(APPCOMPAT_RESOURCES)
                    implementation(APPCOMPAT)
                    implementation(NAVIGATION_COMPOSE)
                    implementation(PALETTE)
                    implementation(MEDIA)
                }

                with(Dependencies.Google) {
                    implementation(ACCOMPANIST_PAGER)
                    implementation(ACCOMPANIST_PAGER_INDICATORS)
                    implementation(HILT_ANDROID)
                    implementation(GSON)
                    implementation(ACCOMPANIST_SYSTEMUICONTROLLER)

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
            packageName = "com.github.enteraname74.soulsearching"
            packageVersion = Config.DESKTOP_VERSION_NAME
        }
    }
}