package com.github.enteraname74.soulsearching.remote.model.player

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class AddMusicUrlToPlayedListBody(
    val deviceId: String,
    val listId: Uuid,
    val url: String,
) 