package com.github.enteraname74.localdb.model.player

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey
import com.github.enteraname74.domain.model.player.PlayerUserStatus
import com.github.enteraname74.domain.model.player.SharedPlayedListUser
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
    val playedListId: Uuid,
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
