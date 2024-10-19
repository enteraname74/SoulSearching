package com.github.enteraname74.soulsearching.features.playback.progressjob

interface PlaybackProgressJobCallbacks {
    fun isPlaying(): Boolean
    fun getMusicPosition(): Int
}