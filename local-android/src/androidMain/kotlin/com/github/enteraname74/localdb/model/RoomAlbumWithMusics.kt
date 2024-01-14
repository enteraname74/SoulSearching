package com.github.enteraname74.localdb.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.github.enteraname74.domain.model.AlbumWithMusics

/**
 * Room representation of an AlbumWithMusics.
 */
internal data class RoomAlbumWithMusics(
    @Embedded val roomAlbum: RoomAlbum = RoomAlbum(),
    @Relation(
        parentColumn = "albumId",
        entityColumn = "musicId",
        associateBy = Junction(RoomMusicAlbum::class)
    )
    val roomMusics : List<RoomMusic> = emptyList(),
    @Relation(
        parentColumn = "albumId",
        entityColumn = "artistId",
        associateBy = Junction(RoomAlbumArtist::class)
    )
    val roomArtist: RoomArtist? = RoomArtist()
) {
    /**
     * Converts a RoomAlbumWithMusics to a RoomAlbumWithArtist.
     */
    fun toAlbumWithArtist() : RoomAlbumWithArtist = RoomAlbumWithArtist(
        roomAlbum = roomAlbum,
        roomArtist = roomArtist
    )
}

/**
 * Converts a RoomAlbumWithMusics to an AlbumWithMusics.
 */
internal fun RoomAlbumWithMusics.toAlbumWithMusics(): AlbumWithMusics = AlbumWithMusics(
    album = roomAlbum.toAlbum(),
    musics = roomMusics.map { it.toMusic() },
    artist = roomArtist?.toArtist()
)

/**
 * Converts an AlbumWithMusics to a RoomAlbumWithMusics.
 */
internal fun AlbumWithMusics.toRoomAlbumWithMusics(): RoomAlbumWithMusics = RoomAlbumWithMusics(
    roomAlbum = album.toRoomAlbum(),
    roomMusics = musics.map { it.toRoomMusic() },
    roomArtist = artist?.toRoomArtist()
)