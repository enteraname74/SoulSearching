package com.github.enteraname74.soulsearching.feature.player.presentation.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.composables.SoulImage
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.combinedClickableWithRightClick
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import com.github.enteraname74.soulsearching.util.CoverUtils
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
    currentMusic: Music,
    onCoverLoaded: (ImageBitmap?) -> Unit,
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
                    pageSpacing = 120.dp,
                    userScrollEnabled = playerViewManager.currentValue == BottomSheetStates.EXPANDED,
                ) { currentSongPos ->

                    val song: Music? = aroundSongs.getOrNull(currentSongPos)

                    MusicCover(
                        modifier = imageModifier,
                        cover = song?.cover,
                        size = imageSize,
                        offset = playerViewManager.offset,
                        onCoverLoaded = if (song?.musicId == currentMusic.musicId) {
                            onCoverLoaded
                        } else {
                            null
                        },
                    )
                }
            } else {
                MusicCover(
                    cover = currentMusic.cover,
                    size = imageSize,
                    offset = playerViewManager.offset,
                    onCoverLoaded = onCoverLoaded,
                    modifier = imageModifier,
                )
            }
        }
    }
}

@Composable
private fun MusicCover(
    cover: Cover?,
    size: Dp,
    modifier: Modifier = Modifier,
    offset: Float,
    onCoverLoaded: ((bitmap: ImageBitmap?) -> Unit)?,
) {
    SoulImage(
        modifier = modifier,
        cover = cover,
        size = size,
        roundedPercent = (offset / 100).roundToInt()
            .coerceIn(3, 10),
        onSuccess = onCoverLoaded,
        builderOptions = {
            this.size(CoverUtils.IMAGE_SIZE)
        }
    )
}