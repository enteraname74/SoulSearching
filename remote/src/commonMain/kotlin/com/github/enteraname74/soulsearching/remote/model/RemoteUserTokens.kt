package com.github.enteraname74.soulsearching.remote.model

import com.github.enteraname74.soulsearching.repository.model.UserTokens
import kotlinx.serialization.Serializable

@Serializable
data class RemoteUserTokens(
    val accessToken: String,
    val refreshToken: String,
) {
    fun toUserTokens(): UserTokens = UserTokens(
        accessToken = accessToken,
        refreshToken = refreshToken,
    )
}
