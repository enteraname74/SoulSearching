package com.github.enteraname74.soulsearching.feature.mainpage.presentation.tab

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.paging.compose.collectAsLazyPagingItems
import com.github.enteraname74.domain.model.SortDirection
import com.github.enteraname74.soulsearching.composables.BigPreviewComposable
import com.github.enteraname74.soulsearching.coreui.multiselection.MultiSelectionState
import com.github.enteraname74.soulsearching.coreui.multiselection.SelectionMode
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.ElementEnum
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.PagerScreen
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.AllAlbumsState
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel.MainPageViewModel
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable.MainPageListPaged
import java.util.UUID

fun allAlbumsTab(
    mainPageViewModel: MainPageViewModel,
    navigateToAlbum: (albumId: UUID) -> Unit,
): PagerScreen = PagerScreen(
    type = ElementEnum.ALBUMS,
    screen = {

        val albumState: AllAlbumsState by mainPageViewModel.allAlbumsState.collectAsState()
        val multiSelectionState: MultiSelectionState by mainPageViewModel.multiSelectionState.collectAsState()

        val albums = albumState.albums.collectAsLazyPagingItems()

        MainPageListPaged(
            list = albums,
            title = strings.albums,
            setSortType = mainPageViewModel::setAlbumSortType,
            toggleSortDirection = {
                val newDirection =
                    if (albumState.sortDirection == SortDirection.ASC) {
                        SortDirection.DESC
                    } else {
                        SortDirection.ASC
                    }
                mainPageViewModel.setAlbumSortDirection(newDirection)
            },
            sortType = albumState.sortType,
            sortDirection = albumState.sortDirection,
            key = { it?.id },
            contentType = ALL_ALBUMS_CONTENT_TYPE,
        ) { element ->
            BigPreviewComposable(
                modifier = Modifier
                    .animateItem(),
                cover = element.cover,
                title = element.name,
                text = element.artist,
                onClick = {
                    navigateToAlbum(element.id)
                },
                imageSize = null,
                onLongClick = {
                    mainPageViewModel.toggleElementInSelection(
                        id = element.id,
                        mode = SelectionMode.Album,
                    )
                },
                isSelected = multiSelectionState.selectedIds.contains(element.id),
                isSelectionModeOn = multiSelectionState.selectedIds.isNotEmpty(),
            )
        }
    }
)

private const val ALL_ALBUMS_CONTENT_TYPE: String = "ALL_ALBUMS_CONTENT_TYPE"