package com.github.enteraname74.domain.usecase.playlist

import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetFavoritePlaylistWithMusicsUseCase(
    private val playlistRepository: PlaylistRepository,
) {
    operator fun invoke(dataMode: DataMode? = null): Flow<PlaylistWithMusics?> =
        playlistRepository.getAllPlaylistWithMusics(dataMode).map { list ->
            list.firstOrNull { it.playlist.isFavorite }
        }
}