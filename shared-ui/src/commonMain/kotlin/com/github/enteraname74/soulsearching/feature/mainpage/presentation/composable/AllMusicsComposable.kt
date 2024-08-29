package com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.composables.MusicItemComposable
import com.github.enteraname74.soulsearching.coreui.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.ViewSettingsManager
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.AllMusicsState
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AllMusicsComposable(
    musicState: AllMusicsState,
    navigateToFolder: (folderPath: String) -> Unit,
    navigateToMonth: (month: String) -> Unit,
    setSortType: (Int) -> Unit,
    toggleSortDirection: () -> Unit = {},
    isUsingSort: Boolean = true,
    onLongClick: (Music) -> Unit,
    playbackManager: PlaybackManager = injectElement(),
    viewSettingsManager: ViewSettingsManager = injectElement(),
    playerViewManager: PlayerViewManager = injectElement(),
) {
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (viewSettingsManager.areMusicsByFoldersShown) {
            item(
                key = ALL_MUSICS_FOLDER_KEY,
                contentType = ALL_MUSICS_FOLDER_CONTENT_TYPE,
            ) {
                MusicFoldersHorizontalList(
                    folders = musicState.folderMusics,
                    onFolderClicked = navigateToFolder,
                    onFolderLongClicked = {}
                )
            }
        }
        if (viewSettingsManager.areMusicsByMonthsShown) {
            item(
                key = ALL_MUSICS_MONTH_KEY,
                contentType = ALL_MUSICS_MONTH_STICKY_HEADER,
            ) {
                MusicMonthsHorizontalList(
                    months = musicState.monthMusics,
                    onMonthClicked = navigateToMonth,
                    onMonthLongClicked = {}
                )
            }
        }
        stickyHeader(
            key = ALL_MUSICS_STICKY_HEADER_KEY,
            contentType = ALL_MUSICS_STICKY_CONTENT_TYPE,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = SoulSearchingColorTheme.colorScheme.primary)
                    .padding(bottom = UiConstants.Spacing.large),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SubMenuComposable(
                    title = strings.musics,
                    setSortType = setSortType,
                    toggleSortDirection = toggleSortDirection,
                    sortType = musicState.sortType,
                    sortDirection = musicState.sortDirection,
                    isUsingSort = isUsingSort,
                    rightComposable = {
                        Icon(
                            modifier = Modifier
                                .padding(start = UiConstants.Spacing.medium)
                                .size(30.dp)
                                .clickable {
                                    if (musicState.musics.isNotEmpty()) {
                                        coroutineScope
                                            .launch {
                                                playerViewManager.animateTo(newState = BottomSheetStates.EXPANDED)
                                            }
                                            .invokeOnCompletion {
                                                playbackManager.playShuffle(musicList = musicState.musics)
                                            }
                                    }
                                },
                            imageVector = Icons.Rounded.Shuffle,
                            contentDescription = strings.shuffleButton,
                            tint = SoulSearchingColorTheme.colorScheme.onPrimary
                        )
                    }
                )
            }
        }
        if (musicState.musics.isNotEmpty()) {
            items(
                key = { it.musicId },
                contentType = { ALL_MUSICS_CONTENT_TYPE },
                items = musicState.musics
            ) { elt ->
                MusicItemComposable(
                    modifier = Modifier
                        .animateItemPlacement(),
                    music = elt,
                    onClick = {
                        coroutineScope.launch {
                            playerViewManager.animateTo(newState = BottomSheetStates.EXPANDED)
                        }.invokeOnCompletion {
                            playbackManager.setCurrentPlaylistAndMusic(
                                music = elt,
                                musicList = musicState.musics,
                                isMainPlaylist = true,
                                playlistId = null,
                            )
                        }
                    },
                    onLongClick = {
                        coroutineScope.launch {
                            onLongClick(elt)
                        }
                    },
                    isPlayedMusic = playbackManager.isSameMusicAsCurrentPlayedOne(elt.musicId)
                )
            }
            item(
                key = ALL_MUSICS_SPACER_KEY,
                contentType = ALL_MUSICS_SPACER_CONTENT_TYPE,
            ) {
                SoulPlayerSpacer()
            }
        } else {
            item (
                key = ALL_MUSICS_NO_ELEMENT_KEY,
                contentType = ALL_MUSICS_NO_ELEMENT_CONTENT_TYPE,
            ){
                NoElementView()
            }
        }
    }
}

private const val ALL_MUSICS_FOLDER_KEY: String = "ALL_MUSICS_FOLDER_KEY"
private const val ALL_MUSICS_FOLDER_CONTENT_TYPE: String = "ALL_MUSICS_FOLDER_CONTENT_TYPE"
private const val ALL_MUSICS_MONTH_KEY: String = "ALL_MUSICS_MONTH_KEY"
private const val ALL_MUSICS_MONTH_STICKY_HEADER: String = "ALL_MUSICS_MONTH_STICKY_HEADER"
private const val ALL_MUSICS_STICKY_HEADER_KEY: String = "ALL_MUSICS_STICKY_HEADER_KEY"
private const val ALL_MUSICS_STICKY_CONTENT_TYPE: String = "ALL_MUSICS_STICKY_CONTENT_TYPE"
private const val ALL_MUSICS_CONTENT_TYPE: String = "ALL_MUSICS_CONTENT_TYPE"
private const val ALL_MUSICS_SPACER_KEY: String = "ALL_MUSICS_SPACER_KEY"
private const val ALL_MUSICS_SPACER_CONTENT_TYPE: String = "ALL_MUSICS_SPACER_CONTENT_TYPE"
private const val ALL_MUSICS_NO_ELEMENT_KEY: String = "ALL_MUSICS_NO_ELEMENT_KEY"
private const val ALL_MUSICS_NO_ELEMENT_CONTENT_TYPE: String = "ALL_MUSICS_NO_ELEMENT_CONTENT_TYPE"
