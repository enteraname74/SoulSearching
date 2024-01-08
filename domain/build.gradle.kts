import com.github.enteraname74.buildsrc.Config
import com.github.enteraname74.buildsrc.Dependencies

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.library")
}

kotlin {
    androidTarget()
    jvm("desktop")
    jvmToolchain(17)

    sourceSets {
        commonMain {
            dependencies {
                implementation(Dependencies.COROUTINES_CORE)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                implementation(Dependencies.INJECT_API)
                with(Dependencies.AndroidX) {
                    implementation(RUNTIME)
                    implementation(FOUNDATION)
                    implementation(UI)
                    implementation(MATERIAL)
                    implementation(MATERIAL3)
                }
            }
        }
    }
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

//dependencies {
//    implementation(Dependencies.COROUTINES_CORE)
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
//    implementation(Dependencies.INJECT_API)
//    implementation("androidx.compose.ui:ui-graphics-android:1.5.4")
//}