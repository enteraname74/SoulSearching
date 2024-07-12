plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.compose")
}

kotlin {
    jvm("desktop")

    sourceSets {
        val desktopMain by getting {
            dependencies {
                implementation(project(":domain"))
                implementation(project(":repository"))
                implementation(files("exposed-flows-core-0.2.0.jar"))
                implementation(libs.koin.core)
                implementation(libs.bundles.exposed)
                implementation(libs.sqlite.jdbc)
                implementation(compose.ui)
            }
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}