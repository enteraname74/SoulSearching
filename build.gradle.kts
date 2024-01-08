buildscript {
    repositories {
        mavenCentral()
        google()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.2.1")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.50")
    }
}


// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Necessary plugins for compose multiplatform (android and desktop)
//    id("com.android.application") version "8.2.1" apply false
    id("com.android.library") version "8.2.1" apply false
    id("org.jetbrains.compose") version "1.5.11" apply false
    id("org.jetbrains.kotlin.multiplatform") version "1.9.21" apply false
}