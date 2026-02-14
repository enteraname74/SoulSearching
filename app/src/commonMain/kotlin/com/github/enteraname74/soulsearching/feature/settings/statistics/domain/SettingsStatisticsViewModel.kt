package com.github.enteraname74.soulsearching.feature.settings.statistics.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.usecase.album.CommonAlbumUseCase
import com.github.enteraname74.domain.usecase.artist.CommonArtistUseCase
import com.github.enteraname74.domain.usecase.music.CommonMusicUseCase
import com.github.enteraname74.domain.usecase.playlist.CommonPlaylistUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus

class SettingsStatisticsViewModel(
    commonArtistUseCase: CommonArtistUseCase,
    commonMusicUseCase: CommonMusicUseCase,
    commonAlbumUseCase: CommonAlbumUseCase,
    commonPlaylistUseCase: CommonPlaylistUseCase,
) : ViewModel() {
    val state: StateFlow<SettingsStatisticsState> = combine(
        commonMusicUseCase.getMostListened(),
        commonAlbumUseCase.getMostListened(),
        commonPlaylistUseCase.getMostListened(),
        commonArtistUseCase.getMostListened(),
        commonArtistUseCase.getArtistsWistMostMusics(),
    ) { mostListenedMusics, mostListenedAlbums, mostListenedPlaylists, mostListenedArtists, artistsWithMostSongs ->
        SettingsStatisticsState(
            mostListenedMusics = mostListenedMusics.map { it.toListenedElement() },
            mostListenedArtists = mostListenedArtists.map { it.toListenedElement() },
            mostListenedPlaylists = mostListenedPlaylists.map { it.toListenedElement() },
            mostListenedAlbums = mostListenedAlbums.map { it.toListenedElement() },
            artistsWithMostSongs = artistsWithMostSongs.map { it.toMostSongsListenedElement() },
        )
    }.stateIn(
        scope = viewModelScope.plus(Dispatchers.IO),
        started = SharingStarted.Eagerly,
        initialValue = SettingsStatisticsState(),
    )

    companion object {
        private const val MAX_TO_SHOW: Int = 11
    }
}