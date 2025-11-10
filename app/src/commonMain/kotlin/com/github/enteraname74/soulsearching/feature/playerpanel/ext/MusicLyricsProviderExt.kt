package com.github.enteraname74.soulsearching.feature.playerpanel.ext

import androidx.compose.runtime.Composable
import com.github.enteraname74.domain.model.lyrics.MusicLyrics
import com.github.enteraname74.soulsearching.coreui.strings.strings

@Composable
fun MusicLyrics.Provider.description(): String =
    when(this) {
        MusicLyrics.Provider.Remote -> strings.remoteLyricsProvider
        MusicLyrics.Provider.LocalFile -> strings.localLyricsProvider
    }