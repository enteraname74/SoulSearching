package com.github.enteraname74.soulsearching.repository.datasource

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.MusicLyrics

interface LyricsRemoteDataSource {
    suspend fun getLyricsOfSong(music: Music): MusicLyrics?
}