pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
rootProject.name = "SoulSearching"
include(":domain")
include(":local-android")
include(":shared-ui")
include(":desktopApp")
include(":local-desktop")
include("core-ui")
include("repository")
include("shared-di")
include(":playback")
include(":filemanager")
include(":musicmanager")
include(":serialization")
include("remote")
include(":httpclient")

project(":playback").projectDir = file("features/playback")
project(":filemanager").projectDir = file("features/filemanager")
project(":musicmanager").projectDir = file("features/musicmanager")
project(":serialization").projectDir = file("features/serialization")
project(":httpclient").projectDir = file("features/httpclient")
