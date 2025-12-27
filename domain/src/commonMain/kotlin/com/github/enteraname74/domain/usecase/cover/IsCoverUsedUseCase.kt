package com.github.enteraname74.domain.usecase.cover

import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.first
import java.util.*

class IsCoverUsedUseCase(
    private val musicRepository: MusicRepository,
    private val playlistRepository: PlaylistRepository,
    private val artistRepository: ArtistRepository,
    private val albumRepository: AlbumRepository,
) {
    // TODO: Move into single SQL query
    suspend operator fun invoke(coverId: UUID): Boolean =
        musicRepository.getAll().first().any { (it.cover as? Cover.CoverFile)?.fileCoverId == coverId }
                || albumRepository.getAll().first().any { (it.cover as? Cover.CoverFile)?.fileCoverId == coverId }
                || artistRepository.getAll().first().any { (it.cover as? Cover.CoverFile)?.fileCoverId == coverId }
                || playlistRepository.getAll().first().any { (it.cover as? Cover.CoverFile)?.fileCoverId == coverId }
}