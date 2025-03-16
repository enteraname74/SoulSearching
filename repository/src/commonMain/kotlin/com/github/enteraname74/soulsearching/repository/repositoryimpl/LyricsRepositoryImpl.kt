package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.MusicLyrics
import com.github.enteraname74.domain.repository.LyricsRepository
import com.github.enteraname74.soulsearching.repository.datasource.LyricsRemoteDataSource
import com.github.enteraname74.soulsearching.repository.datasource.artist.ArtistLocalDataSource
import kotlinx.coroutines.flow.firstOrNull

class LyricsRepositoryImpl(
    private val lyricsRemoteDataSource: LyricsRemoteDataSource,
    private val artistLocalDataSource: ArtistLocalDataSource,
): LyricsRepository {
    override suspend fun getLyricsOfSong(music: Music): MusicLyrics? {
        val principalArtistName: String = artistLocalDataSource
            .getArtistsOfMusic(musicId = music.musicId)
            .firstOrNull()
            ?.firstOrNull()
            ?.artistName ?: music.artist

        return lyricsRemoteDataSource.getLyricsOfSong(
            music = music,
            principalArtistName = principalArtistName,
        )
    }
}

