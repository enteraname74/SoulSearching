import com.github.enteraname74.buildsrc.Config
import com.github.enteraname74.buildsrc.Dependencies

plugins {
    id("com.android.library")
    id("kotlin-android")
    kotlin("kapt")
}

android {
    namespace = Config.DATA_NAMESPACE
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
    implementation(project(mapOf("path" to ":domain")))

    with(Dependencies.AndroidX) {
        implementation(CORE)
        implementation(ROOM)
        kapt(ROOM_COMPILER)
    }

    with(Dependencies.Google) {
        implementation(HILT_ANDROID)
        kapt(HILT_COMPILER)

        // For instrumentation tests
        androidTestImplementation(HILT_ANDROID_TESTING)
        kaptAndroidTest(HILT_COMPILER)

        // For local unit tests
        testImplementation(HILT_ANDROID_TESTING)
        kaptTest(HILT_COMPILER)
    }
}