package com.github.enteraname74.soulsearching.remote.model.player

import kotlinx.serialization.Serializable

@Serializable
data class NewPlayedListBody(
    val deviceId: String,
    val musicIds: List<String>,
)