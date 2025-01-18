package com.github.enteraname74.localdb.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.github.enteraname74.domain.ext.coverFromSongs
import com.github.enteraname74.domain.model.AlbumWithArtist
import com.github.enteraname74.domain.model.AlbumWithMusics

/**
 * Room representation of an AlbumWithMusics.
 */
internal data class RoomAlbumWithMusics(
    @Embedded val roomAlbum: RoomAlbum,
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

    fun toAlbumWithArtist(): AlbumWithArtist {
        val album = roomAlbum.toAlbum()

        return AlbumWithArtist(
            album = album,
            artist = roomArtist?.toArtist(),
            cover = if (album.cover?.isEmpty() == false) {
                album.cover
            } else {
                roomMusics
                    .map { it.toMusic() }
                    .coverFromSongs()
            }
        )
    }
}

/**
 * Converts a RoomAlbumWithMusics to an AlbumWithMusics.
 */
internal fun RoomAlbumWithMusics.toAlbumWithMusics(): AlbumWithMusics = AlbumWithMusics(
    album = roomAlbum.toAlbum(),
    musics = roomMusics.map { it.toMusic() },
    artist = roomArtist?.toArtist()
)