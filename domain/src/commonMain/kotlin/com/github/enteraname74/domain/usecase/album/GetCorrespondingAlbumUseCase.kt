package com.github.enteraname74.domain.usecase.album

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumWithArtist
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.repository.AlbumRepository
import kotlinx.coroutines.flow.first
import java.util.*

class GetCorrespondingAlbumUseCase(
    private val albumRepository: AlbumRepository,
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
}