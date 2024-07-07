package com.github.soulsearching.model.playback

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlayerMusicRepository
import com.github.soulsearching.player.domain.model.PlaybackManager
import com.github.soulsearching.model.player.MediaSessionManager
import com.github.soulsearching.model.player.SoulSearchingAndroidPlayerImpl
import com.github.enteraname74.domain.settings.SoulSearchingSettings

/**
 * Implementation of a MusicPlayerManager for Android.
 * It manages the player, foreground service, media sessions and notification.
 */
class PlaybackManagerAndroidImpl(
    private val context: Context,
    settings: SoulSearchingSettings,
    playerMusicRepository: PlayerMusicRepository,
    musicRepository: MusicRepository
): PlaybackManager(
    settings = settings,
    playerMusicRepository = playerMusicRepository,
    musicRepository = musicRepository
) {
    private var shouldLaunchService: Boolean = true

    private val mediaSessionManager = MediaSessionManager(
        context = context,
        playbackManager = this
    )

    override val player: SoulSearchingAndroidPlayerImpl = SoulSearchingAndroidPlayerImpl(
        context = context,
        playbackManager =  this
    )

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.extras?.getBoolean(STOP_RECEIVE) == true) {
                context.unregisterReceiver(this)
            } else if (intent.extras?.getBoolean(NEXT) == true) {
                next()
            } else if (intent.extras?.getBoolean(PREVIOUS) == true) {
                previous()
            } else if(intent.extras?.getBoolean(TOGGLE_PLAY_PAUSE) == true) {
                togglePlayPause()
            }
        }
    }

    /**
     * Launch the foreground service used to handle the notification.
     * It gives the token of the media session manager to start the notification.
     */
    private fun launchService() {
        val serviceIntent = Intent(context, PlayerService::class.java)
        serviceIntent.putExtra(PlayerService.MEDIA_SESSION_TOKEN, mediaSessionManager.token)
        context.startForegroundService(serviceIntent)
        shouldLaunchService = false
    }

    /**
     * Stop the service used to emit the music notification.
     */
    private fun stopService() {
        val serviceIntent = Intent(context, PlayerService::class.java)
        context.stopService(serviceIntent)
    }

    /**
     * Initialize the playback manager.
     */
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun init() {
        super.init()
        if (Build.VERSION.SDK_INT >= 33) {
            context.registerReceiver(
                broadcastReceiver,
                IntentFilter(BROADCAST_NOTIFICATION),
                Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            context.registerReceiver(
                broadcastReceiver,
                IntentFilter(BROADCAST_NOTIFICATION)
            )
        }
        mediaSessionManager.init()
        shouldLaunchService = true
    }


    override suspend fun initializePlayerFromSavedList(savedList: List<Music>) {
        super.initializePlayerFromSavedList(savedList)
        defineCoverAndPaletteFromCoverId(coverId = currentMusic?.coverId)
        launchService()
    }

    override fun setAndPlayMusic(music: Music) {

        if (shouldLaunchService) launchService()

        super.setAndPlayMusic(music)
    }

    override fun stopPlayback(resetPlayedList: Boolean) {
        if (shouldInit) return

        try {
            context.unregisterReceiver(broadcastReceiver)
        } catch (e: Exception) {
            Log.e("PLAYBACK MANAGER", "EXCEPTION WHILE ON STOP PLAYBACK: $e")
        }
        player.dismiss()
        mediaSessionManager.release()

        if (!shouldLaunchService) stopService()

        shouldInit = true
        super.stopPlayback(resetPlayedList)
    }

    override fun updateNotification() {
        mediaSessionManager.updateMetadata()
        mediaSessionManager.updateState()

        val intentForUpdatingNotification = Intent(PlayerService.SERVICE_BROADCAST)
        intentForUpdatingNotification.putExtra(PlayerService.UPDATE_WITH_PLAYING_STATE, isPlaying)
        context.sendBroadcast(intentForUpdatingNotification)
    }

    companion object {
        const val BROADCAST_NOTIFICATION = "BROADCAST_NOTIFICATION"

        const val STOP_RECEIVE = "STOP RECEIVE"
        const val NEXT = "NEXT"
        const val PREVIOUS = "PREVIOUS"
        const val TOGGLE_PLAY_PAUSE = "TOGGLE PLAY PAUSE"
    }
}