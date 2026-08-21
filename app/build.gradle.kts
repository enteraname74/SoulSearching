import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.bundling.Compression
import org.gradle.api.tasks.bundling.Tar

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
            implementation(libs.compose.preview)

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

val appId = "io.github.enteraname74.soulsearching"
val appVersion = libs.versions.application.version.name.get()

val manifestFile = layout.projectDirectory.file("$appId.yml")

val flatpakRootDirectory = layout.buildDirectory.dir("flatpak")
val flatpakBuildDirectory = layout.buildDirectory.dir("flatpak/build-dir")
val flatpakRepositoryDirectory = layout.buildDirectory.dir("flatpak/repo")
val flatpakBundleFile = flatpakRootDirectory.map {
    it.file("$appId-$appVersion.flatpak")
}

val packageTarReleaseDistributable =
    tasks.register<Tar>("packageTarReleaseDistributable") {
        group = "compose desktop"
        description = "Creates the Linux release distributable archive."

        dependsOn("createReleaseDistributable")

        from(
            providers.provider {
                tasks.getByName("createReleaseDistributable").outputs.files
            }
        )

        // Gradle 9 normalizes archive file permissions to 0644.
        filesMatching(
            listOf(
                "**/bin/**",
                "**/lib/jspawnhelper",
            )
        ) {
            permissions {
                unix("755")
            }
        }

        archiveBaseName.set("soulsearching")
        archiveClassifier.set("linux")
        archiveExtension.set("tar.gz")
        archiveVersion.set("")
        compression = Compression.GZIP

        destinationDirectory.set(layout.buildDirectory.dir("distributions"))
        archiveFileName.set("soulsearching-$appVersion-linux.tar.gz")
    }

val buildFlatpak =
    tasks.register<Exec>("buildFlatpak") {
        group = "compose desktop"
        description = "Builds the Flatpak application directory."

        dependsOn(packageTarReleaseDistributable)

        inputs.file(manifestFile)
        inputs.file(packageTarReleaseDistributable.flatMap { it.archiveFile })

        workingDir(layout.projectDirectory.asFile)

        doFirst {
            val buildDirectory = flatpakBuildDirectory.get().asFile

            logger.lifecycle("Building Flatpak from: ${manifestFile.asFile}")
            logger.lifecycle("Flatpak build directory: $buildDirectory")

            commandLine(
                "flatpak-builder",
                "--user",
                "--force-clean",
                buildDirectory.absolutePath,
                manifestFile.asFile.absolutePath,
            )
        }
    }

val exportFlatpakRepository =
    tasks.register<Exec>("exportFlatpakRepository") {
        group = "compose desktop"
        description = "Exports the Flatpak build into an OSTree repository."

        dependsOn(buildFlatpak)

        workingDir(layout.projectDirectory.asFile)

        doFirst {
            val repositoryDirectory =
                flatpakRepositoryDirectory.get().asFile

            val buildDirectory =
                flatpakBuildDirectory.get().asFile

            repositoryDirectory.mkdirs()

            logger.lifecycle(
                "Exporting Flatpak repository to: $repositoryDirectory"
            )

            commandLine(
                "flatpak",
                "build-export",
                repositoryDirectory.absolutePath,
                buildDirectory.absolutePath,
            )
        }
    }

tasks.register<Exec>("packageFlatpakReleaseDistributable") {
    group = "compose desktop"
    description =
        "Builds a Flatpak bundle and stores it in build/flatpak."

    dependsOn(exportFlatpakRepository)

    workingDir(layout.projectDirectory.asFile)

    outputs.file(flatpakBundleFile)

    doFirst {
        val repositoryDirectory =
            flatpakRepositoryDirectory.get().asFile

        val bundleFile = flatpakBundleFile.get().asFile

        bundleFile.parentFile.mkdirs()

        // build-bundle may refuse to overwrite an existing bundle.
        if (bundleFile.exists() && !bundleFile.delete()) {
            error("Unable to replace existing bundle: $bundleFile")
        }

        logger.lifecycle("Creating Flatpak bundle: $bundleFile")

        commandLine(
            "flatpak",
            "build-bundle",
            repositoryDirectory.absolutePath,
            bundleFile.absolutePath,
            appId,
        )
    }
}
