plugins {
    id("soulsearching.kmp.compose")
}

group = "com.github.enteraname74.soulsearching.coreui"
description = "Core UI elements of the application"

kotlin {
    android.namespace = "com.github.enteraname74.soulsearching.coreui"
    android.androidResources.enable = true

    sourceSets {
        commonMain.dependencies {
            implementation(libs.androidx.annotation)

            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.resources)
            implementation(libs.compose.ui)
            implementation(libs.compose.navigationevent)

            implementation(libs.kmpalette)

            implementation(libs.markdown.renderer.core)
            implementation(libs.markdown.renderer.m3)


            implementation(project(":domain"))
        }

        androidMain.dependencies {
            implementation(libs.bundles.androidx)
            implementation(libs.bundles.accompanist)
            implementation(libs.compose.cloudy)
        }
    }
}

compose.resources {
    publicResClass = true
    nameOfResClass = "CoreRes"
    generateResClass = always
}
