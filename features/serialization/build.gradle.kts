plugins {
    id("soulsearching.kmp.base")
    alias(libs.plugins.kotlinSerialization)
}

group = "com.github.enteraname74.soulsearching.features.serialization"
description = "Serialization logic for the app"

kotlin {
    android.namespace = "com.github.enteraname74.soulsearching.serialization"
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.json)
        }
    }
}
