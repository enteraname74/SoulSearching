package com.github.enteraname74.soulsearching.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class MusicIdsBody(
    val ids: List<String>
)
