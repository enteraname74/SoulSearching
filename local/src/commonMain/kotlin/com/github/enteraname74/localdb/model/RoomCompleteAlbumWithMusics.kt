package com.github.enteraname74.localdb.model

import androidx.room.Embedded
import androidx.room.Relation
import com.github.enteraname74.domain.model.AlbumWithMusics

data class RoomCompleteAlbumWithMusics(
    @Embedded val roomAlbum: RoomAlbum,
    @Relation(
        parentColumn = "albumId",
        entityColumn = "albumId",
        entity = RoomMusic::class,
    )
    val roomMusics : List<RoomCompleteMusic>,
    @Relation(
        parentColumn = "albumId",
        entityColumn = "albumId",
        entity = RoomAlbum::class,
    )
    val completeAlbum: RoomCompleteAlbum,
) {
    /**
     * Converts a RoomAlbumWithMusics to an AlbumWithMusics.
     */
    internal fun toAlbumWithMusics(): AlbumWithMusics = AlbumWithMusics(
        album = completeAlbum.toAlbum(),
        musics = roomMusics.map { it.toMusic() }.sortedWith(
            compareBy(nullsLast()) {
                it.albumPosition
            }
        ),
    )
}
