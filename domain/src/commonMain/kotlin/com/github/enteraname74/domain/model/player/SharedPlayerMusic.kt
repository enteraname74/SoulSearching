package com.github.enteraname74.domain.model.player

import com.github.enteraname74.domain.model.CloudMusic
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class SharedPlayerMusic(
    val playedListId: Uuid,
    val music: CloudMusic,
    val order: Double,
    val lastPlayedMillis: Long?,
)
