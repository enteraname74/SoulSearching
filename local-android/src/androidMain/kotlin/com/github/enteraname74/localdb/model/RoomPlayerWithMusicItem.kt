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
        entity = RoomMusic::class,
    )
    val roomMusic: RoomCompleteMusic? = null
) {
    fun toPlayerWithMusicItem(): PlayerWithMusicItem = PlayerWithMusicItem(
        playerMusic = roomPlayerMusic.toPlayerMusic(),
        music = roomMusic?.toMusic()
    )
}