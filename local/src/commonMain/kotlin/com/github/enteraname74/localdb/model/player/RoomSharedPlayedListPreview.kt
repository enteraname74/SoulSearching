package com.github.enteraname74.localdb.model.player

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.enteraname74.domain.model.player.SharedPlayedListPreview
import kotlin.uuid.Uuid

@Entity
data class RoomSharedPlayedListPreview(
    @PrimaryKey
    val id: Uuid,
    val code: String,
    val totalUsers: Int,
    val connectedUsers: Int,
    val createdAtMillis: Long,
    val isOwner: Boolean,
) {
    fun toSharedPlayedListPreview(): SharedPlayedListPreview =
        SharedPlayedListPreview(
            id = id,
            code = code,
            totalUsers = totalUsers,
            connectedUsers = connectedUsers,
            createdAtMillis = createdAtMillis,
            isOwner = isOwner,
        )
}

internal fun SharedPlayedListPreview.toRoomSharedPlayedListPreview(): RoomSharedPlayedListPreview =
    RoomSharedPlayedListPreview(
        id = id,
        code = code,
        totalUsers = totalUsers,
        connectedUsers = connectedUsers,
        createdAtMillis = createdAtMillis,
        isOwner = isOwner,
    )
