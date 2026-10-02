plugins {
    id("soulsearching.kmp.compose")
}

group = "com.github.enteraname74.soulsearching.features.playback"
description = "Playback elements of the application"

kotlin {
    android.namespace = "com.github.enteraname74.soulsearching.features.playback"
    android.androidResources.enable = true
    sourceSets {
        desktopMain.dependencies {
            implementation(libs.dbus)
            implementation(libs.dbus.transport.native.unixsocket)
            implementation(libs.coroutines.core.swing)
            implementation(libs.vlcj)
        }

        commonMain.dependencies {
            implementation(libs.compose.ui)
            implementation(libs.koin.core)

            implementation(libs.androidx.paging.compose)
            implementation(libs.androidx.paging.common)

            implementation(project(":domain"))
            implementation(project(":filemanager"))
        }

        androidMain.dependencies {
            implementation(libs.bundles.androidx)
            implementation(libs.androidx.media3.exoplayer)
            implementation(libs.androidx.media3.datasource)
            implementation(libs.androidx.media3.session)
            implementation(libs.koin.androidx.compose)
            implementation(libs.coroutines.guava)
            //            implementation(libs.ffmpeg)

            implementation(project(":core-ui"))
        }
    }
}
