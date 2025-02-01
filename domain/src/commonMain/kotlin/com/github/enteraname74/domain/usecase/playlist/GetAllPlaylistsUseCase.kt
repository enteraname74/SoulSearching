package com.github.enteraname74.domain.usecase.playlist

import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow

class GetAllPlaylistsUseCase(
    private val playlistRepository: PlaylistRepository
) {
    operator fun invoke(dataMode: DataMode? = null): Flow<List<Playlist>> =
        playlistRepository.getAll(dataMode)
}