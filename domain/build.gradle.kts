plugins {
    id("soulsearching.kmp.compose")
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    android.namespace = "com.github.enteraname74.soulsearching.domain"
    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.ui)
            implementation(libs.androidx.paging.common)
            implementation(libs.koin.core)
            implementation(libs.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
        }

        commonTest.dependencies {
            implementation(project(":serialization"))
            implementation(libs.kotlin.test)
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
