package com.github.enteraname74.soulsearching.feature.mainpage.presentation.tab

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.github.enteraname74.domain.model.SortDirection
import com.github.enteraname74.soulsearching.composables.BigPreviewComposable
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulIconButton
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.ElementEnum
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.PagerScreen
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.AllPlaylistsState
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel.MainPageViewModel
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable.MainPageList
import java.util.*

fun allPlaylistsTab(
    mainPageViewModel: MainPageViewModel,
    navigateToPlaylist: (playlistId: UUID) -> Unit,
): PagerScreen =
    PagerScreen(
        type = ElementEnum.PLAYLISTS,
        screen = {
            val playlistState: AllPlaylistsState by mainPageViewModel.allPlaylistsState.collectAsState()

            MainPageList(
                list = playlistState.playlists,
                title = strings.playlists,
                rightComposable = {
                    SoulIconButton(
                        onClick = mainPageViewModel::showCreatePlaylistDialog,
                        icon = Icons.Rounded.Add,
                        contentDescription = strings.createPlaylistButton,
                        size = UiConstants.ImageSize.medium,
                    )
                },
                setSortType = mainPageViewModel::setPlaylistSortType,
                toggleSortDirection = {
                    val newDirection = if (playlistState.sortDirection == SortDirection.ASC) {
                        SortDirection.DESC
                    } else {
                        SortDirection.ASC
                    }
                    mainPageViewModel.setPlaylistSortDirection(newDirection)
                },
                sortType = playlistState.sortType,
                sortDirection = playlistState.sortDirection,
                key = { it.playlist.playlistId },
                contentType = { ALL_PLAYLISTS_CONTENT_TYPE }
            ) { element ->
                BigPreviewComposable(
                    modifier = Modifier
                        .animateItem(),
                    cover = element.cover,
                    title = element.playlist.name,
                    text = strings.musics(element.musicsNumber),
                    onClick = {
                        navigateToPlaylist(element.playlist.playlistId)
                    },
                    onLongClick = { mainPageViewModel.showPlaylistBottomSheet(element.playlist) },
                    isFavoritePlaylist = element.playlist.isFavorite
                )
            }
        }
    )

private const val ALL_PLAYLISTS_CONTENT_TYPE = "ALL_PLAYLISTS_CONTENT_TYPE"