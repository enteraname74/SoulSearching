package com.github.enteraname74.soulsearching.remote.model

import com.github.enteraname74.domain.model.lyrics.MusicLyrics
import kotlinx.serialization.Serializable

@Serializable
data class RemoteLyrics(
    val plainLyrics: String?,
    val syncedLyrics: String?,
) {
    fun toMusicLyrics(): MusicLyrics? =
        plainLyrics?.let {
            MusicLyrics(
                plainLyrics = it.split("\n"),
                syncedLyrics = syncedLyrics?.let { stringLyrics ->
                    MusicLyrics.buildSyncedLyrics(stringLyrics = stringLyrics)
                },
                provider = MusicLyrics.Provider.Remote,
            )
        }
}