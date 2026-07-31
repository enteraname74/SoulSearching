plugins {
    id("soulsearching.kmp.base")
}

group = "com.github.enteraname74.soulsearching.features.musicmanager"
description = "Music manager elements of the application"

kotlin {
    android.namespace = "com.github.enteraname74.soulsearching.features.musicmanager"
    sourceSets {
        desktopMain.dependencies {
            implementation(libs.coroutines.core.swing)
        }
        commonMain.dependencies {
            implementation(project(":domain"))
            implementation(project(":core-ui"))
            implementation(libs.koin.core)
            implementation(libs.jaudiotagger)
            implementation(libs.coroutines.core)
        }
    }
}
