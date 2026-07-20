package com.github.enteraname74.soulsearching.remote.model.player

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class MusicsOperationOnPlayedListBody(
    val deviceId: String,
    val listId: Uuid,
    val musicIds: List<String>,
)