package com.github.enteraname74.model

import java.util.UUID

/**
 * Used to save a played music.
 * It is used to save multiple musics from the player and retrieve theme later.
 */
data class PlayerMusic(
    val id: Long = 0,
    val playerMusicId: UUID = UUID.randomUUID()
)