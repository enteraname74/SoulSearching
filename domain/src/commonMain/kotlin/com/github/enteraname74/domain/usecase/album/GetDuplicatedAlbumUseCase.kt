package com.github.enteraname74.domain.usecase.album

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumWithArtist
import com.github.enteraname74.domain.repository.AlbumRepository
import kotlinx.coroutines.flow.first
import java.util.*

class GetDuplicatedAlbumUseCase(
    private val albumRepository: AlbumRepository,
) {
    suspend operator fun invoke(
        albumId: UUID,
        albumName: String,
        artistId: UUID
    ): Album? {
        val allAlbumsWithArtists: List<AlbumWithArtist> = albumRepository.getAllAlbumsWithArtist().first()
        return allAlbumsWithArtists
            .firstOrNull {
                it.album.albumName == albumName &&
                        it.artist?.artistId == artistId &&
                        it.album.albumId != albumId
            }?.album
    }
}