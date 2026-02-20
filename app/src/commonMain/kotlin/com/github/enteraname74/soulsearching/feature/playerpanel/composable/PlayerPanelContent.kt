package com.github.enteraname74.soulsearching.feature.playerpanel.composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonColors
import com.github.enteraname74.soulsearching.coreui.ext.toDp
import com.github.enteraname74.soulsearching.coreui.multiselection.MultiSelectionState
import com.github.enteraname74.soulsearching.coreui.multiselection.composable.SoulSelectedIconColors
import com.github.enteraname74.soulsearching.coreui.multiselection.composable.SoulSelectedIconDefaults
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.utils.getNavigationBarPadding
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.TabData
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.player.domain.model.LyricsFetchState
import com.github.enteraname74.soulsearching.feature.player.domain.state.PlayerViewState
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerMusicListViewManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.coroutines.launch

@Composable
fun PlayerPanelContent(
    playerState: PlayerViewState.Data,
    lyricsState: LyricsFetchState,
    onMoreClickedOnMusic: (Music) -> Unit,
    onLongSelectOnMusic: (Music) -> Unit,
    onActivateRemoteLyrics: () -> Unit,
    multiSelectionState: MultiSelectionState,
    contentColor: Color,
    subTextColor: Color,
    containerColor: Color,
    isExpanded: Boolean,
    buttonColors: SoulButtonColors,
    modifier: Modifier = Modifier,
    playbackManager: PlaybackManager = injectElement(),
    playerMusicListViewManager: PlayerMusicListViewManager = injectElement(),
    selectedIconColors: SoulSelectedIconColors = SoulSelectedIconDefaults.secondary(),
) {
    val coroutineScope = rememberCoroutineScope()
    val playedList = playerState.playedList.collectAsLazyPagingItems()

    val pages = listOf(
        TabData(
            title = strings.playedList,
            screen = {
                PlayerListView(
                    playbackManager = playbackManager,
                    playedList = playedList,
                    onMoreClickedOnMusic = onMoreClickedOnMusic,
                    contentColor = contentColor,
                    containerColor = containerColor,
                    isExpanded = isExpanded,
                    buttonColors = buttonColors,
                    currentMusicIndex = playerState.currentMusicIndex,
                    onLongSelectOnMusic = onLongSelectOnMusic,
                    multiSelectionState = multiSelectionState,
                    selectedIconColors = selectedIconColors,
                )
            }
        ),
        TabData(
            title = strings.lyrics,
            screen = {
                MusicLyricsView(
                    contentColor = contentColor,
                    containerColor = containerColor,
                    noLyricsColor = subTextColor,
                    lyricsState = lyricsState,
                    isExpanded = isExpanded,
                    buttonColor = buttonColors,
                    onActivateRemoteLyrics = onActivateRemoteLyrics,
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
                    contentColor = if (isSelected && isExpanded) contentColor else subTextColor,
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