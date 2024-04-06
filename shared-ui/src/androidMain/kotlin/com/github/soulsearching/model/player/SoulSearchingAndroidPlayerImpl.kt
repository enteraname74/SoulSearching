package com.github.soulsearching.model.player

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.util.Log
import com.github.enteraname74.domain.model.Music
import com.github.soulsearching.player.domain.model.PlaybackManager
import com.github.soulsearching.player.domain.model.SoulSearchingPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

/**
 * Implementation of the SoulSearchingAndroidPlayer
 */
class SoulSearchingAndroidPlayerImpl(
    context: Context,
    private val playbackManager: PlaybackManager
) :
    SoulSearchingPlayer,
    MediaPlayer.OnCompletionListener,
    MediaPlayer.OnPreparedListener,
    MediaPlayer.OnErrorListener {

    private var player: MediaPlayer = MediaPlayer()

//    private val notificationService: SoulSearchingNotification =
//        SoulSearchingNotificationBuilder.buildNotification(
//            context = context,
//            mediaSessionToken = mediaSession.sessionToken
//        )
//    private var currentDurationJob: Job? = null
    private var isOnlyLoadingMusic: Boolean = false

    private val audioManager: PlayerAudioManager = PlayerAudioManager(context, this)

//    /**
//     * Broadcast receiver used to control the player from the notification.
//     */
//    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//            if (intent.extras?.getBoolean("STOP") != null) {
//                when (intent.extras?.getBoolean("STOP") as Boolean) {
//                    true -> pause()
//                    false -> play()
//                }
//            }
//        }
//    }

    fun init() {
        audioManager.init()
        player = MediaPlayer()
        player.apply {
            setAudioAttributes(audioManager.audioAttributes)
            setOnPreparedListener(this@SoulSearchingAndroidPlayerImpl)
            setOnCompletionListener(this@SoulSearchingAndroidPlayerImpl)
            setOnErrorListener(this@SoulSearchingAndroidPlayerImpl)
        }
    }

//    init {
//        init()
//        initializeMediaSession()
//        initializeBroadcastReceiver()
//        notificationService.initializeNotification()
//        manageAudioBecomingNoisy()
//    }

    override fun setMusic(music: Music) {
        player.stop()
        player.reset()
        if (File(music.path).exists()) {
            player.setDataSource(music.path)
        }
    }

    override fun onlyLoadMusic() {
        isOnlyLoadingMusic = true
        launchMusic()
    }

    override fun isPlaying(): Boolean {
        return try {
            player.isPlaying
        } catch (e: IllegalStateException) {
            false
        }
    }

    override fun launchMusic() {
        try {
            player.prepareAsync()
        } catch (_: IllegalStateException) {

        }
    }

    /**
     * Tries to retrieve the audio focus and play the current music.
     */
    override fun play() {
        when (audioManager.requestAudioFocus()) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                try {
//                    PlayerUtils.playerViewModel.handler.isPlaying = true
//                    player.start()
//                    updateMediaSessionState()
//                    notificationService.updateNotification()
                    player.start()
                    playbackManager.update()
                } catch (_: IllegalStateException) {
                }
            }

            else -> {
                Log.d("PLAYER", "MISSING AUDIO MANAGER")
            }
        }
    }

    /**
     * Release the audio focus and pause the current music.
     */
    override fun pause() {
//        try {
//            PlayerUtils.playerViewModel.handler.isPlaying = false
//            audioManager.abandonAudioFocusRequest(audioFocusRequest)
//            player.pause()
//            updateMediaSessionState()
//            notificationService.updateNotification()
//        } catch (_: IllegalStateException) {
//        }
        try {
            audioManager.abandonAudioFocusRequest()
            player.pause()
            playbackManager.update()
        } catch (_: IllegalStateException) {
        }
    }

    override fun togglePlayPause() {
        if (player.isPlaying) pause()
        else play()
    }

    override fun seekToPosition(position: Int) {
//        player.seekTo(position)
//        PlayerUtils.playerViewModel.handler.currentMusicPosition = position
//        notificationService.updateNotification()
        try {
            player.seekTo(position)
            playbackManager.update()
        } catch (_: IllegalStateException) {

        }
    }

