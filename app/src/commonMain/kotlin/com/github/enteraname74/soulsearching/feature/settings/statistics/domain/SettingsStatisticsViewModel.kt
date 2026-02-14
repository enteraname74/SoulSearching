package com.github.enteraname74.soulsearching.feature.settings.statistics.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.ext.getFirstsOrMax
import com.github.enteraname74.domain.model.SortDirection
import com.github.enteraname74.domain.model.SortType
import com.github.enteraname74.domain.usecase.album.CommonAlbumUseCase
import com.github.enteraname74.domain.usecase.artist.CommonArtistUseCase
import com.github.enteraname74.domain.usecase.music.CommonMusicUseCase
import com.github.enteraname74.domain.usecase.playlist.GetAllPlaylistWithMusicsSortedUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus

class SettingsStatisticsViewModel(
    getAllPlaylistWithMusicsSortedUseCase: GetAllPlaylistWithMusicsSortedUseCase,
    commonArtistUseCase: CommonArtistUseCase,
    commonMusicUseCase: CommonMusicUseCase,
    commonAlbumUseCase: CommonAlbumUseCase,
) : ViewModel() {
    val state: StateFlow<SettingsStatisticsState> = combine(
        commonMusicUseCase.getMostListened(),
        commonAlbumUseCase.getMostListened(),
        getAllPlaylistWithMusicsSortedUseCase(
            sortDirection = SortDirection.DESC,
            sortType = SortType.NB_PLAYED,
        ),
        commonArtistUseCase.getMostListened(),
        commonArtistUseCase.getArtistsWistMostMusics(),
    ) { mostListenedMusics, mostListenedAlbums, allPlaylists, mostListenedArtists, artistsWithMostSongs ->
        SettingsStatisticsState(
            mostListenedMusics = mostListenedMusics.map { it.toListenedElement() },
            mostListenedArtists = mostListenedArtists
                .map { it.toListenedElement() },
            mostListenedPlaylists = allPlaylists
                .getFirstsOrMax(MAX_TO_SHOW)
                .filter { it.playlist.nbPlayed >= 1 }
                .map { it.toListenedElement() },
            mostListenedAlbums = mostListenedAlbums.map { it.toListenedElement() },
            artistsWithMostSongs = artistsWithMostSongs
                .map { it.toMostSongsListenedElement() },
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