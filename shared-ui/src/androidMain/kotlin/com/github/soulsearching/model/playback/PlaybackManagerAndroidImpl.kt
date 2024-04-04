package com.github.soulsearching.model.playback

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import com.github.enteraname74.domain.model.Music
import com.github.soulsearching.model.utils.AndroidUtils
import com.github.soulsearching.model.PlaybackManager
import com.github.soulsearching.model.SoulSearchingPlayer
import com.github.soulsearching.model.player.MediaSessionManager
import com.github.soulsearching.model.player.SoulSearchingAndroidPlayerImpl
import com.github.soulsearching.utils.PlayerUtils
import com.github.soulsearching.viewmodel.PlayerViewModel

/**
 * Implementation of a MusicPlayerManager for Android.
 * It manages the player, foreground service, media sessions and notification.
 */
class PlaybackManagerAndroidImpl(
    private val context: Context
): PlaybackManager() {
    private var shouldLaunchService: Boolean = true
    private var shouldInit: Boolean = true

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
            if (intent.extras?.getBoolean(STOP_RECEIVE) != null) {
                context.unregisterReceiver(this)
            } else if (intent.extras?.getBoolean(NEXT) != null) {
                next()
            } else if (intent.extras?.getBoolean(PREVIOUS) != null) {
                previous()
            } else if(intent.extras?.getBoolean(TOGGLE_PLAY_PAUSE) != null) {
                togglePlayPause()
            }
        }
    }

    init {
        init()
    }

    /**
     * Initialize the playback manager.
     */
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun init() {
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

        player.init()
        mediaSessionManager.init()
        shouldLaunchService = true
        shouldInit = false
    }


    override fun initializePlayerFromSavedList(savedMusicList: ArrayList<Music>) {
        PlayerUtils.playerViewModel.handler.setPlayerInformationFromSavedList(
            savedMusicList
        )
        AndroidUtils.launchService(
            context = context,
            isFromSavedList = true
        )
        PlayerUtils.playerViewModel.handler.shouldServiceBeLaunched = true
    }

    override fun initializeMusicPlayerManager(isFromSavedList: Boolean) = AndroidUtils.launchService(
        context = context,
        isFromSavedList = isFromSavedList
    )

    override fun setAndPlayMusic(music: Music) {
        if (shouldInit) init()

        if (shouldLaunchService) {
            val serviceIntent = Intent(context, PlayerService::class.java)
            serviceIntent.putExtra(PlayerService.MEDIA_SESSION_TOKEN, mediaSessionManager.getToken())
            context.startForegroundService(serviceIntent)
            shouldLaunchService = false
        }

        _currentMusic = music
        player.setMusic(music)
        player.launchMusic()
        update()
    }

    override fun stopPlayback() {
        if (shouldInit) return

        releaseDurationJob()

        context.unregisterReceiver(broadcastReceiver)
        player.dismiss()
        mediaSessionManager.release()

        val serviceIntent = Intent(context, PlayerService::class.java)
        context.stopService(serviceIntent)

        shouldInit = true
        _playedList = ArrayList()
        _initialList = ArrayList()
    }

    override fun update() {
        mediaSessionManager.updateMetadata()
        mediaSessionManager.updateState()

        if (durationJob == null) {
            launchDurationJob()
        }

        val intentForUpdatingNotification = Intent(PlayerService.SERVICE_BROADCAST)
        intentForUpdatingNotification.putExtra(PlayerService.UPDATE_WITH_PLAYING_STATE, isPlaying)
        context.sendBroadcast(intentForUpdatingNotification)

        playerViewModel?.handler?.let {
            it.currentMusic = _currentMusic
            it.isPlaying = isPlaying
        }
    }

    companion object {
        const val BROADCAST_NOTIFICATION = "BROADCAST_NOTIFICATION"

        const val STOP_RECEIVE = "STOP RECEIVE"
        const val NEXT = "NEXT"
        const val PREVIOUS = "NEXT"
        const val TOGGLE_PLAY_PAUSE = "TOGGLE PLAY PAUSE"
    }
}