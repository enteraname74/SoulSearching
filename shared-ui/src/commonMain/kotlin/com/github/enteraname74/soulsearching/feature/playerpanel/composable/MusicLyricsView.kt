package com.github.enteraname74.soulsearching.feature.playerpanel.composable

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SyncAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.enteraname74.domain.model.SyncedLyric
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonDefaults
import com.github.enteraname74.soulsearching.coreui.button.SoulIconButton
import com.github.enteraname74.soulsearching.coreui.ext.toDp
import com.github.enteraname74.soulsearching.coreui.list.LazyColumnCompat
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.utils.getNavigationBarPadding
import com.github.enteraname74.soulsearching.feature.player.domain.model.LyricsFetchState
import kotlinx.coroutines.launch
import kotlin.math.max

@Composable
fun MusicLyricsView(
    noLyricsColor: Color,
    contentColor: Color,
    containerColor: Color,
    lyricsState: LyricsFetchState,
    isExpanded: Boolean,
) {
    if (!isExpanded) {
        FetchingLyricsView(
            contentColor = contentColor
        )
    } else {
        when (lyricsState) {
            LyricsFetchState.FetchingLyrics -> FetchingLyricsView(
                contentColor = contentColor
            )

            is LyricsFetchState.FoundLyrics -> LyricsView(
                contentColor = contentColor,
                containerColor = containerColor,
                subTextColor = noLyricsColor,
                lyricsState = lyricsState,
            )

            LyricsFetchState.NoLyricsFound -> NoLyricsFoundView(
                contentColor = noLyricsColor
            )
        }
    }
}

