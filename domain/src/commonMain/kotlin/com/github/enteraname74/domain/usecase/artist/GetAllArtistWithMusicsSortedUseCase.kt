package com.github.enteraname74.domain.usecase.artist

import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.model.SortDirection
import com.github.enteraname74.domain.model.SortType
import com.github.enteraname74.domain.repository.ArtistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAllArtistWithMusicsSortedUseCase(
    private val artistRepository: ArtistRepository,
) {
    operator fun invoke(
        sortDirection: SortDirection,
        sortType: SortType,
    ): Flow<List<ArtistWithMusics>> =
        artistRepository.getAllArtistWithMusics().map { list ->
            list.sorted(
                sortDirection = sortDirection,
                sortType = sortType,
            )
        }

    private fun List<ArtistWithMusics>.sortedByType(sortType: SortType): List<ArtistWithMusics> =
        when (sortType) {
            SortType.NB_PLAYED -> this.sortedBy { it.artist.nbPlayed }
            SortType.ADDED_DATE -> this.sortedBy{ it.artist.addedDate }
            SortType.NAME -> this.sortedBy { it.artist.artistName }
        }

    private fun List<ArtistWithMusics>.sorted(
        sortDirection: SortDirection,
        sortType: SortType,
    ): List<ArtistWithMusics> =
        when(sortDirection) {
            SortDirection.DESC -> this.sortedByType(sortType).asReversed()
            SortDirection.ASC -> this.sortedByType(sortType)
        }
}