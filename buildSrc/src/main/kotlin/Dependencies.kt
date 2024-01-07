package com.github.enteraname74.buildsrc

/**
 * Object holding all dependencies of the application.
 */
object Dependencies {
    const val JUNIT = "junit:junit:${Versions.JUNIT}"
    const val GRADLE = "com.android.tools.build:gradle:${Versions.GRADLE}"
    const val COROUTINES_CORE = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.COROUTINES_CORE}"
    const val INJECT_API = "jakarta.inject:jakarta.inject-api:${Versions.INJECt_API}"
    object AndroidX {
        const val CORE = "androidx.core:core-ktx:${Versions.AndroidX.CORE}"
        const val LIFECYCLE = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.AndroidX.LIFECYCLE}"
        const val ACTIVITY_COMPOSE = "androidx.activity:activity-compose:${Versions.AndroidX.ACTIVITY_COMPOSE}"
        const val UI = "androidx.compose.ui:ui:${Versions.AndroidX.COMPOSE}"
        const val UI_TOOLING_PREVIEW = "androidx.compose.ui:ui-tooling-preview:${Versions.AndroidX.COMPOSE}"
        const val MATERIAL3 = "androidx.compose.material3:material3:${Versions.AndroidX.MATERIAL3}"
        const val MATERIAL = "androidx.compose.material:material:${Versions.AndroidX.MATERIAL}"
        const val JUNIT = "androidx.test.ext:junit:${Versions.AndroidX.JUNIT}"
        const val ESPRESSO_CORE = "androidx.test.espresso:espresso-core:${Versions.AndroidX.ESPRESSO_CORE}"
        const val UI_TEST_JUNIT4 = "androidx.compose.ui:ui-test-junit4:${Versions.AndroidX.COMPOSE}"
        const val UI_TOOLING = "androidx.compose.ui:ui-tooling:$${Versions.AndroidX.COMPOSE}"
        const val UI_TEST_MANIFEST = "androidx.compose.ui:ui-test-manifest:${Versions.AndroidX.COMPOSE}"
        const val MATERIAL_ICON_EXTENDED = "androidx.compose.material:material-icons-extended:${Versions.AndroidX.COMPOSE}"
        const val ROOM = "androidx.room:room-ktx:${Versions.AndroidX.ROOM}"
        const val ROOM_COMPILER = "androidx.room:room-compiler:${Versions.AndroidX.ROOM}"
        const val APPCOMPAT_RESOURCES = "androidx.appcompat:appcompat-resources:${Versions.AndroidX.APPCOMPAT}"
        const val APPCOMPAT = "androidx.appcompat:appcompat:${Versions.AndroidX.APPCOMPAT}"
        const val NAVIGATION_COMPOSE = "androidx.navigation:navigation-compose:${Versions.AndroidX.NAVIGATION_COMPOSE}"
        const val PALETTE = "androidx.palette:palette-ktx:${Versions.AndroidX.PALETTE}"
        const val MEDIA = "androidx.media:media:${Versions.AndroidX.MEDIA}"
    }

    object Google {
        const val ACCOMPANIST_PAGER = "com.google.accompanist:accompanist-pager:${Versions.Google.ACCOMPANIST}"
        const val ACCOMPANIST_PAGER_INDICATORS = "com.google.accompanist:accompanist-pager-indicators:${Versions.Google.ACCOMPANIST}"
        const val HILT_ANDROID = "com.google.dagger:hilt-android:${Versions.Google.DAGGER}"
        const val HILT_COMPILER = "com.google.dagger:hilt-compiler:${Versions.Google.DAGGER}"
        const val HILT_ANDROID_TESTING = "com.google.dagger:hilt-android-testing:${Versions.Google.DAGGER}"
        const val GSON = "com.google.code.gson:gson:${Versions.Google.GSON}"
        const val ACCOMPANIST_SYSTEMUICONTROLLER = "com.google.accompanist:accompanist-systemuicontroller:${Versions.Google.ACCOMPANIST_SYSTEMUICONTROLLER}"
        const val HILT_ANDROID_GRADLE_PLUGIN = "com.google.dagger:hilt-android-gradle-plugin:${Versions.Google.DAGGER}"
    }
}