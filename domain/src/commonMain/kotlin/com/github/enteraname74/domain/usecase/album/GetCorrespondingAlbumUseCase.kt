package com.github.enteraname74.domain.usecase.album

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumWithArtist
import com.github.enteraname74.domain.repository.AlbumRepository
import java.util.UUID

class GetCorrespondingAlbumUseCase(
    private val albumRepository: AlbumRepository,
) {
    suspend operator fun invoke(
        albumName: String,
        artistId: UUID
    ): Album? {
        val allAlbumsWithArtists: List<AlbumWithArtist> = albumRepository.getAllAlbumsWithArtist()
        return allAlbumsWithArtists
            .firstOrNull {
                it.album.albumName == albumName && it.artist?.artistId == artistId
            }?.album
    }
}