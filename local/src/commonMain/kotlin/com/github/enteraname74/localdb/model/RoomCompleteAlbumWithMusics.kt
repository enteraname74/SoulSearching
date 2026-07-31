package com.github.enteraname74.localdb.model

import androidx.room3.Embedded
import androidx.room3.Relation
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.domain.model.Music
import kotlin.comparisons.nullsLast

data class RoomCompleteAlbumWithMusics(
    @Embedded val roomAlbum: RoomAlbum,
    @Relation(
        parentColumns = ["albumId"],
        entityColumns = ["albumId"],
        entity = RoomMusic::class,
    )
    val roomMusics: List<RoomCompleteMusic>,
    @Relation(
        parentColumns = ["albumId"],
        entityColumns = ["albumId"],
        entity = RoomAlbum::class,
    )
    val completeAlbum: RoomCompleteAlbum,
) {
    /**
     * Converts a RoomAlbumWithMusics to an AlbumWithMusics.
     */
    internal fun toAlbumWithMusics(): AlbumWithMusics = AlbumWithMusics(
        album = completeAlbum.toAlbum(),
        musics = roomMusics
            .map { it.toMusic() }
            .sortedWith(
                compareBy<Music, Int?>(nullsLast()) { it.albumPosition }
                    .thenBy { it.name }
            ),
    )
}
