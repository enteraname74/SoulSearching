package com.github.enteraname74.domain.usecase.imagecover

import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.first
import java.util.UUID

class IsImageCoverUsedUseCase(
    private val musicRepository: MusicRepository,
    private val albumRepository: AlbumRepository,
    private val playlistRepository: PlaylistRepository,
    private val artistRepository: ArtistRepository,
) {
    suspend operator fun invoke(coverId: UUID): Boolean =
        musicRepository.getAll().first().any { it.coverId == coverId }
                || albumRepository.getAll().first().any { it.coverId == coverId }
                || artistRepository.getAll().first().any { it.coverId == coverId }
                || playlistRepository.getAll().first().any { it.coverId == coverId }
}