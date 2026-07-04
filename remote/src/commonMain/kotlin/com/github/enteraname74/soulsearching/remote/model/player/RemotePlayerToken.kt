package com.github.enteraname74.soulsearching.remote.model.player

import com.github.enteraname74.domain.model.player.PlayerToken
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class RemotePlayerToken(
    val token: String,
    val expireAt: Long,
) {
    fun toPlayerToken(): PlayerToken =
        PlayerToken(
            token = token,
            expireAt = Instant.fromEpochMilliseconds(expireAt)
        )
}
