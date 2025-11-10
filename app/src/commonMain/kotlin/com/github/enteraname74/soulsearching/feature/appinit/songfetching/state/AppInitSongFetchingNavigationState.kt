package com.github.enteraname74.soulsearching.feature.appinit.songfetching.state

sealed interface AppInitSongFetchingNavigationState {
    data object Idle: AppInitSongFetchingNavigationState
    data object ToMultipleArtists: AppInitSongFetchingNavigationState
}