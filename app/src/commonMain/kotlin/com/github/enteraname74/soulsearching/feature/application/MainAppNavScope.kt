package com.github.enteraname74.soulsearching.feature.application

interface MainAppNavScope {
    val toSettings: () -> Unit
    val toMainPageDestinationIfNeeded: () -> Unit
}