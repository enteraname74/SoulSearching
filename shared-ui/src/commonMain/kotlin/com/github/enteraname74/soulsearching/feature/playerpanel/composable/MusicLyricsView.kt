package com.github.enteraname74.soulsearching.feature.playerpanel.composable

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.github.enteraname74.domain.model.SyncedLyric
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.toDp
import com.github.enteraname74.soulsearching.coreui.list.LazyColumnCompat
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.utils.getStatusBarPadding
import com.github.enteraname74.soulsearching.feature.player.domain.model.LyricsFetchState
import kotlin.math.min

@Composable
fun MusicLyricsView(
    noLyricsColor: Color,
    contentColor: Color,
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
                subTextColor = noLyricsColor,
                lyricsState = lyricsState
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
            .padding(bottom = getStatusBarPadding().toDp())
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
    subTextColor: Color,
    lyricsState: LyricsFetchState.FoundLyrics,
) {
    when {
        lyricsState.lyrics.syncedLyrics?.takeIf { it.isNotEmpty() } != null -> {
            SyncedLyricsView(
                contentColor = contentColor,
                subTextColor = subTextColor,
                lyrics = lyricsState.lyrics.syncedLyrics!!,
                currentHighlightedLine = lyricsState.highlightedLyricsLine,
            )
        }
        else -> {
            PlainLyricsView(
                contentColor = contentColor,
                subTextColor = subTextColor,
                lyrics = lyricsState.lyrics.plainLyrics,
            )
        }
    }
}

@Composable
private fun SyncedLyricsView(
    contentColor: Color,
    subTextColor: Color,
    lyrics: List<SyncedLyric>,
    currentHighlightedLine: Int?,
) {
    val lazyListState = rememberLazyListState()

    println("CURRENT? $currentHighlightedLine")

    val shouldFocusOnLine: Boolean by remember(currentHighlightedLine) {
        derivedStateOf {
            if (currentHighlightedLine == null) return@derivedStateOf false

            // We want to focus the user on the current highlighted line if it is in the view of the user.
            val firstItemIndex: Int = lazyListState.firstVisibleItemIndex
            val lastItemIndex: Int = lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: firstItemIndex

            // We want to reduce the bounds a little to let the user have more freedom when viewing the lyrics.
            val upperBound = min(firstItemIndex+1, lastItemIndex)
            val lowerBounds = maxOf(lastItemIndex-1, firstItemIndex)

            println("CURRENT: $currentHighlightedLine, upper: $upperBound, lower: $lowerBounds")

            currentHighlightedLine in upperBound..lowerBounds
        }
    }

    println("SHOULD FOCUS: $shouldFocusOnLine")

    var heightOfPreviousItem by remember { mutableStateOf(0) }

    LaunchedEffect(currentHighlightedLine) {
        if (shouldFocusOnLine) {
            currentHighlightedLine?.let {
                lazyListState.animateScrollToItem(
                    index = it,
                    scrollOffset = -heightOfPreviousItem,
                )
            }
        }
    }

    LazyColumnCompat(
        horizontalAlignment = Alignment.CenterHorizontally,
        state = lazyListState,
    ) {
        items(
            count = lyrics.size,
        ) { pos ->
            val sizeModifier = if (currentHighlightedLine != null && pos == currentHighlightedLine - 1) {
                Modifier.onGloballyPositioned { layoutCoordinates ->
                    heightOfPreviousItem = layoutCoordinates.size.height
                }
            } else {
                Modifier
            }

            Text(
                text = lyrics[pos].line,
                modifier = Modifier
                    .padding(vertical = UiConstants.Spacing.mediumPlus)
                    .then(sizeModifier),
                fontWeight = if (pos == currentHighlightedLine) FontWeight.Bold else FontWeight.Normal,
                color = contentColor,
                fontSize = 18.sp
            )
        }

        item {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = UiConstants.Spacing.veryLarge),
                text = strings.lyricsProvider,
                fontSize = 12.sp,
                color = subTextColor,
                textAlign = TextAlign.End
            )
        }

        item {
            Spacer(
                modifier = Modifier
                    .height(getStatusBarPadding().toDp())
            )
        }
    }
}

@Composable
private fun PlainLyricsView(
    contentColor: Color,
    subTextColor: Color,
    lyrics: String,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                state = rememberScrollState()
            )
            .padding(
                top = UiConstants.Spacing.medium,
                start = UiConstants.Spacing.medium,
                end = UiConstants.Spacing.medium,
            ),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = lyrics,
            color = contentColor,
            fontSize = 14.sp
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = UiConstants.Spacing.veryLarge),
            text = strings.lyricsProvider,
            fontSize = 12.sp,
            color = subTextColor,
            textAlign = TextAlign.End
        )
        Spacer(
            modifier = Modifier
                .height(getStatusBarPadding().toDp())
        )
    }
}

@Composable
private fun NoLyricsFoundView(
    contentColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(UiConstants.Spacing.small)
            .padding(bottom = getStatusBarPadding().toDp())
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