import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    id("com.android.kotlin.multiplatform.library")
}

kotlin {
    jvmToolchain(21)

    android {
        compileSdk = 37
        minSdk = 26
        compilerOptions.jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }

    jvm("desktop")
    js {
        browser()
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    sourceSets {
        val commonMain by getting
        val androidMain by getting
        val desktopMain by getting
        val jsMain by getting
        val wasmJsMain by getting

        val nonAndroidMain by creating {
            dependsOn(commonMain)
        }
        val jvmMain by creating {
            dependsOn(commonMain)
        }
        val webMain by creating {
            dependsOn(nonAndroidMain)
        }

        androidMain.dependsOn(jvmMain)
        desktopMain.dependsOn(jvmMain)
        desktopMain.dependsOn(nonAndroidMain)
        jsMain.dependsOn(webMain)
        wasmJsMain.dependsOn(webMain)
    }

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
        optIn.addAll(
            "kotlin.time.ExperimentalTime",
            "kotlin.uuid.ExperimentalUuidApi",
        )
    }
}
