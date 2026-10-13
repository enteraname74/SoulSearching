package com.github.enteraname74.soulsearching.repository.datasource.lyrics

import com.github.enteraname74.soulsearching.domain.model.Music
import com.github.enteraname74.soulsearching.domain.model.lyrics.MusicLyrics
import com.github.enteraname74.soulsearching.domain.model.lyrics.MusicLyricsRequest

expect class LyricsLocalDataSource() {
    suspend fun getLyricsOfSong(request: MusicLyricsRequest): MusicLyrics?
}
