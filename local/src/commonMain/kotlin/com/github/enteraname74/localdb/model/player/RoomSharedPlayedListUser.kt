package com.github.enteraname74.localdb.model.player

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.github.enteraname74.domain.model.player.PlayerUserStatus
import com.github.enteraname74.domain.model.player.SharedPlayedListUser
import java.util.UUID
import kotlin.uuid.Uuid

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = RoomPlayerPlayedList::class,
            parentColumns = ["id"],
            childColumns = ["playedListId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("playedListId")]
)
data class RoomSharedPlayedListUser(
    @PrimaryKey val id: String,
    val playedListId: UUID,
    val userId: Uuid,
    val deviceId: String,
    val username: String,
    val joinedAt: Long,
    val isOwner: Boolean,
    val status: PlayerUserStatus,
) {
    fun toSharedPlayedListUser(): SharedPlayedListUser =
        SharedPlayedListUser(
            id = userId,
            deviceId = deviceId,
            username = username,
            joinedAt = joinedAt,
            listId = playedListId,
            isOwner = isOwner,
            status = status,
        )
}

internal fun SharedPlayedListUser.toRoomSharedPlayedListUser(): RoomSharedPlayedListUser =
    RoomSharedPlayedListUser(
        id = listUserId,
        playedListId = listId,
        userId = id,
        deviceId = deviceId,
        username = username,
        joinedAt = joinedAt,
        isOwner = isOwner,
        status = status,
    )
