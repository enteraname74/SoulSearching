plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    compileSdk = 37

    defaultConfig {
        minSdk = 26
        targetSdk = 36
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    packaging.resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"

    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }
}
