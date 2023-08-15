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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


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
        private var isDoingOperations: Boolean = false


        fun setAndPlayCurrentMusic() {
            if (!isDoingOperations) {
                isDoingOperations = true
                CoroutineScope(Dispatchers.IO).launch {
                    player?.let {
                        it.setMusic(PlayerUtils.playerViewModel.currentMusic!!)
                        it.launchMusic()
                    }
                    isDoingOperations = false
                }
            }
        }

        fun onlyLoadMusic() {
            if (!isDoingOperations) {
                isDoingOperations = true
                CoroutineScope(Dispatchers.IO).launch {
                    PlayerUtils.playerViewModel.currentMusic?.let {music ->
                        player?.let {
                            it.setMusic(music)
                            it.onlyLoadMusic()
                        }
                    }

                    isDoingOperations = false
                }
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
            player?.togglePlayPause()
        }

        fun playNext() {
            if (!isDoingOperations) {
                isDoingOperations = true
                CoroutineScope(Dispatchers.IO).launch {
                    player?.next()
                    isDoingOperations = false
                }
            }
        }

        fun playPrevious() {
            if (!isDoingOperations) {
                isDoingOperations = true
                CoroutineScope(Dispatchers.IO).launch {
                    player?.previous()
                    isDoingOperations = false
                }
            }
        }

        fun seekToPosition(position: Int) {
            player?.seekToPosition(position)
        }

        fun getMusicDuration(): Int {
            return if (PlayerUtils.playerViewModel.currentMusic == null) {
                0
            } else {
                PlayerUtils.playerViewModel.currentMusic!!.duration.toInt()
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
            CoroutineScope(Dispatchers.IO).launch {
                player?.updateNotification()
            }
        }

        const val RESTART_SERVICE = "RESTART_SERVICE"
    }
}