import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("soulsearching.kmp.base")
    alias(libs.plugins.androidx.room)
}

apply(plugin = "com.google.devtools.ksp")

kotlin {
    android.namespace = "com.github.enteraname74.soulsearching.data"
    js {
        useEsModules()
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        useEsModules()
    }

    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(libs.androidx.sqlite.bundled)
            }
        }
        val webMain by getting {
            dependencies {
                implementation(libs.androidx.sqlite.web)
                implementation(project(":serialization"))
                implementation(npm("sql-js-worker", layout.projectDirectory.dir("worker").asFile))
            }
        }
        wasmJsMain.dependencies {
            implementation(libs.kotlinx.browser)
        }

        androidMain {
            dependencies {
                implementation(libs.androidx.room.sqlite.wrapper)
            }
        }
        commonMain.dependencies {
            implementation(project(":domain"))
            implementation(project(":repository"))
            implementation(project(":filemanager"))
            implementation(project(":serialization"))
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

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
