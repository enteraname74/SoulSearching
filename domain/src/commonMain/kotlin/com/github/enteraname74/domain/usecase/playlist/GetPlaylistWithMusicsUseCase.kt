package com.github.enteraname74.domain.usecase.playlist

import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class GetPlaylistWithMusicsUseCase(
    private val playlistRepository: PlaylistRepository,
) {
    operator fun invoke(playlistId: UUID): Flow<PlaylistWithMusics?> =
        playlistRepository.getPlaylistWithMusics(playlistId)
}