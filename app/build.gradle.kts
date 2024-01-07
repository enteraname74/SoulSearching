import com.github.enteraname74.buildsrc.Dependencies
import com.github.enteraname74.buildsrc.AndroidConfig

plugins {
    id("com.android.application")
    id("kotlin-android")
//    id("org.jetbrains.kotlin.android")
//    id("kotlin-kapt")
//    id("com.google.dagger.hilt.android")
//    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin")
    kotlin("kapt")
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

hilt {
    enableAggregatingTask = true
}

dependencies {

    with(Dependencies.AndroidX) {
        implementation(CORE)
        implementation(LIFECYCLE)
        implementation(ACTIVITY_COMPOSE)
        implementation(UI)
        implementation(UI_TOOLING)
        implementation(MATERIAL3)
        implementation(MATERIAL)
        implementation(MATERIAL_ICON_EXTENDED)
        implementation(ROOM)
        implementation(APPCOMPAT_RESOURCES)
        implementation(APPCOMPAT)
        implementation(NAVIGATION_COMPOSE)
        implementation(PALETTE)
        implementation(MEDIA)

        androidTestImplementation(JUNIT)
        androidTestImplementation(ESPRESSO_CORE)
        androidTestImplementation(UI_TEST_JUNIT4)

        debugImplementation(UI_TOOLING_PREVIEW)
        debugImplementation(UI_TEST_MANIFEST)

        kapt(ROOM_COMPILER)
    }

    with(Dependencies.Google) {
        implementation(ACCOMPANIST_PAGER)
        implementation(ACCOMPANIST_PAGER_INDICATORS)
        implementation(HILT_ANDROID)
        implementation(GSON)
        implementation(ACCOMPANIST_SYSTEMUICONTROLLER)

        kapt(HILT_COMPILER)

        // For instrumentation tests
        androidTestImplementation(HILT_ANDROID_TESTING)
        kaptAndroidTest(HILT_COMPILER)

        // For local unit tests
        testImplementation(HILT_ANDROID_TESTING)
        kaptTest(HILT_COMPILER)
    }

    testImplementation(Dependencies.JUNIT)

    implementation(project(":domain"))
    runtimeOnly(project(":local-android"))
}