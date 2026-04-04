package com.github.enteraname74.domain.model.player

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class SharedPlayedListUser(
    val id: Uuid,
    val deviceId: String,
    val username: String,
    val joinedAt: Long,
)