@Composable
private fun FetchingLyricsView(
    contentColor: Color
) {
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

@Composable
private fun LyricsView(
    contentColor: Color,
    containerColor: Color,
    subTextColor: Color,
    lyricsState: LyricsFetchState.FoundLyrics,
) {
    val hasSyncedLyrics = lyricsState.lyrics.syncedLyrics?.takeIf { it.isNotEmpty() } != null


    var isSwitchLyricsVisible by rememberSaveable {
        mutableStateOf(hasSyncedLyrics)
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // Hide FAB
                if (available.y < -1) {
                    isSwitchLyricsVisible = false
                }

                // Show FAB
                if (available.y > 1) {
                    isSwitchLyricsVisible = true
                }

                return Offset.Zero
            }
        }
    }

    val pagerState = rememberPagerState(
        initialPage = if (hasSyncedLyrics) 1 else 0,
        pageCount = { if (hasSyncedLyrics) 2 else 1 }
    )

    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false
        ) { page ->
            when(page) {
                0 -> {
                    PlainLyricsView(
                        contentColor = contentColor,
                        subTextColor = subTextColor,
                        lyrics = lyricsState.lyrics.plainLyrics,
                        nestedScrollConnection = nestedScrollConnection,
                    )
                }
                else -> {
                    SyncedLyricsView(
                        contentColor = contentColor,
                        containerColor = containerColor,
                        subTextColor = subTextColor,
                        lyrics = lyricsState.lyrics.syncedLyrics!!,
                        currentHighlightedLine = lyricsState.highlightedLyricsLine,
                        nestedScrollConnection = nestedScrollConnection,
                    )
                }
            }
        }

        if (hasSyncedLyrics) {
            SwitchLyricsModeButton(
                modifier = Modifier.align(Alignment.BottomEnd),
                isButtonVisible = isSwitchLyricsVisible,
                onSwitch = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(
                            page = (pagerState.currentPage + 1).mod(2),
                            animationSpec = tween(SWITCH_LYRICS_MODE_DURATION)
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun SwitchLyricsModeButton(
    onSwitch: () -> Unit,
    isButtonVisible: Boolean,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        modifier = modifier
            .padding(bottom = getNavigationBarPadding().toDp())
            .padding(
                end = UiConstants.Spacing.medium,
                bottom = UiConstants.Spacing.medium,
            ),
        visible = isButtonVisible,
        enter = slideInVertically(initialOffsetY = { it * 2 }),
        exit = slideOutVertically(targetOffsetY = { it * 2 }),
    ) {
        SoulIconButton(
            modifier = Modifier.size(44.dp),
            icon = Icons.Rounded.SyncAlt,
            size = 28.dp,
            colors = SoulButtonDefaults.primaryColors(),
            onClick = onSwitch
        )
    }
}

private suspend fun LazyListState.animateToHighlightedLine(
    line: Int?
) {
    line?.let {
        this.animateScrollToItem(
            index = max(0, it - 2),
        )
    }
}

@Composable
private fun SyncedLyricsView(
    contentColor: Color,
    containerColor: Color,
    subTextColor: Color,
    lyrics: List<SyncedLyric>,
    currentHighlightedLine: Int?,
    nestedScrollConnection: NestedScrollConnection,
) {
    val lazyListState = rememberLazyListState()

    val shouldFocusOnLine: Boolean by remember(currentHighlightedLine) {
        derivedStateOf {
            if (currentHighlightedLine == null) return@derivedStateOf false

            // We want to focus the user on the current highlighted line if it is in the view of the user (top of the view).
            val upperBounds: Int = lazyListState.firstVisibleItemIndex
            val lowerBounds: Int = upperBounds + SYNCED_LYRICS_LOWER_BOUNDS_LIMIT

            currentHighlightedLine in upperBounds..lowerBounds
        }
    }

    LaunchedEffect(Unit) {
        // We want to focus the current highlighted lyrics when opening the view
        lazyListState.animateToHighlightedLine(currentHighlightedLine)
    }

    LaunchedEffect(currentHighlightedLine) {
        // If we were in the focus zone or if we were at the very limit of it

        val wasAtLimitOfZone = (lazyListState.firstVisibleItemIndex + SYNCED_LYRICS_LOWER_BOUNDS_LIMIT + 1) == currentHighlightedLine

        if (shouldFocusOnLine || wasAtLimitOfZone) {
            lazyListState.animateToHighlightedLine(currentHighlightedLine)
        }
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {

        var firstPreviousHeight: Int by rememberSaveable {
            mutableIntStateOf(0)
        }

        var secondPreviousHeight: Int by rememberSaveable {
            mutableIntStateOf(0)
        }

        LazyColumnCompat(
            modifier = Modifier
                .fillMaxWidth()
                .nestedScroll(nestedScrollConnection),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(
                horizontal = UiConstants.Spacing.medium,
            ),
            state = lazyListState,
        ) {
            items(
                count = lyrics.size,
            ) { pos ->
                val isHighlighted = pos == currentHighlightedLine

                val highlightedLineState = HighlightedLineState(
                    line = pos,
                    isHighlighted = isHighlighted,
                    isFocused = shouldFocusOnLine,
                )

                val weight: Int by animateIntAsState(
                    targetValue = (if (highlightedLineState.isHighlightedAndFocused) {
                        FontWeight.ExtraBold
                    } else if (isHighlighted) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Medium
                    }).weight,
                    animationSpec = tween(TEXT_SIZE_ANIMATION_DURATION)
                )

                val color: Color by animateColorAsState(
                    targetValue = if (isHighlighted) {
                        contentColor
                    } else {
                        subTextColor
                    },
                    animationSpec = tween(TEXT_SIZE_ANIMATION_DURATION),
                )

                val fontSize: Int by animateIntAsState(
                    targetValue = if (highlightedLineState.isHighlightedAndFocused) {
                        SYNCED_LYRIC_FOCUSED_SIZE
                    } else {
                        SYNCED_LYRIC_DEFAULT_SIZE
                    },
                    animationSpec = tween(TEXT_SIZE_ANIMATION_DURATION),
                )

                val sizeModifier = if (currentHighlightedLine != null && pos == currentHighlightedLine - 1) {
                    Modifier.onGloballyPositioned { layoutCoordinates ->
                        firstPreviousHeight = layoutCoordinates.size.height
                    }
                } else if (currentHighlightedLine != null && pos == currentHighlightedLine - 2) {
                    Modifier.onGloballyPositioned { layoutCoordinates ->
                        secondPreviousHeight = layoutCoordinates.size.height
                    }
                } else {
                    Modifier
                }

                Text(
                    text = lyrics[pos].line,
                    modifier = sizeModifier
                        .fillMaxWidth()
                        .animateItem(
                            fadeInSpec = tween(TEXT_SIZE_ANIMATION_DURATION),
                            placementSpec = tween(TEXT_SIZE_ANIMATION_DURATION),
                            fadeOutSpec = tween(TEXT_SIZE_ANIMATION_DURATION),
                        )
                        .animateContentSize(
                            animationSpec = tween(TEXT_SIZE_ANIMATION_DURATION),
                            alignment = Alignment.Center,
                        )
                        .padding(vertical = SYNCED_LYRIC_VERTICAL_PADDING),
                    fontWeight = FontWeight(weight),
                    color = color,
                    textAlign = TextAlign.Center,
                    fontSize = fontSize.sp
                )
            }

            item {
                LyricsProvider(color = subTextColor)
            }

            item {
                Spacer(
                    modifier = Modifier
                        .height(getNavigationBarPadding().toDp())
                )
            }
        }

        val previousItemsSize = (firstPreviousHeight + secondPreviousHeight).toDp()

        val alpha: Float by animateFloatAsState(
            targetValue = if (shouldFocusOnLine) {
                0.8f
            } else {
                0.0f
            },
            animationSpec = tween(TEXT_SIZE_ANIMATION_DURATION)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(previousItemsSize)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            containerColor.copy(alpha = alpha),
                            Color.Transparent,
                        ),
                    )
                ),
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .height(maxHeight * 0.4f)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            containerColor.copy(alpha = alpha),
                        ),
                    )
                ),
        )
    }
}

