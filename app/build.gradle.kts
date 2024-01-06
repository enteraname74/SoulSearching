import com.github.enteraname74.buildsrc.Dependencies
import com.github.enteraname74.buildsrc.AndroidConfig

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
//    id("com.google.dagger.hilt.android")
    id("kotlin-parcelize")
}

android {
    namespace = AndroidConfig.APP_NAMESPACE
    compileSdk = AndroidConfig.COMPILE_SDK

    defaultConfig {
        applicationId = AndroidConfig.APP_ID
        minSdk = AndroidConfig.MIN_SDK
        targetSdk = AndroidConfig.TARGET_SDK
        versionCode = AndroidConfig.VERSION_CODE
        versionName = AndroidConfig.VERSION_NAME

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    this.buildOutputs.all {
        val variantOutputImpl = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
        val name = "${AndroidConfig.APP_ID}_${AndroidConfig.VERSION_NAME}.apk"
        variantOutputImpl.outputFileName = name
    }

    buildTypes {
        debug {
            buildConfigField("String", "VERSION_NAME", "\"" + AndroidConfig.VERSION_NAME + "-dev" + "\"")
            manifestPlaceholders["appName"] = "Soul Searching Dev"
            versionNameSuffix = "-dev"
            applicationIdSuffix = ".dev"
        }

        release {
            buildConfigField("String", "VERSION_NAME", "\"" + AndroidConfig.VERSION_NAME + "\"")
            manifestPlaceholders["appName"] = "Soul Searching"
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {

    implementation(Dependencies.AndroidX.CORE)
    implementation(Dependencies.AndroidX.LIFECYCLE)
    implementation(Dependencies.AndroidX.ACTIVITY_COMPOSE)
    implementation(Dependencies.AndroidX.UI)
    implementation(Dependencies.AndroidX.UI_TOOLING)
    implementation(Dependencies.AndroidX.MATERIAL3)
    implementation(Dependencies.AndroidX.MATERIAL)
    implementation(Dependencies.AndroidX.MATERIAL_ICON_EXTENDED)
    implementation(Dependencies.AndroidX.ROOM)
    implementation(Dependencies.AndroidX.APPCOMPAT_RESOURCES)
    implementation(Dependencies.AndroidX.APPCOMPAT)
    implementation(Dependencies.AndroidX.NAVIGATION_COMPOSE)
    implementation(Dependencies.AndroidX.PALETTE)
    implementation(Dependencies.AndroidX.MEDIA)

    implementation(Dependencies.Google.ACCOMPANIST_PAGER)
    implementation(Dependencies.Google.ACCOMPANIST_PAGER_INDICATORS)
    implementation(Dependencies.Google.HILT_ANDROID)
    implementation(Dependencies.Google.GSON)
    implementation(Dependencies.Google.ACCOMPANIST_SYSTEMUICONTROLLER)

    androidTestImplementation(Dependencies.AndroidX.JUNIT)
    androidTestImplementation(Dependencies.AndroidX.ESPRESSO_CORE)
    androidTestImplementation(Dependencies.AndroidX.UI_TEST_JUNIT4)

    debugImplementation(Dependencies.AndroidX.UI_TOOLING_PREVIEW)
    debugImplementation(Dependencies.AndroidX.UI_TEST_MANIFEST)

//    testImplementation(Dependencies.JUNIT)
//    kapt(Dependencies.AndroidX.ROOM_COMPILER)
//    kapt(Dependencies.Google.HILT_COMPILER)
//
//    // For instrumentation tests
//    androidTestImplementation(Dependencies.Google.HILT_ANDROID_TESTING)
//    kaptAndroidTest(Dependencies.Google.HILT_COMPILER)
//
//    // For local unit tests
//    testImplementation(Dependencies.Google.HILT_ANDROID_TESTING)
//    kaptTest(Dependencies.Google.HILT_COMPILER)

    implementation(project(":domain"))
    runtimeOnly(project(":data"))
}