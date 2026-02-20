package com.github.enteraname74.domain.model.player

import com.github.enteraname74.domain.model.Music
import java.util.UUID

/**
 * Used to save a played music.
 * It is used to save multiple musics from the player and retrieve theme later.
 */
data class PlayerMusic(
    val music: Music,
    val playedListId: UUID,
    val order: Double,
    val shuffledOrder: Double,
    val lastPlayedMillis: Long?,
)