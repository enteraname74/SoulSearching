package com.github.enteraname74.domain.usecase.playlist

import com.github.enteraname74.domain.model.PlaylistWithMusicsNumber
import com.github.enteraname74.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAllPlaylistWithMusicsNumberFromQuickAccessUseCase(
    private val playlistRepository: PlaylistRepository,
) {
    operator fun invoke(): Flow<List<PlaylistWithMusicsNumber>> =
        playlistRepository.getAllPlaylistWithMusics().map { list ->
            list
                .filter { it.playlist.isInQuickAccess }
                .map { it.toPlaylistWithMusicsNumber() }
        }
}