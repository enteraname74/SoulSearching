package com.github.soulsearching.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.github.soulsearching.classes.utils.PlayerUtils
import com.github.soulsearching.classes.notification.SoulSearchingNotification
import com.github.soulsearching.classes.player.SoulSearchingMediaPlayerImpl
import com.github.soulsearching.classes.player.SoulSearchingPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Service used for the playback.
 */
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


        /**
         * Set and play the current music from the player view model.
         */
        fun setAndPlayCurrentMusic() {
            if (isDoingOperations) {
                return
            }
            isDoingOperations = true
            CoroutineScope(Dispatchers.IO).launch {
                player?.let {
                    it.setMusic(PlayerUtils.playerViewModel.currentMusic!!)
                    it.launchMusic()
                }
                isDoingOperations = false
            }
        }

        /**
         * Load the current music of the player view model.
         * The music will not be played.
         */
        fun onlyLoadMusic() {
            if (isDoingOperations) {
                return
            }
            isDoingOperations = true
            CoroutineScope(Dispatchers.IO).launch {
                PlayerUtils.playerViewModel.currentMusic?.let { music ->
                    player?.let {
                        it.setMusic(music)
                        it.onlyLoadMusic()
                    }
                }

                isDoingOperations = false
            }
        }

        /**
         * Check if the player is currently playing a song.
         * If the player is not defined yet, it will return false.
         */
        fun isPlayerPlaying(): Boolean {
            return if (player != null) {
                player!!.isPlaying()
            } else {
                false
            }
        }

        /**
         * Toggle the play pause action of the player if it's defined.
         */
        fun togglePlayPause() {
            player?.togglePlayPause()
        }

        /**
         * Play the next song in queue.
         */
        fun playNext() {
            if (isDoingOperations) {
                return
            }
            isDoingOperations = true
            CoroutineScope(Dispatchers.IO).launch {
                player?.next()
                isDoingOperations = false
            }
        }

        /**
         * Play the previous song in queue.
         */
        fun playPrevious() {
            if (isDoingOperations) {
                return
            }
            isDoingOperations = true
            CoroutineScope(Dispatchers.IO).launch {
                player?.previous()
                isDoingOperations = false
            }
        }

        /**
         * Seek to a given position in the current played music.
         */
        fun seekToPosition(position: Int) {
            player?.seekToPosition(position)
        }

        /**
         * Retrieve the current played music duration.
         * Return 0 if no music is playing.
         */
        fun getMusicDuration(): Int {
            return if (PlayerUtils.playerViewModel.currentMusic == null) {
                0
            } else {
                PlayerUtils.playerViewModel.currentMusic!!.duration.toInt()
            }
        }

        /**
         * Retrieve the current position in the current played music.
         * Return 0 if the player if not defined.
         */
        fun getCurrentMusicPosition(): Int {
            return if (player == null) {
                0
            } else {
                player!!.getMusicPosition()
            }
        }

        /**
         * Stop the playback.
         * If will stop the service, dismiss the player and reset the player view model data.
         */
        fun stopMusic(context: Context) {
            if (player != null) {
                player!!.dismiss()
                player = null
            }
            PlayerUtils.playerViewModel.resetPlayerData()
            val serviceIntent = Intent(context, PlayerService::class.java)
            context.stopService(serviceIntent)
        }

        /**
         * Force the update of the notification.
         */
        fun updateNotification() {
            CoroutineScope(Dispatchers.IO).launch {
                player?.updateNotification()
            }
        }

        const val RESTART_SERVICE = "RESTART_SERVICE"
    }
}