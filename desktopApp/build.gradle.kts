import org.gradle.configurationcache.DefaultConfigurationCache
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.compose.internal.de.undercouch.gradle.tasks.download.org.apache.hc.core5.net.Host

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

group = "com.github.enteraname74.soulsearching.desktopapp"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
    implementation(libs.material3.desktop)
    implementation(libs.material.desktop)
    implementation(libs.foundation.desktop)
    implementation(compose.components.resources)
    implementation(project(":shared-ui"))

    implementation(libs.koin.core)
    implementation(libs.koin.compose)
}

compose.desktop {
    application {
        mainClass = "MainKt"

        buildTypes.release.proguard {
            configurationFiles.from(project.file("proguard-rules.pro"))
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
            packageVersion = libs.versions.desktop.version.name.get()
            description = "Music player application."

            linux {

                packageName = "SoulSearching"
                packageVersion = libs.versions.desktop.version.name.get()
                appCategory = "AudioVideo;Player;"
                appRelease = "1"
                rpmLicenseType = "GPL-3.0-or-later"
                iconFile.set(project.file("src/main/composeResources/drawable/app_icon.png"))
            }

        }
    }
}
