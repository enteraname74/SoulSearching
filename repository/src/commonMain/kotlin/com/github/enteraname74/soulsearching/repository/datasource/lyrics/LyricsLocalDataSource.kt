package com.github.enteraname74.soulsearching.repository.datasource.lyrics

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.lyrics.MusicLyrics
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File

class LyricsLocalDataSource {
    fun getLyricsOfSong(music: Music): MusicLyrics? =
        runCatching {
            val audioFile = AudioFileIO.read(File(music.path))
            val tag = audioFile.tag

            val lyrics: String = tag.getFirst(FieldKey.LYRICS).takeIf { it.isNotBlank() } ?: return@runCatching null

            MusicLyrics(
                plainLyrics = MusicLyrics.cleanPlainLyrics(lyrics),
                syncedLyrics = MusicLyrics.buildSyncedLyrics(lyrics),
                provider = MusicLyrics.Provider.LocalFile,
            )
        }.getOrNull()
}