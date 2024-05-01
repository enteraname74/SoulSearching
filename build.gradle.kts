buildscript {
    repositories {
        mavenCentral()
        google()
    }
    dependencies {
        classpath(libs.gradle)
    }
}


// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Necessary plugins for compose multiplatform (android and desktop)
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.org.jetbrains.kotlin.jvm) apply false
    kotlin("plugin.serialization") version "1.9.22"
}
repositories {
    google()
}
