package com.github.enteraname74.soulsearching.feature.player.presentation.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.composables.SoulImage
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.combinedClickableWithRightClick
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.ViewSettingsManager
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlayerMusicCover(
    playbackManager: PlaybackManager = injectElement(),
    playerViewManager: PlayerViewManager = injectElement(),
    imageSize: Dp,
    horizontalPadding: Dp,
    topPadding: Dp,
    onLongClick: () -> Unit,
    canSwipeCover: Boolean,
    aroundSongs: List<Music?>,
) {
    val imageModifier = if (playerViewManager.currentValue == BottomSheetStates.EXPANDED) {
        Modifier.combinedClickableWithRightClick(
            onLongClick = onLongClick,
            onClick = { }
        )
    } else {
        Modifier
    }

    Box(
        modifier = Modifier
            .padding(UiConstants.Spacing.small)
    ) {
        Box(
            modifier = Modifier
                .padding(
                    start = horizontalPadding,
                    top = topPadding,
                    end = horizontalPadding,
                )
        ) {
            if (
                aroundSongs.filterNotNull().size > 1
                && playerViewManager.currentValue == BottomSheetStates.EXPANDED
                && canSwipeCover
            ) {
                val pagerState = remember(aroundSongs) {
                    object : PagerState(currentPage = 1) {
                        override val pageCount: Int = aroundSongs.size
                    }
                }

                LaunchedEffect(pagerState) {
                    snapshotFlow { pagerState.currentPage }.collect { page ->
                        CoroutineScope(Dispatchers.IO).launch {
                            when (page) {
                                0 -> playbackManager.previous()
                                2 -> playbackManager.next()
                            }
                        }
                    }
                }

                HorizontalPager(
                    state = pagerState,
                    pageSpacing = 120.dp
                ) { currentSongPos ->
                    SoulImage(
                        modifier = imageModifier,
                        coverId = aroundSongs.getOrNull(currentSongPos)?.coverId,
                        size = imageSize,
                        roundedPercent = (playerViewManager.offset / 100).roundToInt()
                            .coerceIn(3, 10)
                    )
                }
            } else {
                SoulImage(
                    modifier = imageModifier,
                    coverId = playbackManager.currentMusic?.coverId,
                    size = imageSize,
                    roundedPercent = (playerViewManager.offset / 100).roundToInt()
                        .coerceIn(3, 10)
                )
            }
        }
    }
}