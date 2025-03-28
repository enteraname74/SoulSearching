package com.github.enteraname74.soulsearching.repository.repositoryimpl

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.MusicLyrics
import com.github.enteraname74.domain.repository.LyricsRepository
import com.github.enteraname74.soulsearching.repository.datasource.ArtistDataSource
import com.github.enteraname74.soulsearching.repository.datasource.LyricsRemoteDataSource
import kotlinx.coroutines.flow.firstOrNull

class LyricsRepositoryImpl(
    private val lyricsRemoteDataSource: LyricsRemoteDataSource,
    private val artistDataSource: ArtistDataSource,
): LyricsRepository {
    override suspend fun getLyricsOfSong(music: Music): MusicLyrics? {
        val principalArtistName: String = artistDataSource
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

