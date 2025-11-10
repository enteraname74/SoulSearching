package com.github.enteraname74.soulsearching.feature.appinit.songfetching

import cafe.adriel.voyager.core.model.ScreenModel
import com.github.enteraname74.soulsearching.feature.appinit.songfetching.state.AppInitSongFetchingNavigationState
import com.github.enteraname74.soulsearching.feature.appinit.songfetching.state.AppInitSongFetchingState
import com.github.enteraname74.soulsearching.features.musicmanager.fetching.MusicFetcher
import com.github.enteraname74.soulsearching.features.musicmanager.multipleartists.FetchAllMultipleArtistManagerImpl
import com.github.enteraname74.soulsearching.features.musicmanager.persistence.MusicPersistence
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
            musicFetcher.fetchMusics(
                updateProgress = { progression, folder ->
                    _state.value = AppInitSongFetchingState(
                        currentProgression = progression,
                        currentFolder = folder,
                    )
                }
            )

            val multipleArtistManager = FetchAllMultipleArtistManagerImpl(
                optimizedCachedData = musicFetcher.optimizedCachedData,
            )

            if (multipleArtistManager.doDataHaveMultipleArtists()) {
                _navigationState.value = AppInitSongFetchingNavigationState.ToMultipleArtists
            } else {
                MusicPersistence(optimizedCachedData = musicFetcher.optimizedCachedData).saveAll()
            }
        }
    }

    fun consumeNavigation() {
        _navigationState.value = AppInitSongFetchingNavigationState.Idle
    }
}