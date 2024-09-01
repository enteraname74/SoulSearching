package com.github.enteraname74.domain.repository

import com.github.enteraname74.domain.model.Music

/**
 * Repository for songs lyrics
 */
interface LyricsRepository {
    /**
     * Retrieves the lyrics of a song.
     * If no lyrics are found, returns null.
     */
    suspend fun getLyricsOfSong(music: Music): String?
}