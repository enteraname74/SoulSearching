package com.github.enteraname74.localdb.model

import androidx.room.Embedded
import androidx.room.Relation
import com.github.enteraname74.domain.model.PlayerWithMusicItem

/**
 * Room representation of a PlayerWithMusicItem.
 */
internal data class RoomPlayerWithMusicItem(
    @Embedded val roomPlayerMusic: RoomPlayerMusic = RoomPlayerMusic(),
    @Relation(
        parentColumn = "playerMusicId",
        entityColumn = "musicId",
    )
    val roomMusic: RoomMusic? = null
)

/**
 * Converts a RoomPlayerWithMusicItem to a PlayerWithMusicItem.
 */
internal fun RoomPlayerWithMusicItem.toPlayerWithMusicItem(): PlayerWithMusicItem = PlayerWithMusicItem(
    playerMusic = roomPlayerMusic.toPlayerMusic(),
    music = roomMusic?.toMusic()
)

/**
 * Converts a PlayerWithMusicItem to a RoomPlayerWithMusicItem.
 */
internal fun PlayerWithMusicItem.toRoomPlayerWithMusicItem(): RoomPlayerWithMusicItem = RoomPlayerWithMusicItem(
    roomPlayerMusic = playerMusic.toRoomPlayerMusic(),
    roomMusic = music?.toRoomMusic()
)