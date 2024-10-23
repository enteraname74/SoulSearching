package com.github.enteraname74.domain.usecase.album

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.repository.AlbumRepository
import kotlinx.coroutines.flow.first
import java.util.UUID

class UpdateAlbumCoverUseCase(
    private val albumRepository: AlbumRepository,
) {
    suspend operator fun invoke(
        newCoverId: UUID,
        albumId: UUID,
    ) {
        val album: Album = albumRepository.getFromId(albumId = albumId).first() ?: return
        albumRepository.upsert(
            album = album.copy(
                cover = Cover.FileCover(fileCoverId = newCoverId),
            )
        )
    }
}