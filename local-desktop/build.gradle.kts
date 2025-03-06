plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    jvm("desktop")

    sourceSets {
        val desktopMain by getting {
            dependencies {
                implementation(project(":domain"))
                implementation(project(":repository"))
                implementation(project(":filemanager"))
                implementation(libs.koin.core)
                implementation(libs.bundles.exposed)
                implementation(libs.sqlite.jdbc)
            }
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}