package com.github.enteraname74.soulsearching.remote.model.player

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class RemoveUserFromPlayedListBody(
    val listId: Uuid,
    val deviceId: String,
    val userIdToRemove: Uuid,
    val deviceIdToRemove: String,
)