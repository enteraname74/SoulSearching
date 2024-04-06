package com.github.enteraname74.buildsrc

/**
 * Object holding all dependencies of the application.
 */
object Dependencies {
    const val COROUTINES_CORE = "org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:${Versions.COROUTINES_CORE}"
    const val KOIN_CORE = "io.insert-koin:koin-core:${Versions.KOIN}"
    const val KOIN_COMPOSE = "io.insert-koin:koin-compose:${Versions.KOIN_COMPOSE}"
    const val KMPALETTE_CORE = "com.kmpalette:kmpalette-core:${Versions.KMPALETTE}"

    object Voyager {
        const val NAVIGATOR = "cafe.adriel.voyager:voyager-navigator:${Versions.VOYAGER}"
        const val SCREEN_MODEL = "cafe.adriel.voyager:voyager-screenmodel:${Versions.VOYAGER}"
        const val TRANSITIONS = "cafe.adriel.voyager:voyager-transitions:${Versions.VOYAGER}"
        const val KOIN = "cafe.adriel.voyager:voyager-koin:${Versions.VOYAGER}"
    }

    object AndroidX {
        const val KOIN_COMPOSE = "io.insert-koin:koin-androidx-compose:${Versions.KOIN}"
        const val CORE = "androidx.core:core-ktx:${Versions.AndroidX.CORE}"
        const val LIFECYCLE = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.AndroidX.LIFECYCLE}"
        const val ACTIVITY_COMPOSE = "androidx.activity:activity-compose:${Versions.AndroidX.ACTIVITY_COMPOSE}"
        const val UI = "androidx.compose.ui:ui:${Versions.AndroidX.UI}"
        const val UI_TOOLING = "androidx.compose.ui:ui-tooling:${Versions.AndroidX.UI}"
        const val ROOM = "androidx.room:room-ktx:${Versions.AndroidX.ROOM}"
        const val APPCOMPAT_RESOURCES = "androidx.appcompat:appcompat-resources:${Versions.AndroidX.APPCOMPAT}"
        const val APPCOMPAT = "androidx.appcompat:appcompat:${Versions.AndroidX.APPCOMPAT}"
        const val NAVIGATION_COMPOSE = "androidx.navigation:navigation-compose:${Versions.AndroidX.NAVIGATION_COMPOSE}"
        const val MEDIA = "androidx.media:media:${Versions.AndroidX.MEDIA}"
    }

    object Google {
        const val ACCOMPANIST_PAGER = "com.google.accompanist:accompanist-pager:${Versions.Google.ACCOMPANIST}"
        const val ACCOMPANIST_PAGER_INDICATORS = "com.google.accompanist:accompanist-pager-indicators:${Versions.Google.ACCOMPANIST}"
        const val GSON = "com.google.code.gson:gson:${Versions.Google.GSON}"
        const val ACCOMPANIST_SYSTEMUICONTROLLER = "com.google.accompanist:accompanist-systemuicontroller:${Versions.Google.ACCOMPANIST_SYSTEMUICONTROLLER}"
    }
}