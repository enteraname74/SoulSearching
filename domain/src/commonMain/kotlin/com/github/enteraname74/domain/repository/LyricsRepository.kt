package com.github.enteraname74.domain.repository

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.lyrics.MusicLyrics

/**
 * Repository for songs lyrics
 */
interface LyricsRepository {
    suspend fun getLocalLyricsOfSong(music: Music): MusicLyrics?
    suspend fun getRemoteLyricsOfSong(music: Music): MusicLyrics?
}