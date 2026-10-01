package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.soulsearching.domain.model.Music
import com.github.enteraname74.soulsearching.domain.model.lyrics.MusicLyrics
import com.github.enteraname74.soulsearching.domain.repository.LyricsRepository
import com.github.enteraname74.soulsearching.repository.datasource.lyrics.LyricsLocalDataSource
import com.github.enteraname74.soulsearching.repository.datasource.lyrics.LyricsRemoteDataSource

class LyricsRepositoryImpl(
    private val lyricsRemoteDataSource: LyricsRemoteDataSource,
    private val lyricsLocalDataSource: LyricsLocalDataSource,
) : LyricsRepository {
    override suspend fun getLocalLyricsOfSong(music: Music): MusicLyrics? =
        lyricsLocalDataSource.getLyricsOfSong(music)

    override suspend fun getRemoteLyricsOfSong(music: Music): MusicLyrics? =
        lyricsRemoteDataSource.getLyricsOfSong(
            music = music,
        )
}

