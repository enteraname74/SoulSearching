import com.github.enteraname74.buildsrc.Config
import com.github.enteraname74.buildsrc.Dependencies

plugins {
    id("com.android.library")
    id("kotlin-android")
    kotlin("kapt")
}

android {
    namespace = Config.DOMAIN_NAMESPACE
    compileSdk = Config.COMPILE_SDK

    defaultConfig {
        minSdk = Config.MIN_SDK
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
    implementation(Dependencies.COROUTINES_CORE)
    implementation(Dependencies.Google.HILT_ANDROID)
    kapt(Dependencies.Google.HILT_COMPILER)
}