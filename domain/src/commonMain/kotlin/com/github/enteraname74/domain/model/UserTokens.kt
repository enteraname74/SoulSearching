package com.github.enteraname74.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UserTokens(
    val accessToken: String,
    val refreshToken: String,
)