package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.MusicLyrics
import com.github.enteraname74.domain.repository.LyricsRepository
import com.github.enteraname74.soulsearching.repository.datasource.LyricsRemoteDataSource

class LyricsRepositoryImpl(
    private val lyricsRemoteDataSource: LyricsRemoteDataSource,
): LyricsRepository {
    override suspend fun getLyricsOfSong(music: Music): MusicLyrics? =
        lyricsRemoteDataSource.getLyricsOfSong(music)
}

