package com.github.enteraname74.soulsearching.feature.settings.statistics.domain

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.SortDirection
import com.github.enteraname74.domain.model.SortType
import com.github.enteraname74.domain.usecase.album.GetAllAlbumWithMusicsSortedUseCase
import com.github.enteraname74.domain.usecase.artist.GetAllArtistWithMusicsSortedUseCase
import com.github.enteraname74.domain.usecase.music.GetAllMusicsSortedUseCase
import com.github.enteraname74.domain.usecase.playlist.GetAllPlaylistWithMusicsSortedUseCase
import com.github.enteraname74.soulsearching.feature.player.ext.getFirstsOrMax
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus

class SettingsStatisticsViewModel(
    getAllMusicsSortedUseCase: GetAllMusicsSortedUseCase,
    getAllAlbumWithMusicsSortedUseCase: GetAllAlbumWithMusicsSortedUseCase,
    getAllPlaylistWithMusicsSortedUseCase: GetAllPlaylistWithMusicsSortedUseCase,
    getAllArtistWithMusicsSortedUseCase: GetAllArtistWithMusicsSortedUseCase,
): ScreenModel {
    val state: StateFlow<SettingsStatisticsState> = combine(
        getAllMusicsSortedUseCase(
            sortDirection = SortDirection.DESC,
            sortType = SortType.NB_PLAYED,
        ),
        getAllAlbumWithMusicsSortedUseCase(
            sortDirection = SortDirection.DESC,
            sortType = SortType.NB_PLAYED,
        ),
        getAllPlaylistWithMusicsSortedUseCase(
            sortDirection = SortDirection.DESC,
            sortType = SortType.NB_PLAYED,
        ),
        getAllArtistWithMusicsSortedUseCase(
            sortDirection = SortDirection.DESC,
            sortType = SortType.NB_PLAYED,
        ),
    ) { allMusics, allAlbums, allPlaylists, allArtists ->
        SettingsStatisticsState(
            mostListenedMusics = allMusics.getFirstsOrMax(MAX_TO_SHOW).map { it.toListenedElement() },
            mostListenedArtists = allArtists.getFirstsOrMax(MAX_TO_SHOW).map { it.toListenedElement() },
            mostListenedPlaylists = allPlaylists.getFirstsOrMax(MAX_TO_SHOW).map { it.toListenedElement() },
            mostListenedAlbums = allAlbums.getFirstsOrMax(MAX_TO_SHOW).map { it.toListenedElement() },
        )
    }.stateIn(
        scope = screenModelScope.plus(Dispatchers.IO),
        started = SharingStarted.Eagerly,
        initialValue = SettingsStatisticsState(),
    )

    companion object {
        private const val MAX_TO_SHOW: Int = 5
    }
}