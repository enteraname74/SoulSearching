package com.github.enteraname74.domain.model.player

import com.github.enteraname74.domain.model.CloudMusic
import com.github.enteraname74.domain.model.Music
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class SharedPlayerMusic(
    val playedListId: Uuid,
    val music: CloudMusic,
    val order: Double,
    val lastPlayedMillis: Long?,
) {
    fun toPlayerMusic(
        music: Music
    ): PlayerMusic =
        PlayerMusic(
            music = music,
            playedListId = playedListId,
            order = order,
            shuffledOrder = order,
            lastPlayedMillis = lastPlayedMillis,
        )
}
