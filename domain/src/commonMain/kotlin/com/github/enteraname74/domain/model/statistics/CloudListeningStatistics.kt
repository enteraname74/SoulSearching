package com.github.enteraname74.domain.model.statistics

import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.uuid.Uuid

@Serializable
data class CloudListeningStatistics(
    val id: String,
    val userId: Uuid,
    val lastUpdateAtMillis: Long,
    val nbPlayed: Int,
    val timeListened: Duration?,
    val localMonthYear: CloudLocalMonthYear,
    val musicId: String?,
    val playlistId: Uuid?,
    val albumId: Uuid?,
    val artistId: Uuid?,
)

fun ListeningStatistics.toCloud(userId: Uuid): CloudListeningStatistics =
    CloudListeningStatistics(
        id = buildCloudId(),
        userId = userId,
        lastUpdateAtMillis = lastUpdatedMillis,
        nbPlayed = nbPlayed,
        timeListened = (this as? ListeningStatistics.MusicStats)?.timeListened,
        localMonthYear = localMonthYear.toCloud(),
        musicId = (this as? ListeningStatistics.MusicStats)?.music?.remoteId,
        playlistId = (this as? ListeningStatistics.PlaylistStats)?.playlist?.remoteId,
        albumId = (this as? ListeningStatistics.AlbumStats)?.album?.remoteId,
        artistId = (this as? ListeningStatistics.ArtistStats)?.artist?.remoteId,
    )

private fun ListeningStatistics.buildCloudId(): String = when (this) {
    is ListeningStatistics.AlbumStats -> "$localMonthYear-${album.remoteId}"
    is ListeningStatistics.ArtistStats -> "$localMonthYear-${artist.remoteId}"
    is ListeningStatistics.MusicStats -> "$localMonthYear-${music.remoteId}"
    is ListeningStatistics.PlaylistStats -> "$localMonthYear-${playlist.remoteId}"
}

