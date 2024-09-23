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

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun PlayerMusicCover(
    playbackManager: PlaybackManager = injectElement(),
    playerViewManager: PlayerViewManager = injectElement(),
    viewSettingsManager: ViewSettingsManager = injectElement(),
    imageSize: Dp,
    horizontalPadding: Dp,
    topPadding: Dp,
    onLongClick: () -> Unit,
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

            var aroundSongs by remember {
                mutableStateOf(listOf<Music?>())
            }
            aroundSongs = getAroundSongs(playbackManager = playbackManager)

            if (
                aroundSongs.filterNotNull().size > 1
                && playerViewManager.currentValue == BottomSheetStates.EXPANDED
                && viewSettingsManager.isPlayerSwipeEnabled
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

/**
 * Retrieve a list containing the current song and its around songs (previous and next).
 * If no songs are played, return a list containing null. If the played list contains only
 * the current song, it will return a list with only the current song.
 */
private fun getAroundSongs(
    playbackManager: PlaybackManager
): List<Music?> {
    val currentSongIndex = playbackManager.currentMusicIndex

    if (currentSongIndex == -1) return listOf(null)

    if (playbackManager.playedList.size == 1) return listOf(
        playbackManager.currentMusic
    )

    return listOf(
        playbackManager.getPreviousMusic(currentSongIndex),
        playbackManager.currentMusic,
        playbackManager.getNextMusic(currentSongIndex)
    )
}