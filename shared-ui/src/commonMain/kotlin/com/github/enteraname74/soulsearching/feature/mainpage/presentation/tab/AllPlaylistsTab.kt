package com.github.enteraname74.soulsearching.feature.mainpage.presentation.tab

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.ui.Modifier
import com.github.enteraname74.domain.model.SortDirection
import com.github.enteraname74.domain.model.SortType
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.domain.events.PlaylistEvent
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.PagerScreen
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.PlaylistState
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel.AllPlaylistsViewModel
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable.AllElementsComposable

fun allPlaylistsTab(
    allPlaylistsViewModel: AllPlaylistsViewModel,
    playlistState: PlaylistState,
    navigateToPlaylist: (playlistId: String) -> Unit,
): PagerScreen = PagerScreen(
    title = strings.playlists,
    screen = {
        AllElementsComposable(
            list = playlistState.playlists,
            title = strings.playlists,
            navigateToPlaylist = navigateToPlaylist,
            playlistBottomSheetAction = allPlaylistsViewModel::showPlaylistBottomSheet,
            createPlaylistComposable = {
                Icon(
                    modifier = Modifier
                        .clickable {
                            allPlaylistsViewModel.showCreatePlaylistDialog()
                        }
                        .size(UiConstants.ImageSize.medium),
                    imageVector = Icons.Rounded.Add,
                    contentDescription = strings.createPlaylistButton,
                    tint = SoulSearchingColorTheme.colorScheme.onPrimary
                )
            },
            sortByName = {
                allPlaylistsViewModel.onPlaylistEvent(
                    PlaylistEvent.SetSortType(SortType.NAME)
                )
            },
            sortByDateAction = {
                allPlaylistsViewModel.onPlaylistEvent(
                    PlaylistEvent.SetSortType(SortType.ADDED_DATE)
                )
            },
            sortByMostListenedAction = {
                allPlaylistsViewModel.onPlaylistEvent(
                    PlaylistEvent.SetSortType(SortType.NB_PLAYED)
                )
            },
            setSortDirectionAction = {
                val newDirection =
                    if (playlistState.sortDirection == SortDirection.ASC) {
                        SortDirection.DESC
                    } else {
                        SortDirection.ASC
                    }
                allPlaylistsViewModel.onPlaylistEvent(
                    PlaylistEvent.SetSortDirection(newDirection)
                )
            },
            sortType = playlistState.sortType,
            sortDirection = playlistState.sortDirection
        )
    }
)