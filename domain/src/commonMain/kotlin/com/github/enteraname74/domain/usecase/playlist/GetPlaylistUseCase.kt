package com.github.enteraname74.domain.usecase.playlist

import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class GetPlaylistUseCase(
    private val playlistRepository: PlaylistRepository
) {
    operator fun invoke(playlistId: UUID): Flow<Playlist?> =
        playlistRepository.getFromId(
            playlistId = playlistId,
        )
}