package com.github.enteraname74.localdb.model.listeningstatistics

import androidx.room3.Embedded
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey
import com.github.enteraname74.domain.model.statistics.ListeningStatistics
import com.github.enteraname74.localdb.model.RoomAlbum
import com.github.enteraname74.localdb.model.RoomArtist
import com.github.enteraname74.localdb.model.RoomMusic
import com.github.enteraname74.localdb.model.RoomPlaylist
import kotlin.time.Duration
import kotlin.uuid.Uuid

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = RoomMusic::class,
            parentColumns = ["musicId"],
            childColumns = ["musicId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = RoomPlaylist::class,
            parentColumns = ["playlistId"],
            childColumns = ["playlistId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = RoomAlbum::class,
            parentColumns = ["albumId"],
            childColumns = ["albumId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = RoomArtist::class,
            parentColumns = ["artistId"],
            childColumns = ["artistId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index("musicId"),
        Index("playlistId"),
        Index("albumId"),
        Index("artistId"),
    ]
)
data class RoomListeningStatistics(
    @PrimaryKey
    val id: Uuid,
    val nbPlayed: Int,
    val timeListened: Duration?,
    @Embedded val localMonthYear: RoomLocalMonthYear,
    val musicId: Uuid?,
    val playlistId: Uuid?,
    val albumId: Uuid?,
    val artistId: Uuid?,
)

fun ListeningStatistics.toRoomListeningStatistics(): RoomListeningStatistics =
    RoomListeningStatistics(
        id = id,
        nbPlayed = nbPlayed,
        timeListened = (this as? ListeningStatistics.MusicStats)?.timeListened,
        localMonthYear = localMonthYear.toRoomLocalMonthYear(),
        musicId = (this as? ListeningStatistics.MusicStats)?.music?.musicId,
        playlistId = (this as? ListeningStatistics.PlaylistStats)?.playlist?.id,
        albumId = (this as? ListeningStatistics.AlbumStats)?.album?.id,
        artistId = (this as? ListeningStatistics.ArtistStats)?.artist?.id,
    )