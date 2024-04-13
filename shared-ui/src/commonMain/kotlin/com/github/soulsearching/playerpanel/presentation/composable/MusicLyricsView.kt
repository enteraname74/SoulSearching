package com.github.soulsearching.playerpanel.presentation.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.sp
import com.github.enteraname74.domain.model.Music
import com.github.soulsearching.Constants
import com.github.soulsearching.strings.strings

@Composable
fun MusicLyricsView(
    noLyricsColor: Color,
    contentColor: Color,
    lyrics: String?,
    onRetrieveLyrics: () -> Unit,
    currentMusic: Music?
) {

    LaunchedEffect(currentMusic) {
        onRetrieveLyrics()
    }

    if (lyrics != null) {
        LyricsView(
            contentColor = contentColor,
            subTextColor = noLyricsColor,
            lyrics = lyrics
        )
    } else {
        NoLyricsFoundView(
            contentColor = noLyricsColor
        )
    }
}

@Composable
fun LyricsView(
    contentColor: Color,
    subTextColor: Color,
    lyrics: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                state = rememberScrollState()
            )
            .padding(Constants.Spacing.medium),
        verticalArrangement = Arrangement.spacedBy(Constants.Spacing.veryLarge)
    ) {
        Text(
            text = lyrics,
            color = contentColor,
            fontSize = 14.sp
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = strings.lyricsProvider,
            fontSize = 12.sp,
            color = subTextColor,
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun NoLyricsFoundView(
    contentColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(Constants.Spacing.small)
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