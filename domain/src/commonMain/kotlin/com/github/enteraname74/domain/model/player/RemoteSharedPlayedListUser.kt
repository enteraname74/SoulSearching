package com.github.enteraname74.domain.model.player

import kotlinx.serialization.Serializable
import java.util.UUID
import kotlin.uuid.Uuid

@Serializable
data class RemoteSharedPlayedListUser(
    val id: Uuid,
    val deviceId: String,
    val username: String,
    val joinedAt: Long,
)

data class SharedPlayedListUser(
    val listId: UUID,
    val isOwner: Boolean,
    val id: Uuid,
    val deviceId: String,
    val username: String,
    val joinedAt: Long,
) {
    val listUserId = "$listId-$id-$deviceId"
}