package com.github.enteraname74.soulsearching.remote.model.player

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class UpdateCurrentMusicBody(
    val listId: Uuid,
    val deviceId: String,
    val musicId: String,
)
