package com.github.enteraname74.soulsearching.feature.mainpage.presentation.tab

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.enteraname74.domain.model.SortDirection
import com.github.enteraname74.soulsearching.composables.BigPreviewComposable
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.ElementEnum
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.PagerScreen
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.AllAlbumsState
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel.MainPageViewModel
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable.MainPageList
import java.util.*

fun allAlbumsTab(
    mainPageViewModel: MainPageViewModel,
    navigateToAlbum: (albumId: UUID) -> Unit,
): PagerScreen = PagerScreen(
    type = ElementEnum.ALBUMS,
    screen = {

        val albumState: AllAlbumsState by mainPageViewModel.allAlbumsState.collectAsState()

        MainPageList(
            list = albumState.albums,
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
            sortDirection = albumState.sortDirection
        ) { element ->
            BigPreviewComposable(
                coverId = element.album.coverId,
                title = element.album.albumName,
                text = element.artist?.artistName.orEmpty(),
                onClick = {
                    navigateToAlbum(element.album.albumId)
                },
                onLongClick = {
                    mainPageViewModel.showAlbumBottomSheet(element.album)
                }
            )
        }
    }
)