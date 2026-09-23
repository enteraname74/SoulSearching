package com.github.enteraname74.soulsearching.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CloudPreferences(
    val url: String?,
    val lastSyncMillis: Long?,
    val lastStatisticsSyncMillis: Long?,
)
