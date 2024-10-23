package com.github.enteraname74.domain.usecase.album

import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.repository.AlbumRepository
import kotlinx.coroutines.flow.first
import java.util.*

class GetNumberOfAlbumsWithCoverIdUseCase(
    private val albumRepository: AlbumRepository,
) {
    suspend operator fun invoke(coverId: UUID): Int {
        val allAlbums = albumRepository.getAll().first()
        return allAlbums.count { (it.cover as? Cover.FileCover)?.fileCoverId == coverId }
    }
}