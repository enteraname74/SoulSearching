import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("com.android.library")
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
}

kotlin {
    jvmToolchain(17)
    androidTarget()
    jvm("desktop")
    js {
        browser()
        useEsModules()
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        useEsModules()
    }

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        // Common compiler options applied to all Kotlin source sets for expect / actual implementations
        freeCompilerArgs.add("-Xexpect-actual-classes")
        optIn.addAll(
            "kotlin.time.ExperimentalTime",
            "kotlin.uuid.ExperimentalUuidApi"
        )
    }

    sourceSets {
        val commonMain by getting
        val androidMain by getting
        val desktopMain by getting
        val jsMain by getting
        val wasmJsMain by getting
        val jvmMain = maybeCreate("jvmMain").apply {
            dependsOn(commonMain)

            dependencies {
                implementation(libs.androidx.sqlite.bundled)
            }
        }
        val webMain = maybeCreate("webMain").apply {
            dependsOn(commonMain)

            dependencies {
                implementation(libs.androidx.sqlite.web)
                implementation(project(":serialization"))
                implementation(npm("sql-js-worker", layout.projectDirectory.dir("worker").asFile))
            }
        }

        androidMain.dependsOn(jvmMain)
        desktopMain.dependsOn(jvmMain)
        jsMain.dependsOn(webMain)
        wasmJsMain.apply {
            dependsOn(webMain)

            dependencies {
                implementation(libs.kotlinx.browser)
            }
        }

        androidMain {
            dependencies {
                // Used for injecting app context in database module
                implementation(libs.koin.androidx.compose)
                implementation(libs.androidx.room.sqlite.wrapper)
            }
        }
        commonMain.dependencies {
            implementation(project(":domain"))
            implementation(project(":repository"))
            implementation(project(":filemanager"))
            implementation(libs.koin.core)

            implementation(libs.androidx.paging.common)

            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.room.paging)
            implementation(libs.androidx.sqlite.async)
        }
    }
}

room3 {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspDesktop", libs.androidx.room.compiler)
    add("kspJs", libs.androidx.room.compiler)
    add("kspWasmJs", libs.androidx.room.compiler)
}

android {
    namespace = "com.github.enteraname74.soulsearching.data"
    compileSdk = libs.versions.android.compile.sdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.min.sdk.get().toInt()
    }

    buildTypes {
        create("dev-release")
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
