import com.github.enteraname74.buildsrc.Dependencies
import com.github.enteraname74.buildsrc.Versions

plugins {
    id("org.jetbrains.kotlin.multiplatform")
}

kotlin {
    jvm("desktop")

    sourceSets {
        val desktopMain by getting {
            dependencies {
                implementation(project(mapOf("path" to ":domain")))
                implementation(Dependencies.KOIN_CORE)
                implementation("org.jetbrains.exposed:exposed-core:${Versions.EXPOSED}")
                implementation("org.jetbrains.exposed:exposed-crypt:${Versions.EXPOSED}")
                implementation("org.jetbrains.exposed:exposed-dao:${Versions.EXPOSED}")
                implementation("org.jetbrains.exposed:exposed-jdbc:${Versions.EXPOSED}")
                implementation("org.jetbrains.exposed:exposed-java-time:${Versions.EXPOSED}")
            }
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}