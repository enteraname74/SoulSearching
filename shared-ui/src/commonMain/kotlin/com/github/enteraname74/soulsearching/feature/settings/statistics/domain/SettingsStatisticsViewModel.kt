package com.github.enteraname74.soulsearching.feature.settings.statistics.domain

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.ext.getFirstsOrMax
import com.github.enteraname74.domain.model.SortDirection
import com.github.enteraname74.domain.model.SortType
import com.github.enteraname74.domain.usecase.album.GetAllAlbumWithMusicsSortedUseCase
import com.github.enteraname74.domain.usecase.artist.GetAllArtistWithMusicsSortedByMostSongsUseCase
import com.github.enteraname74.domain.usecase.artist.GetAllArtistWithMusicsSortedUseCase
import com.github.enteraname74.domain.usecase.music.GetAllMusicsSortedUseCase
import com.github.enteraname74.domain.usecase.playlist.GetAllPlaylistWithMusicsSortedUseCase
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
    getAllArtistWithMusicsSortedByMostSongsUseCase: GetAllArtistWithMusicsSortedByMostSongsUseCase,
) : ScreenModel {
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
        getAllArtistWithMusicsSortedByMostSongsUseCase(),
    ) { allMusics, allAlbums, allPlaylists, allArtists, allArtistsWithMostSongs ->
        SettingsStatisticsState(
            mostListenedMusics = allMusics
                .getFirstsOrMax(MAX_TO_SHOW)
                .filter { it.nbPlayed >= 1 }
                .map { it.toListenedElement() },
            mostListenedArtists = allArtists
                .getFirstsOrMax(MAX_TO_SHOW)
                .filter { it.artist.nbPlayed >= 1 }
                .map { it.toListenedElement() },
            mostListenedPlaylists = allPlaylists
                .getFirstsOrMax(MAX_TO_SHOW)
                .filter { it.playlist.nbPlayed >= 1 }
                .map { it.toListenedElement() },
            mostListenedAlbums = allAlbums
                .getFirstsOrMax(MAX_TO_SHOW)
                .filter { it.album.nbPlayed >= 1 }
                .map { it.toListenedElement() },
            artistsWithMostSongs = allArtistsWithMostSongs
                .getFirstsOrMax(MAX_TO_SHOW)
                .map { it.toMostSongsListenedElement() },
        )
    }.stateIn(
        scope = screenModelScope.plus(Dispatchers.IO),
        started = SharingStarted.Eagerly,
        initialValue = SettingsStatisticsState(),
    )

    companion object {
        private const val MAX_TO_SHOW: Int = 11
    }
}