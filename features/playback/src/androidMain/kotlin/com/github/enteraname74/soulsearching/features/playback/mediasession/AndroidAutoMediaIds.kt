package com.github.enteraname74.soulsearching.features.playback.mediasession

import kotlin.uuid.Uuid

internal object AndroidAutoMediaIds {
    const val ROOT: String = "soul_searching_root"
    const val ALL_SONGS: String = "soul_searching_all_songs"
    const val MUSIC_PREFIX: String = "soul_searching_music:"

    fun forMusic(musicId: Uuid): String =
        "$MUSIC_PREFIX$musicId"

    fun musicIdFrom(mediaId: String): Uuid? =
        mediaId
            .takeIf { it.startsWith(MUSIC_PREFIX) }
            ?.removePrefix(MUSIC_PREFIX)
            ?.let { runCatching { Uuid.parse(it) }.getOrNull() }
}
