package com.github.enteraname74.localdb.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.github.enteraname74.domain.model.ArtistWithMusics

/**
 * Room representation of an ArtistWithMusics.
 */
data class RoomArtistWithMusics(
    @Embedded val roomArtist: RoomArtist,
    @Relation(
        parentColumn = "artistId",
        entityColumn = "musicId",
        associateBy = Junction(RoomMusicArtist::class),
        entity = RoomMusic::class,
    )
    val roomMusics : List<RoomCompleteMusic>,
)

/**
 * Converts a RoomArtistWithMusics to an ArtistWithMusics.
 */
internal fun RoomArtistWithMusics.toArtistWithMusics(): ArtistWithMusics = ArtistWithMusics(
    artist = roomArtist.toArtist(),
    musics = roomMusics.map { it.toMusic() }
)

