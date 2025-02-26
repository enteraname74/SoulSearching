package com.github.enteraname74.soulsearching.feature.playerpanel.composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.github.enteraname74.domain.model.MusicLyrics
import com.github.enteraname74.domain.model.SyncedLyric
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.toDp
import com.github.enteraname74.soulsearching.coreui.list.LazyColumnCompat
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.utils.getStatusBarPadding
import com.github.enteraname74.soulsearching.feature.player.domain.model.LyricsFetchState

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
    LazyColumnCompat(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items(
            count = lyrics.size,
        ) { pos ->
            Text(
                text = lyrics[pos].line,
                fontWeight = if (pos == currentHighlightedLine) FontWeight.Bold else FontWeight.Normal,
                color = contentColor,
                fontSize = 14.sp
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