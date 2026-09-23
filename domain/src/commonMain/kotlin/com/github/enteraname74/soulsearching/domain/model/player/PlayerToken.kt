package com.github.enteraname74.soulsearching.domain.model.player

import kotlin.time.Instant

data class PlayerToken(
    val token: String,
    val expireAt: Instant,
)
