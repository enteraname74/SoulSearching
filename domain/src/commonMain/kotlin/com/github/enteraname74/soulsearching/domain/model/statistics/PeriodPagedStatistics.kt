package com.github.enteraname74.soulsearching.domain.model.statistics

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

sealed interface PeriodPagedStatistics {
    val period: Period
    val mostListenedMusics: Flow<PagingData<ListeningStatistics.MusicStats>>
    val mostPlayedMusics: Flow<PagingData<ListeningStatistics.MusicStats>>
    val mostPlayedArtists: Flow<PagingData<ListeningStatistics.ArtistStats>>
    val mostPlayedAlbums: Flow<PagingData<ListeningStatistics.AlbumStats>>
    val mostPlayedPlaylists: Flow<PagingData<ListeningStatistics.PlaylistStats>>
    val mostPlayedCollections: Flow<PagingData<ListeningStatistics.CollectionStats>>

    data class Specific(
        override val period: Period.Specific,
        override val mostListenedMusics: Flow<PagingData<ListeningStatistics.MusicStats>>,
        override val mostPlayedMusics: Flow<PagingData<ListeningStatistics.MusicStats>>,
        override val mostPlayedArtists: Flow<PagingData<ListeningStatistics.ArtistStats>>,
        override val mostPlayedAlbums: Flow<PagingData<ListeningStatistics.AlbumStats>>,
        override val mostPlayedPlaylists: Flow<PagingData<ListeningStatistics.PlaylistStats>>,
        override val mostPlayedCollections: Flow<PagingData<ListeningStatistics.CollectionStats>>,
    ) : PeriodPagedStatistics

    data class All(
        override val period: Period,
        override val mostListenedMusics: Flow<PagingData<ListeningStatistics.MusicStats>>,
        override val mostPlayedMusics: Flow<PagingData<ListeningStatistics.MusicStats>>,
        override val mostPlayedArtists: Flow<PagingData<ListeningStatistics.ArtistStats>>,
        override val mostPlayedAlbums: Flow<PagingData<ListeningStatistics.AlbumStats>>,
        override val mostPlayedPlaylists: Flow<PagingData<ListeningStatistics.PlaylistStats>>,
        override val mostPlayedCollections: Flow<PagingData<ListeningStatistics.CollectionStats>>,
        val artistsWithMostMusics: Flow<PagingData<ListeningStatistics.ArtistStats>>,
    ) : PeriodPagedStatistics
}
