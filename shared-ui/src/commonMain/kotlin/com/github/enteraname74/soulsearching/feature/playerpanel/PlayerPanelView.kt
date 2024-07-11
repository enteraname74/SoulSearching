package com.github.enteraname74.soulsearching.feature.playerpanel

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.github.enteraname74.domain.model.ImageCover
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.navigation.SoulBackHandler
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.domain.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.PagerScreen
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerState
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import com.github.enteraname74.soulsearching.feature.playerpanel.composable.MusicLyricsView
import com.github.enteraname74.soulsearching.feature.playerpanel.composable.PlayerListView
import com.github.enteraname74.soulsearching.feature.playerpanel.composable.PlayerPanelTab
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
@Suppress("Deprecation")
fun PlayerPanelView(
    maxHeight: Float,
    musicListDraggableState: SwipeableState<BottomSheetStates>,
    playbackManager: PlaybackManager = injectElement(),
    playerState: PlayerState,
    onSelectedMusic: (Music) -> Unit,
    coverList: ArrayList<ImageCover>,
    onRetrieveLyrics: () -> Unit,
    primaryColor: Color,
    secondaryColor: Color,
    contentColor: Color,
    subTextColor: Color
) {
    val coroutineScope = rememberCoroutineScope()

    val isExpanded by remember {
        derivedStateOf {
            musicListDraggableState.currentValue == BottomSheetStates.EXPANDED
        }
    }

    SoulBackHandler(isExpanded) {
        coroutineScope.launch {
            musicListDraggableState.animateTo(
                BottomSheetStates.COLLAPSED,
                tween(UiConstants.AnimationDuration.normal)
            )
        }
    }

    val topPanelSize = with(LocalDensity.current) { 64.dp.toPx() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .offset {
                IntOffset(
                    x = 0,
                    y = max(musicListDraggableState.offset.value.roundToInt(), 0)
                )
            }
            .swipeable(
                state = musicListDraggableState,
                orientation = Orientation.Vertical,
                anchors = mapOf(
                    0f to BottomSheetStates.EXPANDED,
                    (maxHeight - topPanelSize) to BottomSheetStates.COLLAPSED,
                )
            )
    ) {

        val pages = listOf(
            PagerScreen(
                title = strings.playedList,
                screen = {
                    PlayerListView(
                        playbackManager = playbackManager,
                        musicListDraggableState = musicListDraggableState,
                        playedList = playerState.playedList,
                        onSelectedMusic = onSelectedMusic,
                        coverList = coverList,
                        secondaryColor = contentColor,
                        primaryColor = primaryColor
                    )
                }
            ),
            PagerScreen(
                title = strings.lyrics,
                screen = {
                    MusicLyricsView(
                        contentColor = contentColor,
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

        /**
         * Handles the click on a tab.
         * If the panel is expanded and we click on the current tab,
         * it will close the panel.
         */
        fun onTabClicked(tabPos: Int) {
            val currentFocusedTab = pagerState.currentPage
            val isSameTab = tabPos == currentFocusedTab
            if (isSameTab && isExpanded) {
                coroutineScope.launch {
                    musicListDraggableState.animateTo(
                        BottomSheetStates.COLLAPSED,
                        tween(UiConstants.AnimationDuration.normal)
                    )
                }
            } else {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(
                        page = tabPos
                    )
                }
                if (!isExpanded) {
                    coroutineScope.launch {
                        musicListDraggableState.animateTo(
                            BottomSheetStates.EXPANDED,
                            tween(UiConstants.AnimationDuration.normal)
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = secondaryColor,
                    shape = RoundedCornerShape(
                        topStartPercent = 4,
                        topEndPercent = 4
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Spacer(
                    modifier = Modifier
                        .background(
                            color = subTextColor,
                            shape = RoundedCornerShape(percent = 50)
                        )
                        .height(4.dp)
                        .width(40.dp)
                )
            }
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
                        contentColor = if (isSelected && isExpanded) contentColor else subTextColor,
                        isSelected = isExpanded && isSelected,
                        onSelected = {
                            onTabClicked(tabPos = index)
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
}
