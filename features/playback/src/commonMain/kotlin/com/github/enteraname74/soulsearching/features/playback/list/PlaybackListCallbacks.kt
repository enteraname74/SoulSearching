package com.github.enteraname74.soulsearching.features.playback.list

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.SoulResult

interface PlaybackListCallbacks {
    suspend fun onlyLoadMusic(
        seekTo: Int = 0,
        music: Music
    ): SoulResult<Unit>
    suspend fun getMusicPosition(): Int
    suspend fun stopPlayback()
    suspend fun next()
    suspend fun setAndPlayMusic(music: Music)
}