import com.github.enteraname74.buildsrc.Versions
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

group = "com.github.enteraname74.soulsearching"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
    implementation("org.jetbrains.compose.material3:material3-desktop:1.6.0-alpha01")
    implementation("org.jetbrains.compose.material:material-desktop:1.6.0-alpha01")
    implementation("org.jetbrains.compose.foundation:foundation-desktop:${Versions.AndroidX.FOUNDATION}")
    implementation(project(":shared-ui"))
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "SoulSearching"
            packageVersion = "1.0.0"
        }
    }
}
