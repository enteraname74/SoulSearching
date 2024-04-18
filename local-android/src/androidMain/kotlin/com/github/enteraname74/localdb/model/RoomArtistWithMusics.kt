package com.github.enteraname74.localdb.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.github.enteraname74.domain.model.ArtistWithMusics

/**
 * Room representation of an ArtistWithMusics.
 */
internal data class RoomArtistWithMusics(
    @Embedded val roomArtist: RoomArtist = RoomArtist(),
    @Relation(
        parentColumn = "artistId",
        entityColumn = "musicId",
        associateBy = Junction(RoomMusicArtist::class)
    )
    val roomMusics : List<RoomMusic> = emptyList()
)

/**
 * Converts a RoomArtistWithMusics to an ArtistWithMusics.
 */
internal fun RoomArtistWithMusics.toArtistWithMusics(): ArtistWithMusics = ArtistWithMusics(
    artist = roomArtist.toArtist(),
    musics = roomMusics.map { it.toMusic() }
)

/**
 * Converts an ArtistWithMusics to a RoomArtistWithMusics.
 */
internal fun ArtistWithMusics.toRoomArtistWithMusics(): RoomArtistWithMusics = RoomArtistWithMusics(
    roomArtist = artist.toRoomArtist(),
    roomMusics = musics.map { it.toRoomMusic() }
)