plugins {
    id("soulsearching.kmp.base")
    alias(libs.plugins.kotlinSerialization)
}

group = "com.github.enteraname74.soulsearching.remote"
description = "Remote data access"

kotlin {
    android.namespace = "com.github.enteraname74.soulsearching.remote"
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(libs.ktor.client.cio)
            }
        }

        commonMain.dependencies {
            implementation(project(":domain"))
            implementation(project(":repository"))
            implementation(project(":filemanager"))
            implementation(libs.ktor.client.auth)
            implementation(libs.ktor.client.content.negoctiation)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.resources)
            implementation(libs.ktor.client.websockets)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.serialization.logging)
            implementation(libs.koin.core)
            implementation(libs.kotlinx.serialization.json)
        }
    }
}
