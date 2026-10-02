plugins {
    id("soulsearching.kmp.base")
}

group = "com.github.enteraname74.soulsearching.shareddi"
description = "Shared DI elements of the application"

kotlin {
    android.namespace = "com.github.enteraname74.soulsearching.shareddi"

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
            implementation(libs.koin.core)
            implementation(libs.coroutines.core)

            implementation(project(":domain"))
            implementation(project(":repository"))
            implementation(project(":remote"))
            implementation(project(":playback"))
            implementation(project(":filemanager"))
            implementation(project(":musicmanager"))
            implementation(project(":local"))

        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
