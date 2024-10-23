plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvm("desktop")

    sourceSets {
        val desktopMain by getting {
            dependencies {
                implementation(project(":domain"))
                implementation(project(":repository"))
                implementation(project(":features:filemanager"))
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