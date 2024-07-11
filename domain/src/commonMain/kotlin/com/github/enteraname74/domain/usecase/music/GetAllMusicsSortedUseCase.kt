package com.github.enteraname74.domain.usecase.music

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.domain.model.SortDirection
import com.github.enteraname74.domain.model.SortType
import com.github.enteraname74.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAllMusicsSortedUseCase(
    private val musicRepository: MusicRepository,
) {
    operator fun invoke(sortDirection: Int, sortType: Int) : Flow<List<Music>> =
        musicRepository.getAll().map { list ->
            list.sorted(
                sortDirection = sortDirection,
                sortType = sortType,
            )
        }

    private fun List<Music>.sortedByType(sortType: Int): List<Music> =
        when (sortType) {
            SortType.NB_PLAYED -> this.sortedBy { it.nbPlayed }
            SortType.ADDED_DATE -> this.sortedBy{ it.addedDate }
            else -> this.sortedBy { it.name }
        }

    private fun List<Music>.sorted(
        sortDirection: Int,
        sortType: Int,
    ): List<Music> =
        when(sortDirection) {
            SortDirection.DESC -> this.sortedByType(sortType).asReversed()
            else -> this.sortedByType(sortType)
        }
}