@Composable
private fun PlainLyricsView(
    contentColor: Color,
    subTextColor: Color,
    lyrics: List<String>,
    nestedScrollConnection: NestedScrollConnection,
) {
    LazyColumnCompat(
        modifier = Modifier
            .fillMaxWidth()
            .nestedScroll(nestedScrollConnection),
        contentPadding = PaddingValues(
            top = UiConstants.Spacing.medium,
            start = UiConstants.Spacing.medium,
            end = UiConstants.Spacing.medium,
        ),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        items(
            items = lyrics
        ) {
            Text(
                text = it,
                color = contentColor,
                fontSize = 14.sp
            )
        }

        item {
            LyricsProvider(color = subTextColor)
        }
        item {
            Spacer(
                modifier = Modifier
                    .height(getNavigationBarPadding().toDp())
            )
        }
    }
}

@Composable
private fun LyricsProvider(
    color: Color,
) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = UiConstants.Spacing.veryLarge),
        text = strings.lyricsProvider,
        fontSize = 12.sp,
        color = color,
        textAlign = TextAlign.End
    )
}

@Composable
private fun NoLyricsFoundView(
    contentColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(UiConstants.Spacing.small)
            .padding(bottom = getNavigationBarPadding().toDp())
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.Center),
            text = strings.noLyricsFound,
            color = contentColor,
            textAlign = TextAlign.Center,
            fontSize = 14.sp
        )
    }
}

private data class HighlightedLineState(
    val line: Int,
    val isHighlighted: Boolean,
    val isFocused: Boolean,
) {
    val isHighlightedAndFocused: Boolean
        get() = isHighlighted && isFocused
}

private const val SYNCED_LYRICS_LOWER_BOUNDS_LIMIT: Int = 5
private val SYNCED_LYRIC_VERTICAL_PADDING: Dp = 5.dp
private const val SYNCED_LYRIC_DEFAULT_SIZE: Int = 17
private const val SYNCED_LYRIC_FOCUSED_SIZE: Int = 23
private const val SWITCH_LYRICS_MODE_DURATION: Int = 375

private const val TEXT_SIZE_ANIMATION_DURATION: Int = 275
