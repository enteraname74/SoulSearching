package com.github.enteraname74.localdb.model.player

import androidx.room3.Embedded
import androidx.room3.Relation
import com.github.enteraname74.domain.model.player.PlayerMusic
import com.github.enteraname74.localdb.model.RoomCompleteMusic
import com.github.enteraname74.localdb.model.RoomMusic

data class RoomCompletePlayerMusic(
    @Embedded val playerMusic: RoomPlayerMusic,
    @Relation(
        parentColumns = ["musicId"],
        entityColumns = ["musicId"],
        entity = RoomMusic::class,
    )
    val completeMusic: RoomCompleteMusic,
) {
    fun toPlayerMusic(): PlayerMusic =
        PlayerMusic(
            music = completeMusic.toMusic(),
            playedListId = playerMusic.playedListId,
            order = playerMusic.order,
            shuffledOrder = playerMusic.shuffledOrder,
            lastPlayedMillis = playerMusic.lastPlayedMillis,
        )
}
