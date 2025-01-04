package com.github.enteraname74.soulsearching.repository.datasource

import com.github.enteraname74.domain.model.Music

interface LyricsDataSource {
    suspend fun getLyricsOfSong(music: Music): String?
}