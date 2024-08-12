package com.github.enteraname74.soulsearching.feature.mainpage.presentation.tab

import com.github.enteraname74.domain.model.SortDirection
import com.github.enteraname74.domain.model.SortType
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.domain.events.AlbumEvent
import com.github.enteraname74.soulsearching.feature.coversprovider.AllImageCoversViewModel
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.PagerScreen
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.AlbumState
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel.AllAlbumsViewModel
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable.AllElementsComposable

fun allAlbumsTab(
    allAlbumsViewModel: AllAlbumsViewModel,
    allImageCoversViewModel: AllImageCoversViewModel,
    albumState: AlbumState,
    navigateToAlbum: (albumId: String) -> Unit,
): PagerScreen = PagerScreen(
    title = strings.albums,
    screen = {
        AllElementsComposable(
            navigateToAlbum = navigateToAlbum,
            retrieveCoverMethod = allImageCoversViewModel::getImageCover,
            list = albumState.albums,
            title = strings.albums,
            albumBottomSheetAction = allAlbumsViewModel::showAlbumBottomSheet,
            sortByName = {
                allAlbumsViewModel.onAlbumEvent(
                    AlbumEvent.SetSortType(SortType.NAME)
                )
            },
            sortByMostListenedAction = {
                allAlbumsViewModel.onAlbumEvent(
                    AlbumEvent.SetSortType(SortType.NB_PLAYED)
                )
            },
            sortByDateAction = {
                allAlbumsViewModel.onAlbumEvent(
                    AlbumEvent.SetSortType(SortType.ADDED_DATE)
                )
            },
            setSortDirectionAction = {
                val newDirection =
                    if (albumState.sortDirection == SortDirection.ASC) {
                        SortDirection.DESC
                    } else {
                        SortDirection.ASC
                    }
                allAlbumsViewModel.onAlbumEvent(
                    AlbumEvent.SetSortDirection(newDirection)
                )
            },
            sortType = albumState.sortType,
            sortDirection = albumState.sortDirection
        )
    }
)