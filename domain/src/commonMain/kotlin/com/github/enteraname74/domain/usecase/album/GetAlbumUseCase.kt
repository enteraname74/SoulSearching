package com.github.enteraname74.domain.usecase.album

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.repository.AlbumRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class GetAlbumUseCase(
    private val albumRepository: AlbumRepository,
) {
    operator fun invoke(albumId: UUID): Flow<Album?> =
        albumRepository.getFromId(
            albumId = albumId,
        )
}