package com.github.enteraname74.soulsearching.feature.mainpage.presentation.tab

import com.github.enteraname74.domain.model.SortDirection
import com.github.enteraname74.domain.model.SortType
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.domain.events.ArtistEvent
import com.github.enteraname74.soulsearching.feature.coversprovider.AllImageCoversViewModel
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.PagerScreen
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.ArtistState
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel.AllArtistsViewModel
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable.AllElementsComposable

fun allArtistsTab(
    allArtistsViewModel: AllArtistsViewModel,
    allImageCoversViewModel: AllImageCoversViewModel,
    artistState: ArtistState,
    navigateToArtist: (artistId: String) -> Unit,
): PagerScreen = PagerScreen(
    title = strings.artists,
    screen = {
        AllElementsComposable(
            navigateToArtist = navigateToArtist,
            retrieveCoverMethod = allImageCoversViewModel::getImageCover,
            list = artistState.artists,
            title = strings.artists,
            artistBottomSheetAction = allArtistsViewModel::showArtistBottomSheet,
            sortByName = {
                allArtistsViewModel.onArtistEvent(
                    ArtistEvent.SetSortType(SortType.NAME)
                )
            },
            sortByMostListenedAction = {
                allArtistsViewModel.onArtistEvent(
                    ArtistEvent.SetSortType(SortType.NB_PLAYED)
                )
            },
            sortByDateAction = {
                allArtistsViewModel.onArtistEvent(
                    ArtistEvent.SetSortType(SortType.ADDED_DATE)
                )
            },
            setSortDirectionAction = {
                val newDirection =
                    if (artistState.sortDirection == SortDirection.ASC) {
                        SortDirection.DESC
                    } else {
                        SortDirection.ASC
                    }
                allArtistsViewModel.onArtistEvent(
                    ArtistEvent.SetSortDirection(newDirection)
                )
            },
            sortType = artistState.sortType,
            sortDirection = artistState.sortDirection
        )
    }
)