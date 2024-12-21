package com.github.enteraname74.domain.usecase.album

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumWithArtist
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.MusicAlbumRepository
import kotlinx.coroutines.flow.first
import java.util.UUID

class GetCorrespondingAlbumUseCase(
    private val albumRepository: AlbumRepository,
    private val musicAlbumRepository: MusicAlbumRepository,
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
        musicId: UUID
    ): Album? {
        val albumId: UUID = musicAlbumRepository.getAlbumIdFromMusicId(
            musicId = musicId
        ) ?: return null
        return albumRepository.getFromId(albumId = albumId).first()
    }
}