package com.github.enteraname74.localdb.model.player

import androidx.room3.Entity
import androidx.room3.ForeignKey
import com.github.enteraname74.domain.model.player.PlayerMusicUser
import com.github.enteraname74.localdb.model.RoomMusic
import kotlin.uuid.Uuid

// TODO: Can we use different Uuid (kotlin and Java) for foreign keys?
@Entity(
    primaryKeys = ["playedListId", "userId", "musicId"],
    foreignKeys = [
        // No foreign keys on users as there may be sync issues where we are not updated on users.
        ForeignKey(
            entity = RoomPlayerPlayedList::class,
            parentColumns = ["id"],
            childColumns = ["playedListId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = RoomMusic::class,
            parentColumns = ["musicId"],
            childColumns = ["musicId"],
            onDelete = ForeignKey.CASCADE,
        )
    ]
)
data class RoomPlayerMusicUser(
    val playedListId: Uuid,
    val userId: Uuid,
    val musicId: Uuid,
)

internal fun PlayerMusicUser.toRoomPlayerMusicUser(): RoomPlayerMusicUser =
    RoomPlayerMusicUser(
        playedListId = playedListId,
        userId = userId,
        musicId = musicId,
    )
