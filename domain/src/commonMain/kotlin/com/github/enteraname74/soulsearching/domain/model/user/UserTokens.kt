package com.github.enteraname74.soulsearching.domain.model.user

import kotlinx.serialization.Serializable

@Serializable
data class UserTokens(
    val accessToken: String,
    val refreshToken: String,
)
