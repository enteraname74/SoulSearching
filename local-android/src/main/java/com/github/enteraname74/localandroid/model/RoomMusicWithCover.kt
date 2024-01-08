package com.github.enteraname74.localandroid.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.github.enteraname74.domain.model.MusicWithCover

/**
 * Room representation of a MusicWithCover.
 */
internal data class RoomMusicWithCover(
    @Embedded val roomMusic : RoomMusic = RoomMusic(),
    @Relation(
            parentColumn = "musicId",
            entityColumn = "coverId",
            associateBy = Junction(RoomImageCover::class)
    )
    val cover : RoomImageCover? = null
)

/**
 * Converts a RoomMusicWithCover to a MusicWithCover.
 */
internal fun RoomMusicWithCover.toMusicWithCover(): MusicWithCover = MusicWithCover(
    music = roomMusic.toMusic(),
    cover = cover?.toImageCover()
)

/**
 * Converts a MusicWithCover to a RoomMusicWithCover.
 */
internal fun MusicWithCover.toRoomMusicWithCover(): RoomMusicWithCover = RoomMusicWithCover(
    roomMusic = music.toRoomMusic(),
    cover = cover?.toRoomImageCover()
)