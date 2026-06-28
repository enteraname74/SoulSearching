package com.github.enteraname74.localdb.model.player

import androidx.room.Entity
import androidx.room.ForeignKey
import com.github.enteraname74.domain.model.player.PlayerMusicUser
import com.github.enteraname74.localdb.model.RoomMusic
import java.util.*
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

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
    val playedListId: UUID,
    val userId: Uuid,
    val musicId: UUID,
)

internal fun PlayerMusicUser.toRoomPlayerMusicUser(): RoomPlayerMusicUser =
    RoomPlayerMusicUser(
        playedListId = playedListId.toJavaUuid(),
        userId = userId,
        musicId = musicId,
    )
