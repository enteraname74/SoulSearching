package com.github.enteraname74.domain.usecase.album

import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.domain.model.SortDirection
import com.github.enteraname74.domain.model.SortType
import com.github.enteraname74.domain.repository.AlbumRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAllAlbumWithMusicsSortedUseCase(
    private val albumRepository: AlbumRepository,
) {
    operator fun invoke(
        sortDirection: SortDirection,
        sortType: SortType,
    ): Flow<List<AlbumWithMusics>> =
        albumRepository.getAllAlbumWithMusics().map { list ->
            list.sorted(
                sortDirection = sortDirection,
                sortType = sortType,
            )
        }

    private fun List<AlbumWithMusics>.sortedByType(sortType: SortType): List<AlbumWithMusics> =
        when (sortType) {
            SortType.NB_PLAYED -> this.sortedBy { it.album.nbPlayed }
            SortType.ADDED_DATE -> this.sortedBy{ it.album.addedDate }
            SortType.NAME -> this.sortedBy { it.album.albumName }
        }

    private fun List<AlbumWithMusics>.sorted(
        sortDirection: SortDirection,
        sortType: SortType,
    ): List<AlbumWithMusics> =
        when(sortDirection) {
            SortDirection.DESC -> this.sortedByType(sortType).asReversed()
            SortDirection.ASC -> this.sortedByType(sortType)
        }
}