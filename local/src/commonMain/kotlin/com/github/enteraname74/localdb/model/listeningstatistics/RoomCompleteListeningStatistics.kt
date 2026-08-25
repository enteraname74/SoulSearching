package com.github.enteraname74.localdb.model.listeningstatistics

import androidx.room3.Embedded
import androidx.room3.Relation
import com.github.enteraname74.domain.model.statistics.ListeningStatistics
import com.github.enteraname74.localdb.model.RoomCompleteMusic
import com.github.enteraname74.localdb.model.RoomMusic
import com.github.enteraname74.localdb.view.RoomAlbumPreview
import com.github.enteraname74.localdb.view.RoomArtistPreview
import com.github.enteraname74.localdb.view.RoomPlaylistPreview
import kotlin.time.Duration

data class RoomCompleteListeningStatistics(
    @Embedded
    val listeningStatistics: RoomListeningStatistics,

    @Relation(
        parentColumns = ["playlistId"],
        entityColumns = ["id"],
        entity = RoomPlaylistPreview::class,
    )
    val playlist: RoomPlaylistPreview?,

    @Relation(
        parentColumns = ["albumId"],
        entityColumns = ["id"],
        entity = RoomAlbumPreview::class,
    )
    val album: RoomAlbumPreview?,

    @Relation(
        parentColumns = ["artistId"],
        entityColumns = ["id"],
        entity = RoomArtistPreview::class,
    )
    val artist: RoomArtistPreview?,

    @Relation(
        parentColumns = ["musicId"],
        entityColumns = ["musicId"],
        entity = RoomMusic::class,
    )
    val music: RoomCompleteMusic?,
) {
    fun toMusicStats(): ListeningStatistics.MusicStats? =
        music?.let {
            ListeningStatistics.MusicStats(
                music = music.toMusic(),
                id = listeningStatistics.id,
                nbPlayed = listeningStatistics.nbPlayed,
                timeListened = listeningStatistics.timeListened ?: Duration.ZERO,
                localMonthYear = listeningStatistics.localMonthYear.toLocalMonthYear(),
            )
        }

    fun toArtistStats(): ListeningStatistics.ArtistStats? =
        artist?.let {
            ListeningStatistics.ArtistStats(
                artist = artist.toArtistPreview(),
                id = listeningStatistics.id,
                nbPlayed = listeningStatistics.nbPlayed,
                localMonthYear = listeningStatistics.localMonthYear.toLocalMonthYear(),
            )
        }

    fun toAlbumStats(): ListeningStatistics.AlbumStats? =
        album?.let {
            ListeningStatistics.AlbumStats(
                album = album.toAlbumPreview(),
                id = listeningStatistics.id,
                nbPlayed = listeningStatistics.nbPlayed,
                localMonthYear = listeningStatistics.localMonthYear.toLocalMonthYear(),
            )
        }

    fun toPlaylistStats(): ListeningStatistics.PlaylistStats? =
        playlist?.let {
            ListeningStatistics.PlaylistStats(
                playlist = playlist.toPlaylistPreview(),
                id = listeningStatistics.id,
                nbPlayed = listeningStatistics.nbPlayed,
                localMonthYear = listeningStatistics.localMonthYear.toLocalMonthYear(),
            )
        }

    fun toListeningStatistics(): ListeningStatistics? =
        when {
            music != null -> toMusicStats()
            playlist != null -> toPlaylistStats()
            album != null -> toAlbumStats()
            artist != null -> toArtistStats()
            else -> null
        }
}
