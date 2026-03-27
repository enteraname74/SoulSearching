package com.github.enteraname74.soulsearching.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class MusicIdsCheckBody(
    val ids: List<String>
)
