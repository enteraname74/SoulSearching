package com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.composables.MusicItemComposable
import com.github.enteraname74.soulsearching.coreui.composable.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulIconButton
import com.github.enteraname74.soulsearching.coreui.list.LazyColumnCompat
import com.github.enteraname74.soulsearching.coreui.multiselection.MultiSelectionState
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.ViewSettingsManager
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.AllMusicsState
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AllMusicsComposable(
    musicState: AllMusicsState,
    multiSelectionState: MultiSelectionState,
    navigateToMonth: (month: String) -> Unit,
    setSortType: (Int) -> Unit,
    toggleSortDirection: () -> Unit = {},
    isUsingSort: Boolean = true,
    onLongClick: (Music) -> Unit,
    onMoreClick: (Music) -> Unit,
    playbackManager: PlaybackManager = injectElement(),
    viewSettingsManager: ViewSettingsManager = injectElement(),
    playerViewManager: PlayerViewManager = injectElement(),
) {
    val coroutineScope = rememberCoroutineScope()
    val currentPlayedSong: Music? by playbackManager.currentSong.collectAsState()

    LazyColumnCompat(
        modifier = Modifier
            .fillMaxSize()
    ) {
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
            SubMenuComposable(
                modifier = Modifier
                    .padding(
                        bottom = UiConstants.Spacing.mediumPlus,
                    ),
                title = strings.musics,
                setSortType = setSortType,
                toggleSortDirection = toggleSortDirection,
                sortType = musicState.sortType,
                sortDirection = musicState.sortDirection,
                isUsingSort = isUsingSort,
                rightComposable = {
                    SoulIconButton(
                        icon = Icons.Rounded.Shuffle,
                        contentDescription = strings.shuffleButton,
                        enabled = musicState.musics.isNotEmpty(),
                        onClick = {
                            if (musicState.musics.isNotEmpty()) {
                                coroutineScope.launch {
                                    playbackManager.playShuffle(musicList = musicState.musics)
                                    playerViewManager.animateTo(BottomSheetStates.EXPANDED)
                                }
                            }
                        }
                    )
                }
            )
        }
        if (musicState.musics.isNotEmpty()) {
            items(
                key = { it.musicId },
                contentType = { ALL_MUSICS_CONTENT_TYPE },
                items = musicState.musics
            ) { elt ->
                MusicItemComposable(
                    modifier = Modifier
                        .animateItem(),
                    music = elt,
                    onClick = {
                        coroutineScope.launch {
                            playbackManager.setCurrentPlaylistAndMusic(
                                music = elt,
                                musicList = musicState.musics,
                                isMainPlaylist = true,
                                playlistId = null,
                            )
                            playerViewManager.animateTo(BottomSheetStates.EXPANDED)
                        }
                    },
                    onMoreClicked = {
                        coroutineScope.launch {
                            onMoreClick(elt)
                        }
                    },
                    onLongClick = { onLongClick(elt) },
                    isPlayedMusic = currentPlayedSong?.musicId == elt.musicId,
                    isSelected = multiSelectionState.selectedIds.contains(elt.musicId),
                    isSelectionModeOn = multiSelectionState.selectedIds.isNotEmpty(),
                )
            }
            item(
                key = ALL_MUSICS_SPACER_KEY,
                contentType = ALL_MUSICS_SPACER_CONTENT_TYPE,
            ) {
                SoulPlayerSpacer()
            }
        } else {
            item(
                key = ALL_MUSICS_NO_ELEMENT_KEY,
                contentType = ALL_MUSICS_NO_ELEMENT_CONTENT_TYPE,
            ) {
                NoElementView()
            }
        }
    }
}

private const val ALL_MUSICS_MONTH_KEY: String = "ALL_MUSICS_MONTH_KEY"
private const val ALL_MUSICS_MONTH_STICKY_HEADER: String = "ALL_MUSICS_MONTH_STICKY_HEADER"
private const val ALL_MUSICS_STICKY_HEADER_KEY: String = "ALL_MUSICS_STICKY_HEADER_KEY"
private const val ALL_MUSICS_STICKY_CONTENT_TYPE: String = "ALL_MUSICS_STICKY_CONTENT_TYPE"
private const val ALL_MUSICS_CONTENT_TYPE: String = "ALL_MUSICS_CONTENT_TYPE"
private const val ALL_MUSICS_SPACER_KEY: String = "ALL_MUSICS_SPACER_KEY"
private const val ALL_MUSICS_SPACER_CONTENT_TYPE: String = "ALL_MUSICS_SPACER_CONTENT_TYPE"
private const val ALL_MUSICS_NO_ELEMENT_KEY: String = "ALL_MUSICS_NO_ELEMENT_KEY"
private const val ALL_MUSICS_NO_ELEMENT_CONTENT_TYPE: String = "ALL_MUSICS_NO_ELEMENT_CONTENT_TYPE"
