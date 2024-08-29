package com.github.enteraname74.soulsearching.feature.playerpanel.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.TabData
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerState
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerMusicListViewManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlayerPanelContent(
    playbackManager: PlaybackManager = injectElement(),
    playerMusicListViewManager: PlayerMusicListViewManager = injectElement(),
    playerState: PlayerState,
    onSelectedMusic: (Music) -> Unit,
    onRetrieveLyrics: () -> Unit,
    primaryColor: Color,
    buttonTextColor: Color,
    textColor: Color,
    subTextColor: Color,
    isExpanded: Boolean,
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
                    primaryColor = primaryColor,
                    isExpanded = isExpanded,
                    buttonTextColor = buttonTextColor,
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
                    onRetrieveLyrics = onRetrieveLyrics
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
                            onTabClicked(
                                tabPos = index,
                                pagerState = pagerState,
                                onAnimatePanel = playerMusicListViewManager::animateTo,
                                isExpanded = isExpanded,
                            )
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

/**
 * Handles the click on a tab.
 * If the panel is expanded, and we click on the current tab,
 * it will close the panel.
 */
@OptIn(ExperimentalFoundationApi::class)
private suspend fun onTabClicked(
    tabPos: Int,
    pagerState: PagerState,
    onAnimatePanel: suspend (newState: BottomSheetStates) -> Unit,
    isExpanded: Boolean,
) {
    val currentFocusedTab = pagerState.currentPage
    val isSameTab = tabPos == currentFocusedTab
    if (isSameTab && isExpanded) {
        onAnimatePanel(BottomSheetStates.COLLAPSED)
    } else {
        pagerState.animateScrollToPage(
            page = tabPos
        )
        if (!isExpanded) {
            onAnimatePanel(BottomSheetStates.EXPANDED)
        }
    }
}