package com.github.enteraname74.soulsearching.repository.datasource.lyrics

import com.github.enteraname74.soulsearching.domain.model.lyrics.MusicLyrics
import com.github.enteraname74.soulsearching.domain.model.lyrics.MusicLyricsRequest
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File

actual class LyricsLocalDataSource actual constructor() {

    actual suspend fun getLyricsOfSong(request: MusicLyricsRequest): MusicLyrics? =
        runCatching {
            val audioFile = AudioFileIO.read(File(request.localPath.orEmpty()))
            val tag = audioFile.tag

            val lyrics: String = tag.getFirst(FieldKey.LYRICS).takeIf { it.isNotBlank() } ?: return@runCatching null

            MusicLyrics(
                plainLyrics = MusicLyrics.buildPlainLyrics(lyrics),
                syncedLyrics = MusicLyrics.buildSyncedLyrics(lyrics),
                provider = MusicLyrics.Provider.LocalFile,
            )
        }.getOrNull()
}
