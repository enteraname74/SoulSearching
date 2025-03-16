package com.github.enteraname74.domain.usecase.album

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumWithArtist
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.MusicRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import java.util.*

class GetCorrespondingAlbumUseCase(
    private val albumRepository: AlbumRepository,
    private val musicRepository: MusicRepository,
) {

    /**
     * Tries to retrieve the corresponding album of a music and its artist id.
     */
    suspend operator fun invoke(
        albumName: String,
        artistId: UUID
    ): Album? {
        val allAlbumsWithArtists: List<AlbumWithArtist> = albumRepository.getAllAlbumsWithArtist().first()
        return allAlbumsWithArtists
            .firstOrNull {
                it.album.albumName == albumName && it.artist?.artistId == artistId
            }?.album
    }

    /**
     * Tries to retrieve the corresponding album of a music and its artist name.
     */
    suspend operator fun invoke(
        albumName: String,
        artistName: String,
    ): Album? {
        val allAlbumsWithArtists: List<AlbumWithArtist> = albumRepository.getAllAlbumsWithArtist().first()
        return allAlbumsWithArtists
            .firstOrNull {
                it.album.albumName == albumName && it.artist?.artistName == artistName
            }?.album
    }

    /**
     * Tries to retrieve the corresponding album of a music.
     */
    suspend operator fun invoke(
        music: Music
    ): Album? = albumRepository.getFromId(albumId = music.albumId).first()

    suspend fun withMusics(
        musicId: UUID
    ): AlbumWithMusics? {
        val albumId: UUID = musicRepository.getFromId(musicId).firstOrNull()?.albumId ?: return null
        return albumRepository.getAlbumWithMusics(albumId = albumId).first()
    }
}