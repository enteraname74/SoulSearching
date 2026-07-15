plugins {
    id("soulsearching.kmp.compose")
}

group = "com.github.enteraname74.soulsearching.features.filemanager"
description = "File manager elements of the application"

kotlin {
    android.namespace = "com.github.enteraname74.soulsearching.features.filemanager"
    sourceSets {
        desktopMain.dependencies {
            implementation(libs.coroutines.core.swing)
        }
        val jvmMain by getting {
            dependencies {
                implementation(libs.jaudiotagger)
            }
        }

        commonMain.dependencies {
            implementation(libs.compose.ui)
            implementation(libs.compose.resources)
            implementation(libs.koin.core)

            implementation(libs.coroutines.core)

            implementation(project(":domain"))
        }

        androidMain.dependencies {
            implementation(libs.bundles.androidx)
        }
    }
}
