import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("soulsearching.kmp.compose")
    alias(libs.plugins.kotlinSerialization)
}

group = "com.github.enteraname74.soulsearching"
description = "Application's elements"

kotlin {
    android.namespace = "com.github.enteraname74.soulsearching.sharedapp"
    android.androidResources.enable = true

    js {
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        binaries.executable()
    }

    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(libs.jaudiotagger)
            }
        }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.coroutines.core.swing)
            implementation(libs.vlcj)
        }
        commonMain.dependencies {
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
            implementation(libs.multiplatform.settings)

            implementation(libs.compose.foundation)
            implementation(libs.compose.material)
            implementation(libs.compose.material3)
            implementation(libs.compose.resources)
            implementation(libs.compose.ui)

            implementation(libs.jaudiotagger)
            implementation(libs.androidx.annotation)

            implementation(libs.coroutines.core)

            implementation(libs.file.kit)

            implementation(libs.bundles.coil)

            implementation(libs.reorderable)

            implementation(libs.androidx.paging.compose)
            implementation(libs.androidx.paging.common)

            implementation(libs.navigation3.ui)
            implementation(libs.navigation3.viewmodel)
        }
        androidMain.dependencies {
            implementation(libs.koin.androidx.compose)
            implementation(libs.koin.androidx.workmanager)
            implementation(libs.bundles.androidx)

            implementation(libs.bundles.accompanist)
            // https://mvnrepository.com/artifact/androidx.documentfile/documentfile
            implementation(libs.androidx.documentfile)
        }
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

    register("packageFlatpakReleaseDistributable") {
        group = "compose desktop"
        description = "Builds a flatpak and stores it in the build/flatpak folder."
        dependsOn("packageTarReleaseDistributable")

        val appId = "io.github.enteraname74.soulsearching"
        val appVersion = libs.versions.application.version.name.get()

        doLast {
            println("packageFlatpakReleaseDistributable -- INFO -- Building manifest")
            providers.exec {
                commandLine(
                    "flatpak-builder",
                    "--user",
                    "--force-clean",
                    "build-dir",
                    "$appId.yml"
                )
            }.result.get()
            println("packageFlatpakReleaseDistributable -- INFO -- Creating flatpak executable")
            providers.exec {
                commandLine(
                    "flatpak",
                    "build-export",
                    "repo",
                    "build-dir"
                )
            }.result.get()
            val outputDir = file("${layout.buildDirectory.get().asFile.absolutePath}/flatpak")
            outputDir.mkdirs()
            println("packageFlatpakReleaseDistributable -- Will install flatpak in: $outputDir")
            providers.exec {
                commandLine(
                    "flatpak",
                    "build-bundle",
                    "repo",
                    "${outputDir.absolutePath}/$appId-$appVersion.flatpak",
                    appId
                )
            }.result.get()
        }
    }
}
