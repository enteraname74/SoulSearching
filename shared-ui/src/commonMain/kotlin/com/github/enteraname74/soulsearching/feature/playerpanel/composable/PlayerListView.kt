package com.github.enteraname74.soulsearching.feature.playerpanel.composable

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.composables.MusicItemComposable
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulButton
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonColors
import com.github.enteraname74.soulsearching.coreui.ext.blend
import com.github.enteraname74.soulsearching.coreui.ext.toDp
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.list.LazyColumnCompat
import com.github.enteraname74.soulsearching.coreui.multiselection.MultiSelectionState
import com.github.enteraname74.soulsearching.coreui.multiselection.composable.SoulSelectedIconColors
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.utils.getNavigationBarPadding
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun PlayerListView(
    playbackManager: PlaybackManager = injectElement(),
    currentMusicIndex: Int,
    isExpanded: Boolean,
    playedList: List<Music>,
    onLongSelectOnMusic: (Music) -> Unit,
    onMoreClickedOnMusic: (Music) -> Unit,
    containerColor: Color,
    contentColor: Color,
    buttonColors: SoulButtonColors,
    multiSelectionState: MultiSelectionState,
    selectedIconColors: SoulSelectedIconColors,
) {

    val coroutineScope = rememberCoroutineScope()
    val playerListState = rememberLazyListState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        if (isExpanded) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(UiConstants.Spacing.small),
                contentAlignment = Alignment.CenterEnd,
            ) {
                SoulButton(
                    colors = buttonColors,
                    onClick = {
                        coroutineScope.launch {
                            if (currentMusicIndex != -1) {
                                playerListState.animateScrollToItem(
                                    currentMusicIndex
                                )
                            }
                        }
                    }
                ) {
                    Text(
                        text = strings.currentSong,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        color = buttonColors.contentColor,
                        fontSize = 12.sp
                    )
                }
            }

            val currentPlayedSong: Music? by playbackManager.currentSong.collectAsState()

            var uiList by remember {
                mutableStateOf(playedList)
            }

            val reorderableLazyListState = rememberReorderableLazyListState(playerListState) { from, to ->
                uiList = uiList.toMutableList().apply {
                    add(to.index, removeAt(from.index))
                }
            }

            LaunchedEffect(playedList) {
                // If the played list was updated, but we were in a dragged state, we don't update the ui list.
                if (!reorderableLazyListState.isAnyItemDragging) {
                    uiList = playedList
                }
            }



            LazyColumnCompat(
                state = playerListState,
                contentPadding = PaddingValues(
                    bottom = getNavigationBarPadding().toDp()
                )
            ) {
                items(
                    items = uiList,
                    key = { it.musicId },
                    contentType = { PLAYER_LIST_CONTENT_TYPE }
                ) { elt ->

                    ReorderableItem(
                        state = reorderableLazyListState,
                        key = elt.musicId
                    ) { isDragging ->
                        val elevation by animateDpAsState(if (isDragging) 4.dp else 0.dp)

                        Surface(shadowElevation = elevation) {
                            Swipeable(
                                modifier = Modifier
                                    .animateItem(),
                                music = elt,
                                contentColor = contentColor,
                                containerColor = containerColor,
                            ) {
                                MusicItemComposable(
                                    modifier = Modifier
                                        .background(containerColor),
                                    music = elt,
                                    reorderableModifier = Modifier
                                        .draggableHandle(
                                            onDragStopped = {
                                                CoroutineScope(Dispatchers.IO).launch {
                                                    playbackManager.updatePlayedListAfterReorder(
                                                        newList = uiList
                                                    )
                                                }
                                            }
                                        ),
                                    onClick = { music ->
                                        CoroutineScope(Dispatchers.IO).launch {
                                            playbackManager.setAndPlayMusic(music)
                                        }
                                    },
                                    onMoreClicked = {
                                        coroutineScope.launch {
                                            onMoreClickedOnMusic(elt)
                                        }
                                    },
                                    onLongClick = { onLongSelectOnMusic(elt) },
                                    textColor = contentColor,
                                    isPlayedMusic = currentPlayedSong?.musicId == elt.musicId,
                                    isSelected = multiSelectionState.selectedIds.contains(elt.musicId),
                                    isSelectionModeOn = multiSelectionState.selectedIds.isNotEmpty(),
                                    selectedIconColors = selectedIconColors,
                                )
                            }
                        }

                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = getNavigationBarPadding().toDp())
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = contentColor
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
@Suppress("Deprecation")
private fun Swipeable(
    music: Music,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    playbackManager: PlaybackManager = injectElement(),
    content: @Composable () -> Unit,
) {
    BoxWithConstraints(
        modifier = modifier,
    ) {
        val swipeableState: SwipeableState<MusicItemSwipeableState> = rememberSwipeableState(
            initialValue = MusicItemSwipeableState.NORMAL
        )

        LaunchedEffect(swipeableState.currentValue) {
            if (swipeableState.currentValue == MusicItemSwipeableState.SWIPED) {
                CoroutineScope(Dispatchers.IO).launch {
                    playbackManager.removeSongsFromPlayedPlaylist(
                        musicIds = listOf(music.musicId)
                    )
                }
            }
        }

        val width = this.constraints.maxWidth.toFloat()
        val floatThreshold = (width * DELETION_OFFSET_RATIO)
        val threshold = floatThreshold.toDp()

        var globalHeight: Int by rememberSaveable {
            mutableStateOf(0)
        }

        val adaptedHeight = globalHeight.toDp()
        val deletionWidth = swipeableState.offset.value.roundToInt().toDp()
        val isInDeletionZone = swipeableState.offset.value >= floatThreshold

        val alpha: Float = min(swipeableState.offset.value, floatThreshold) / floatThreshold

        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = max(swipeableState.offset.value.roundToInt(), 0),
                        y = 0,
                    )
                }.swipeable(
                    state = swipeableState,
                    orientation = Orientation.Horizontal,
                    anchors = mapOf(
                        0f to MusicItemSwipeableState.NORMAL,
                        width to MusicItemSwipeableState.SWIPED
                    ),
                    thresholds = { _, _ ->
                        FixedThreshold(threshold)
                    }
                ).onGloballyPositioned { layoutCoordinates ->
                    globalHeight = layoutCoordinates.size.height
                }
        ) {
            content()
        }


        DeletionIndicator(
            modifier = Modifier
                .height(adaptedHeight)
                .width(deletionWidth)
                .alpha(alpha),
            contentColor = contentColor,
            containerColor = containerColor,
            isInDeletionZone = isInDeletionZone,
        )
    }
}

@Composable
private fun DeletionIndicator(
    isInDeletionZone: Boolean,
    contentColor: Color,
    containerColor: Color,
    modifier: Modifier = Modifier,
) {
    val actualContainerColor: Color by animateColorAsState(
        targetValue = if (isInDeletionZone) {
            containerColor.blend(
                other = Color.Red,
                ratio = DELETION_ZONE_CONTENT_COLOR_RATIO,
            )
        } else {
            containerColor
        },
        animationSpec = tween(UiConstants.AnimationDuration.normal)
    )

    Box(
        modifier = Modifier
            .background(actualContainerColor)
            .then(modifier),
        contentAlignment = Alignment.Center,
    ) {
        SoulIcon(
            icon = Icons.Rounded.Delete,
            tint = contentColor,
        )
    }
}

private enum class MusicItemSwipeableState {
    NORMAL,
    SWIPED
}

private const val PLAYER_LIST_CONTENT_TYPE: String = "PLAYER_LIST_CONTENT_TYPE"
private const val DELETION_OFFSET_RATIO: Float = 0.3f
private const val DELETION_ZONE_CONTENT_COLOR_RATIO: Float = 0.6f