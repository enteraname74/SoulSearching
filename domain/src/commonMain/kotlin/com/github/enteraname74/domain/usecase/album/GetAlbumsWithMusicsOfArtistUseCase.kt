package com.github.enteraname74.domain.usecase.album

import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.domain.repository.AlbumRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID

class GetAlbumsWithMusicsOfArtistUseCase(
    private val albumRepository: AlbumRepository,
) {
    operator fun invoke(artistId: UUID): Flow<List<AlbumWithMusics>> =
        albumRepository.getAlbumsWithMusicsOfArtist(
            artistId = artistId,
        )
}