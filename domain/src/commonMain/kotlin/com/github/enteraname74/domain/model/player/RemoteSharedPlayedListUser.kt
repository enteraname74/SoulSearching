package com.github.enteraname74.domain.model.player

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class RemoteSharedPlayedListUser(
    val id: Uuid,
    val deviceId: String,
    val username: String,
    val joinedAt: Long,
)

data class SharedPlayedListUser(
    val listId: Uuid,
    val isOwner: Boolean,
    val id: Uuid,
    val deviceId: String,
    val username: String,
    val joinedAt: Long,
)