package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.repository.LyricsRepository
import com.github.enteraname74.soulsearching.repository.datasource.LyricsDataSource

class LyricsRepositoryImpl(
    private val lyricsDataSource: LyricsDataSource,
): LyricsRepository {
    override suspend fun getLyricsOfSong(music: Music): String? =
        lyricsDataSource.getLyricsOfSong(music)
}

