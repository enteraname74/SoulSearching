package com.github.enteraname74.soulsearching.remote.model.player

import com.github.enteraname74.domain.model.player.SharedPlayedList
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class UpdatePlayedListBody(
    val listId: Uuid,
    val deviceId: String,
    val state: SharedPlayedList.State,
)