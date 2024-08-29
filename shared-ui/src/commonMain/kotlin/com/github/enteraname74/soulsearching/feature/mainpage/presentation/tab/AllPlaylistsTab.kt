package com.github.enteraname74.soulsearching.feature.mainpage.presentation.tab

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.github.enteraname74.domain.model.SortDirection
import com.github.enteraname74.soulsearching.composables.BigPreviewComposable
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
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
                leftComposable = {
                    Icon(
                        modifier = Modifier
                            .clickable { mainPageViewModel.showCreatePlaylistDialog() }
                            .size(UiConstants.ImageSize.medium),
                        imageVector = Icons.Rounded.Add,
                        contentDescription = strings.createPlaylistButton,
                        tint = SoulSearchingColorTheme.colorScheme.onPrimary
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
            ) { element ->
                BigPreviewComposable(
                    coverId = element.playlist.coverId,
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