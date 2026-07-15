plugins {
    id("soulsearching.kmp.base")
}

group = "com.github.enteraname74.soulsearching.repository"
description = "Repository layer of the app"

kotlin {
    android.namespace = "com.github.enteraname74.soulsearching.repository"
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(libs.jaudiotagger)
            }
        }
        commonMain {
            dependencies {
                implementation(libs.compose.ui)
                implementation(libs.androidx.paging.common)
                implementation(libs.koin.core)

                implementation(libs.coroutines.core)

                implementation(project(":domain"))
                implementation(project(":filemanager"))
                implementation(project(":serialization"))
            }
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
