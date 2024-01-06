import com.github.enteraname74.buildsrc.AndroidConfig
import com.github.enteraname74.buildsrc.Dependencies

plugins {
    kotlin("kapt")
    id("com.android.library")
    kotlin("android")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = AndroidConfig.DATA_NAMESPACE
    compileSdk = AndroidConfig.COMPILE_SDK

    defaultConfig {
        minSdk = AndroidConfig.MIN_SDK
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

dependencies {
    implementation(project(mapOf("path" to ":domain")))
    implementation(Dependencies.AndroidX.CORE)
    implementation(Dependencies.AndroidX.ROOM)
    implementation(Dependencies.Google.HILT_ANDROID)

    kapt(Dependencies.Google.HILT_COMPILER)

    // For instrumentation tests
    androidTestImplementation(Dependencies.Google.HILT_ANDROID_TESTING)
    kaptAndroidTest(Dependencies.Google.HILT_COMPILER)

    // For local unit tests
    testImplementation(Dependencies.Google.HILT_ANDROID_TESTING)
    kaptTest(Dependencies.Google.HILT_COMPILER)
}