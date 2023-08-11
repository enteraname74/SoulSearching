package com.github.soulsearching.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.github.soulsearching.classes.PlayerUtils
import com.github.soulsearching.classes.notification.SoulSearchingNotification
import com.github.soulsearching.classes.player.SoulSearchingMediaPlayerImpl
import com.github.soulsearching.classes.player.SoulSearchingPlayer


class PlayerService : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (player == null) {
            player = SoulSearchingMediaPlayerImpl(applicationContext)

            val extras = intent?.extras
            if (extras != null) {
                if (extras.getBoolean(IS_FROM_SAVED_LIST)) {
                    onlyLoadMusic()
                } else {
                    setAndPlayCurrentMusic()
                }
            } else {
                setAndPlayCurrentMusic()
            }

            startForeground(SoulSearchingNotification.CHANNEL_ID, player!!.getNotification())
        }

        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopMusic(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    companion object {
        const val IS_FROM_SAVED_LIST = "isFromSavedList"
        private var player: SoulSearchingPlayer? = null

        fun setAndPlayCurrentMusic() {
            player?.let {
                it.setMusic(PlayerUtils.playerViewModel.currentMusic!!)
                it.launchMusic()
            }
        }

        fun onlyLoadMusic() {
            player?.let {
                it.setMusic(PlayerUtils.playerViewModel.currentMusic!!)
                it.onlyLoadMusic()
            }
        }

        fun isPlayerPlaying(): Boolean {
            return if (player != null) {
                player!!.isPlaying()
            } else {
                false
            }
        }

        fun togglePlayPause() {
            Log.d("PLAYER SERVICE", "TOGGLE PLAY PAUSE")
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
            Log.d("PLAYER SERVICE", "STOP MUSIC CALL")
            if (player != null) {
                player!!.dismiss()
                player = null
            }
            PlayerUtils.playerViewModel.resetPlayerData()
            val serviceIntent = Intent(context, PlayerService::class.java)
            context.stopService(serviceIntent)
        }

        fun updateNotification() {
            player?.updateNotification()
        }

        const val RESTART_SERVICE = "RESTART_SERVICE"
    }
}