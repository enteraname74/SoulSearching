package com.github.enteraname74.soulsearching.repository.datasource.lyrics

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.lyrics.MusicLyrics

actual class LyricsLocalDataSource actual constructor() {
    actual fun getLyricsOfSong(music: Music): MusicLyrics? = null
}
