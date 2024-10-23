package com.github.enteraname74.soulsearching.features.playback.list

import com.github.enteraname74.domain.model.Music

interface PlaybackListCallbacks {
    suspend fun onlyLoadMusic(
        seekTo: Int = 0,
        music: Music
    )
    suspend fun getMusicPosition(): Int
    suspend fun stopPlayback()
    suspend fun next()

}