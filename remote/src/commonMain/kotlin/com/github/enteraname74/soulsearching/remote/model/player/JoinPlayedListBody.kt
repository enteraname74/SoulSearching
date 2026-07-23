package com.github.enteraname74.soulsearching.remote.model.player

import kotlinx.serialization.Serializable

@Serializable
data class JoinPlayedListBody(
    val code: String,
    val deviceId: String,
)
