plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
}

kotlin {
    androidTarget()
    jvm("desktop")
    jvmToolchain(17)

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.koin.core)
                implementation(libs.coroutines.core)
                implementation(libs.coroutines.core.jvm)
                implementation(compose.ui)
                implementation(libs.jaudiotagger)
            }
        }
    }
}

android {
    namespace = "com.github.enteraname74.soulsearching"
    compileSdk = libs.versions.android.compile.sdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.min.sdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}