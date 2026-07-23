plugins {
    id("soulsearching.android.application")
}

android {
    namespace = "com.github.soulsearching"
    defaultConfig {
        applicationId = "com.github.enteraname74.soulsearching"
        versionCode = 38
        versionName = "0.16.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        debug {
            manifestPlaceholders["appName"] = "SSDDebug"
            versionNameSuffix = "-dev"
            applicationIdSuffix = ".dev"
        }
        create("dev-release") {
            manifestPlaceholders["appName"] = "SSDRelease"
            versionNameSuffix = "-dev.release"
            applicationIdSuffix = ".dev.release"
            signingConfig = signingConfigs.getByName("debug")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        release {
            manifestPlaceholders["appName"] = "Soul Searching"
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

}

dependencies {
    implementation(project(":app"))
    implementation(project(":core-ui"))
    implementation(project(":domain"))
    implementation(project(":playback"))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.compose.ui)
    implementation(libs.coroutines.core)
    implementation(libs.jaudiotagger)
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.androidx.workmanager)
    implementation(libs.coil)
}
