package com.github.enteraname74.domain.model.player

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class RemoteSharedPlayedListUser(
    val id: Uuid,
    val deviceId: String,
    val username: String,
    val joinedAt: Long,
    val status: PlayerUserStatus,
)

data class SharedPlayedListUser(
    val listId: Uuid,
    val isOwner: Boolean,
    val id: Uuid,
    val deviceId: String,
    val username: String,
    val joinedAt: Long,
    val status: PlayerUserStatus,
) {
    val listUserId = "$listId-$id-$deviceId"
}

enum class PlayerUserStatus {
    Connected,
    Disconnected,
}
