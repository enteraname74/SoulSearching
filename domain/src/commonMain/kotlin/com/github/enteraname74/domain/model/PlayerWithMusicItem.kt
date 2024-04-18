package com.github.enteraname74.domain.model

/**
 * Link a PlayerMusic with its corresponding song.
 */
data class PlayerWithMusicItem(
    val playerMusic: PlayerMusic = PlayerMusic(),
    val music: Music? = null
)