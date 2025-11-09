import org.jetbrains.compose.desktop.application.dsl.TargetFormat
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
                implementation(compose.desktop.currentOs)
                implementation(libs.coroutines.core.swing)
                implementation(libs.vlcj)
            }
        }
        commonMain {
            dependencies {
                implementation(project(":domain"))
                implementation(project(":core-ui"))
                implementation(project(":shared-di"))
                implementation(project(":playback"))
                implementation(project(":filemanager"))
                implementation(project(":musicmanager"))
                implementation(project(":serialization"))

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

                implementation(libs.coil)
                implementation(libs.coil.compose)

                implementation(libs.reorderable)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.koin.androidx.compose)
                implementation(libs.bundles.androidx)

                implementation(libs.bundles.accompanist)
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
        versionCode = 32
        versionName = "0.12.2"

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
                "android-proguard-rules.pro"
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
                "android-proguard-rules.pro"
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
    // For F-Droid
    dependenciesInfo {
        // Disables dependency metadata when building APKs.
        includeInApk = false
        // Disables dependency metadata when building Android App Bundles.
        includeInBundle = false
    }
}

compose.desktop {
    application {
        mainClass = "com.github.enteraname74.soulsearching.MainKt"

        val appVersion = libs.versions.application.version.name.get()

        buildTypes.release.proguard {
            configurationFiles.from(project.file("desktop-proguard-rules.pro"))
        }

        nativeDistributions {

            modules(
                "java.instrument",
                "java.management",
                "java.prefs",
                "java.sql",
                "jdk.security.auth",
                "jdk.unsupported"
            )

            targetFormats(TargetFormat.Rpm)

            packageName = "SoulSearching"
            packageVersion = appVersion
            description = "Music player application."

            linux {
                packageName = "SoulSearching"
                packageVersion = appVersion
                appCategory = "AudioVideo;Player;"
                appRelease = "1"
                rpmLicenseType = "GPL-3.0-or-later"
                iconFile.set(project.file("src/commonMain/composeResources/drawable/app_icon_bg.png"))
            }

        }
    }
}

tasks {
    register<Tar>("packageTarReleaseDistributable") {
        group = "compose desktop"
        from(named("createReleaseDistributable"))
        archiveBaseName = "soulsearching"
        archiveClassifier = "linux"
        compression = Compression.GZIP
        archiveExtension = "tar.gz"

        val version = libs.versions.application.version.name.get()

        archiveFileName = "soulsearching-$version-linux.tar.gz"
    }

    task("packageFlatpakReleaseDistributable") {
        group = "compose desktop"
        description = "Builds a flatpak and stores it in the build/flatpak folder."
        dependsOn("packageTarReleaseDistributable")

        val appId = "io.github.enteraname74.soulsearching"
        val appVersion = libs.versions.application.version.name.get()

        doLast {
            println("packageFlatpakReleaseDistributable -- INFO -- Building manifest")
            exec {
                commandLine(
                    "flatpak-builder",
                    "--user",
                    "--force-clean",
                    "build-dir",
                    "$appId.yml"
                )
            }
            println("packageFlatpakReleaseDistributable -- INFO -- Creating flatpak executable")
            exec {
                commandLine(
                    "flatpak",
                    "build-export",
                    "repo",
                    "build-dir"
                )
            }
            val outputDir = file("${layout.buildDirectory.get().asFile.absolutePath}/flatpak")
            outputDir.mkdirs()
            println("packageFlatpakReleaseDistributable -- Will install flatpak in: $outputDir")
            exec {
                commandLine(
                    "flatpak",
                    "build-bundle",
                    "repo",
                    "${outputDir.absolutePath}/$appId-$appVersion.flatpak",
                    appId
                )
            }
        }
    }
}