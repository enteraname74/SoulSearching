package com.github.enteraname74.domain.model

/**
 * Used to link a song to its corresponding cover.
 * The cover can be null (the song doesn't possess a cover).
 */
data class MusicWithCover(
    val music : Music,
    val cover : ImageCover,
)