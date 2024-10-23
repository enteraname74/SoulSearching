package com.github.enteraname74.domain.usecase.cover

import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.enteraname74.domain.usecase.album.GetNumberOfAlbumsWithCoverIdUseCase
import kotlinx.coroutines.flow.first
import java.util.UUID

class IsCoverUsedUseCase(
    private val musicRepository: MusicRepository,
    private val playlistRepository: PlaylistRepository,
    private val artistRepository: ArtistRepository,
    private val getNumberOfAlbumsWithCoverIdUseCase: GetNumberOfAlbumsWithCoverIdUseCase,
) {
    suspend operator fun invoke(coverId: UUID): Boolean =
        musicRepository.getAll().first().any { (it.cover as? Cover.FileCover)?.fileCoverId == coverId }
                || getNumberOfAlbumsWithCoverIdUseCase(coverId = coverId) > 0
                || artistRepository.getAll().first().any { (it.cover as? Cover.FileCover)?.fileCoverId == coverId }
                || playlistRepository.getAll().first().any { (it.cover as? Cover.FileCover)?.fileCoverId == coverId }
}