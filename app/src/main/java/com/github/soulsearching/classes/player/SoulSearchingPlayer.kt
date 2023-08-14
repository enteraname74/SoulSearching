package com.github.soulsearching.classes.player

import android.app.Notification
import com.github.soulsearching.database.model.Music

interface SoulSearchingPlayer {
    fun setMusic(music: Music)
    fun onlyLoadMusic()
    fun launchMusic()
    fun togglePlayPause()
    fun seekToPosition(position: Int)
    fun next()
    fun isPlaying(): Boolean
    fun previous()
    fun dismiss()
    fun getMusicPosition(): Int
    fun updateNotification()
    fun getNotification(): Notification
}