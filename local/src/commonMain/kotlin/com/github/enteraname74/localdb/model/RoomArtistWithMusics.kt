package com.github.enteraname74.localdb.model

import androidx.room3.Embedded
import androidx.room3.Junction
import androidx.room3.Relation
import com.github.enteraname74.domain.model.ArtistWithMusics

/**
 * Room representation of an ArtistWithMusics.
 */
data class RoomArtistWithMusics(
    @Embedded val roomArtist: RoomArtist,
    @Relation(
        parentColumns = ["artistId"],
        entityColumns = ["musicId"],
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

