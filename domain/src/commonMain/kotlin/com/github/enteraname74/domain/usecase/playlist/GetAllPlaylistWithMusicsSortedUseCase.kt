package com.github.enteraname74.domain.usecase.playlist

import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.domain.model.SortDirection
import com.github.enteraname74.domain.model.SortType
import com.github.enteraname74.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAllPlaylistWithMusicsSortedUseCase(
    private val playlistRepository: PlaylistRepository,
) {
    operator fun invoke(sortDirection: Int, sortType: Int): Flow<List<PlaylistWithMusics>> =
        playlistRepository.getAllPlaylistWithMusics().map {
            list -> list.sorted(
                sortDirection = sortDirection,
                sortType = sortType,
            )
        }

    private fun List<PlaylistWithMusics>.sortedByType(sortType: Int): List<PlaylistWithMusics> =
        when (sortType) {
            SortType.NB_PLAYED -> this.sortedBy { it.playlist.nbPlayed }
            SortType.ADDED_DATE -> this.sortedBy{ it.playlist.addedDate }
            else -> this.sortedBy { it.playlist.name }
        }

    private fun List<PlaylistWithMusics>.sorted(
        sortDirection: Int,
        sortType: Int,
    ): List<PlaylistWithMusics> =
        when(sortDirection) {
            SortDirection.DESC -> this.sortedByType(sortType).asReversed()
            else -> this.sortedByType(sortType)
        }
}