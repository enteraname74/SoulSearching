package com.github.enteraname74.soulsearching.remote.ext

import com.github.enteraname74.soulsearching.repository.model.UserTokens
import io.ktor.client.plugins.auth.providers.*

fun UserTokens.toBearerTokens(): BearerTokens =
    BearerTokens(
        accessToken = accessToken,
        refreshToken = refreshToken,
    )