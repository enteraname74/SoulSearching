plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.github.soulsearching"
    compileSdk = libs.versions.android.compile.sdk.get().toInt()

    defaultConfig {
        applicationId = "com.github.enteraname74.soulsearching"
        minSdk = libs.versions.android.min.sdk.get().toInt()
        targetSdk = libs.versions.android.target.sdk.get().toInt()
        versionCode = 36
        versionName = libs.versions.application.version.name.get()
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
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                project(":app").file("android-proguard-rules.pro")
            )
        }
        release {
            manifestPlaceholders["appName"] = "Soul Searching"
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                project(":app").file("android-proguard-rules.pro")
            )
        }
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
