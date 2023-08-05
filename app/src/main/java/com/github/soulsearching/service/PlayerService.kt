package com.github.soulsearching.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.github.soulsearching.classes.PlayerUtils
import com.github.soulsearching.classes.player.SoulSearchingMediaPlayerImpl
import com.github.soulsearching.classes.player.SoulSearchingPlayer


class PlayerService : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("START COMMAND", "")

        if (player == null) {
            player = SoulSearchingMediaPlayerImpl(applicationContext)
            setAndPlayCurrentMusic()
        }

        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Log.d("PLAYBACK SERVICE", "REMOVED")
        stopMusic(this)
    }

    override fun onDestroy() {
        Log.d("DESTROY SERVICE", "DESTROY SERVICE")
        super.onDestroy()
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    companion object {
        private var player : SoulSearchingPlayer? = null

        fun setAndPlayCurrentMusic() {
            player?.let {
                it.setMusic(PlayerUtils.playerViewModel.currentMusic!!)
                it.launchMusic()
            }
        }

        fun pauseMusic() {
            player?.togglePlayPause()
        }

        fun playMusic() {
            player?.togglePlayPause()
        }

        fun playNext() {
            player?.next()
        }

        fun playPrevious() {
            player?.previous()
        }

        fun seekToPosition(position: Int) {
            player?.seekToPosition(position)
        }

        fun getMusicDuration(): Int {
            return if (player == null) {
                0
            } else {
                player!!.getMusicDuration()
            }
        }

        fun getCurrentMusicPosition(): Int {
            return if (player == null) {
                0
            } else {
                player!!.getMusicPosition()
            }
        }

        fun stopMusic(context: Context) {
            if (player != null) {
                player!!.dismiss()
                player = null
            }
            PlayerUtils.playerViewModel.resetPlayerData()
            val serviceIntent = Intent(context, PlayerService::class.java)
            context.stopService(serviceIntent)
        }
    }
}