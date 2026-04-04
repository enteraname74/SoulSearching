package com.github.enteraname74.soulsearching.remote.model.player

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class CheckPlayerMusicIdsBody(
    val musicIds: List<String>,
    val deviceId: String,
    val listId: Uuid,
) 