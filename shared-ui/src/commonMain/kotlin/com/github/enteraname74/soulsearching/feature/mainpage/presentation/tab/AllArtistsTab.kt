package com.github.enteraname74.soulsearching.feature.mainpage.presentation.tab

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.github.enteraname74.domain.model.SortDirection
import com.github.enteraname74.soulsearching.composables.BigPreviewComposable
import com.github.enteraname74.soulsearching.coreui.multiselection.MultiSelectionState
import com.github.enteraname74.soulsearching.coreui.multiselection.SelectionMode
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.ElementEnum
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.PagerScreen
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.AllArtistsState
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel.MainPageViewModel
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable.MainPageList
import java.util.*

fun allArtistsTab(
    mainPageViewModel: MainPageViewModel,
    navigateToArtist: (artistId: UUID) -> Unit,
): PagerScreen = PagerScreen(
    type = ElementEnum.ARTISTS,
    screen = {

        val artistState: AllArtistsState by mainPageViewModel.allArtistsState.collectAsState()
        val multiSelectionState: MultiSelectionState by mainPageViewModel.multiSelectionState.collectAsState()

        MainPageList(
            list = artistState.artists,
            title = strings.artists,
            toggleSortDirection = {
                val newDirection =
                    if (artistState.sortDirection == SortDirection.ASC) {
                        SortDirection.DESC
                    } else {
                        SortDirection.ASC
                    }
                mainPageViewModel.setArtistSortDirection(newDirection)
            },
            setSortType = mainPageViewModel::setArtistSortType,
            sortType = artistState.sortType,
            sortDirection = artistState.sortDirection,
            key = { it.artist.artistId },
            contentType = { ALL_ARTISTS_CONTENT_TYPE },
        ) { element ->
            BigPreviewComposable(
                modifier = Modifier
                    .animateItem(),
                cover = element.cover,
                title = element.artist.artistName,
                text = strings.musics(element.musics.size),
                imageSize = null,
                onClick = {
                    navigateToArtist(element.artist.artistId)
                },
                onLongClick = {
                    mainPageViewModel.toggleElementInSelection(
                        id = element.artist.artistId,
                        mode = SelectionMode.Artist,
                    )
                },
                isSelected = multiSelectionState.selectedIds.contains(element.artist.artistId),
                isSelectionModeOn = multiSelectionState.selectedIds.isNotEmpty(),
            )
        }
    }
)

private const val ALL_ARTISTS_CONTENT_TYPE = "ALL_ARTISTS_CONTENT_TYPE"