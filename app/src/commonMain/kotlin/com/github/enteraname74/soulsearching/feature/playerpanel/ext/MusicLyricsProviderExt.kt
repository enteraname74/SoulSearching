package com.github.enteraname74.soulsearching.feature.playerpanel.ext

import com.github.enteraname74.soulsearching.domain.model.lyrics.MusicLyrics
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.local_lyrics_provider
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.remote_lyrics_provider
import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec

fun MusicLyrics.Provider.description(): SoulTextSpec =
    when(this) {
        MusicLyrics.Provider.Remote -> CoreRes.string.remote_lyrics_provider.toTextSpec()
        MusicLyrics.Provider.LocalFile -> CoreRes.string.local_lyrics_provider.toTextSpec()
    }
