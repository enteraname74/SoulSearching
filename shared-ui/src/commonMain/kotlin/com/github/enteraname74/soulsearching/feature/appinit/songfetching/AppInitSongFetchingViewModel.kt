package com.github.enteraname74.soulsearching.feature.appinit.songfetching

import cafe.adriel.voyager.core.model.ScreenModel
import com.github.enteraname74.soulsearching.feature.appinit.songfetching.state.AppInitSongFetchingNavigationState
import com.github.enteraname74.soulsearching.feature.appinit.songfetching.state.AppInitSongFetchingState
import com.github.enteraname74.soulsearching.features.filemanager.musicfetching.MusicFetcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppInitSongFetchingViewModel(
    private val musicFetcher: MusicFetcher
) : ScreenModel {
    private val _state: MutableStateFlow<AppInitSongFetchingState> = MutableStateFlow(
        AppInitSongFetchingState(
            currentProgression = 0f,
            currentFolder = null,
        )
    )
    val state: StateFlow<AppInitSongFetchingState> = _state.asStateFlow()

    private val _navigationState: MutableStateFlow<AppInitSongFetchingNavigationState> = MutableStateFlow(
        AppInitSongFetchingNavigationState.Idle
    )
    val navigationState: StateFlow<AppInitSongFetchingNavigationState> = _navigationState.asStateFlow()

    fun fetchSongs() {
        CoroutineScope(Dispatchers.IO).launch {
            val hasSongsBeenSaved = musicFetcher.fetchMusics(
                updateProgress = { progression, folder ->
                    _state.value = AppInitSongFetchingState(
                        currentProgression = progression,
                        currentFolder = folder,
                    )
                }
            )

            if (!hasSongsBeenSaved) {
                _navigationState.value = AppInitSongFetchingNavigationState.ToMultipleArtists
            }
        }
    }

    fun consumeNavigation() {
        _navigationState.value = AppInitSongFetchingNavigationState.Idle
    }
}