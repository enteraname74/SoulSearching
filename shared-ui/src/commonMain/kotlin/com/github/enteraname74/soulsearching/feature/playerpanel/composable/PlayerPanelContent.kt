package com.github.enteraname74.soulsearching.feature.playerpanel.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonColors
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.TabData
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.player.domain.state.PlayerViewState
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerMusicListViewManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlayerPanelContent(
    playbackManager: PlaybackManager = injectElement(),
    playerMusicListViewManager: PlayerMusicListViewManager = injectElement(),
    playerState: PlayerViewState.Data,
    onSelectedMusic: (Music) -> Unit,
    onRetrieveLyrics: () -> Unit,
    textColor: Color,
    subTextColor: Color,
    isExpanded: Boolean,
    buttonColors: SoulButtonColors,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()

    val pages = listOf(
        TabData(
            title = strings.playedList,
            screen = {
                PlayerListView(
                    playbackManager = playbackManager,
                    playedList = playerState.playedList,
                    onSelectedMusic = onSelectedMusic,
                    secondaryColor = textColor,
                    isExpanded = isExpanded,
                    buttonColors = buttonColors,
                    currentMusicIndex = playerState.currentMusicIndex,
                )
            }
        ),
        TabData(
            title = strings.lyrics,
            screen = {
                MusicLyricsView(
                    contentColor = textColor,
                    noLyricsColor = subTextColor,
                    lyricsState = playerState.currentMusicLyrics,
                    currentMusic = playerState.currentMusic,
                    onRetrieveLyrics = onRetrieveLyrics,
                    isExpanded = isExpanded,
                )
            }
        )
    )

    val pagerState = rememberPagerState(
        pageCount = { pages.size }
    )

    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = UiConstants.Spacing.medium,
                    end = UiConstants.Spacing.medium,
                    bottom = UiConstants.Spacing.small
                ),
        ) {
            pages.forEachIndexed { index, page ->
                val isSelected = pagerState.currentPage == index

                PlayerPanelTab(
                    modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth()
                        .weight(1f),
                    title = page.title,
                    contentColor = if (isSelected && isExpanded) textColor else subTextColor,
                    isSelected = isExpanded && isSelected,
                    onSelected = {
                        coroutineScope.launch {
                            val currentFocusedTab = pagerState.currentPage
                            val isSameTab = index == currentFocusedTab
                            if (isSameTab && isExpanded) {
                                coroutineScope.launch {
                                    playerMusicListViewManager.animateTo(BottomSheetStates.COLLAPSED)
                                }
                            } else {
                                if (!isExpanded) {
                                    coroutineScope.launch {
                                        playerMusicListViewManager.animateTo(BottomSheetStates.EXPANDED)
                                    }

                                }
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(
                                        page = index
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false
        ) { pagePosition ->
            pages[pagePosition].screen()
        }
    }
}