//    override fun next() {
//        pause()
//        PlayerUtils.playerViewModel.handler.setNextMusic()
//        PlayerUtils.playerViewModel.handler.currentMusic?.let {
//            setMusic(it)
//            launchMusic()
//        }
//    }
//
//    override fun previous() {
//        pause()
//        PlayerUtils.playerViewModel.handler.setPreviousMusic()
//        PlayerUtils.playerViewModel.handler.currentMusic?.let {
//            setMusic(it)
//            launchMusic()
//        }
//    }

    override fun dismiss() {
//        releaseDurationJob()
//
//        player.release()
//        mediaSession.release()
//        context.unregisterReceiver(broadcastReceiver)
//        notificationService.dismissNotification()
//        audioManager.abandonAudioFocusRequest(audioFocusRequest)
//        releaseAudioBecomingNoisyReceiver()
        player.release()
        audioManager.release()
    }

    override fun getMusicPosition(): Int {
        return try {
            player.currentPosition
        } catch (e: IllegalStateException) {
            0
        }
    }

    override fun getMusicDuration(): Int {
        return try {
            player.duration
        } catch (_: Exception) {
            0
        }
    }

//    override fun updateNotification() {
//        updateMediaSessionMetadata()
//        updateMediaSessionState()
//        notificationService.updateNotification()
//    }
//
//    override fun getNotification(): Notification {
//        return this.notificationService.getPlayerNotification()
//    }


//    /**
//     * Initialize the broadcast receiver used by the player.
//     */
//    @SuppressLint("UnspecifiedRegisterReceiverFlag")
//    private fun initializeBroadcastReceiver() {
//        if (Build.VERSION.SDK_INT >= 33) {
//            context.registerReceiver(
//                broadcastReceiver, IntentFilter(BROADCAST_NOTIFICATION),
//                Context.RECEIVER_NOT_EXPORTED
//            )
//        } else {
//            context.registerReceiver(broadcastReceiver, IntentFilter(BROADCAST_NOTIFICATION))
//        }
//    }

    override fun onCompletion(mp: MediaPlayer?) {
        CoroutineScope(Dispatchers.IO).launch {
//            next()
            playbackManager.next()
        }
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        Log.d("MEDIA PLAYER", "ERROR CODE : $what, $extra")
        when (what) {
            MediaPlayer.MEDIA_ERROR_UNKNOWN -> {
//                PlayerUtils.playerViewModel.handler.removeMusicFromCurrentPlaylist(
//                    musicId = PlayerUtils.playerViewModel.handler.currentMusic!!.musicId
//                )
                playbackManager.skipAndRemoveCurrentSong()
            }
        }
        return true
    }

    override fun onPrepared(mp: MediaPlayer?) {
        if (isOnlyLoadingMusic) {
//            if (PlayerUtils.playerViewModel.handler.currentMusicPosition <= player.duration) {
//                player.seekTo(PlayerUtils.playerViewModel.handler.currentMusicPosition)
//                releaseDurationJob()
//                launchDurationJob()
//            }
//            isOnlyLoadingMusic = false
        } else {
            when (audioManager.requestAudioFocus()) {
                AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> {
                    player.start()
                    playbackManager.update()
//                    PlayerUtils.playerViewModel.handler.currentMusic?.let {
//                        PlayerUtils.playerViewModel.handler.updateNbPlayed(it.musicId)
//                    }
//                    PlayerUtils.playerViewModel.handler.isPlaying = true
//
//                    releaseDurationJob()
//                    launchDurationJob()
//
//                    updateNotification()
//
//                    settings.saveCurrentMusicInformation(
//                        PlayerUtils.playerViewModel.handler.getIndexOfCurrentMusic(),
//                        PlayerUtils.playerViewModel.handler.currentMusicPosition
//                    )
                }

                else -> {}
            }
        }
    }

//    /**
//     * Launch a job that save the current music position to the shared preferences.
//     * It is used to save the current music position between app sessions.
//     */
//    private fun launchDurationJob() {
//        currentDurationJob = CoroutineScope(Dispatchers.IO).launch {
//            while (true) {
//                withContext(Dispatchers.IO) {
//                    Thread.sleep(1000)
//                }
//                PlayerUtils.playerViewModel.handler.currentMusicPosition =
//                    PlayerService.getCurrentMusicPosition()
//                if (player.isPlaying) {
//                    settings.setInt(
//                        SoulSearchingSettings.PLAYER_MUSIC_POSITION_KEY,
//                        PlayerUtils.playerViewModel.handler.currentMusicPosition
//                    )
//                }
//            }
//        }
//    }

//    /**
//     * Release the duration job.
//     */
//    private fun releaseDurationJob() {
//        if (currentDurationJob != null) {
//            currentDurationJob!!.cancel()
//            currentDurationJob = null
//        }
//    }
}