package com.github.enteraname74.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CloudPreferences(
    val url: String?,
    val lastSyncMillis: Long?,
)
