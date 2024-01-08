package com.github.enteraname74.model

/**
 * Link a PlayerMusic with its corresponding song.
 */
data class PlayerWithMusicItem(
    val playerMusic: PlayerMusic = PlayerMusic(),
    val music: Music? = null
)