package com.github.enteraname74.soulsearching.feature.mainpage.domain.state

sealed interface ApplicationState {
    data object FetchingSongs: ApplicationState
    data object AppMigration: ApplicationState
    data object Data: ApplicationState
}