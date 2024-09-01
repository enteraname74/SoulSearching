package com.github.enteraname74.domain.usecase.album

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.repository.AlbumRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class GetAlbumsOfArtistsUseCase(
    private val albumRepository: AlbumRepository,
) {
    operator fun invoke(artistId: UUID): Flow<List<Album>> =
        albumRepository.getAlbumsOfArtist(
            artistId = artistId,
        )
}