package com.github.soulsearching.classes.player

import android.annotation.SuppressLint
import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.media.*
import android.media.session.PlaybackState
import android.os.Build
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.KeyEvent
import androidx.compose.ui.graphics.asAndroidBitmap
import com.github.enteraname74.domain.model.Music
import com.github.soulsearching.R
import com.github.soulsearching.classes.notification.SoulSearchingNotification
import com.github.soulsearching.classes.notification.SoulSearchingNotificationBuilder
import com.github.soulsearching.model.settings.SoulSearchingSettings
import com.github.soulsearching.playback.PlayerService
import com.github.soulsearching.utils.PlayerUtils
import kotlinx.coroutines.*
import java.io.File

/**
 * Implementation of the SoulSearchingAndroidPlayer
 */
class SoulSearchingAndroidPlayerImpl(
    private val context: Context,
    private val settings: SoulSearchingSettings
) :
    SoulSearchingAndroidPlayer(),
    MediaPlayer.OnCompletionListener,
    MediaPlayer.OnPreparedListener,
    MediaPlayer.OnErrorListener,
    AudioManager.OnAudioFocusChangeListener {

    private val player: MediaPlayer = MediaPlayer()
    private val mediaSession: MediaSessionCompat =
        MediaSessionCompat(context, context.packageName + "soulSearchingMediaSession")
    private val notificationService: SoulSearchingNotification =
        SoulSearchingNotificationBuilder.buildNotification(
            context = context,
            mediaSessionToken = mediaSession.sessionToken
        )
    private var currentDurationJob: Job? = null
    private var isOnlyLoadingMusic: Boolean = false

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .build()
    private val audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
        .setAudioAttributes(audioAttributes)
        .setAcceptsDelayedFocusGain(true)
        .setOnAudioFocusChangeListener(this)
        .build()

    /**
     * Broadcast receiver used to control the player from the notification.
     */
    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.extras?.getBoolean("STOP") != null) {
                when (intent.extras?.getBoolean("STOP") as Boolean) {
                    true -> pause()
                    false -> play()
                }
            }
        }
    }

    private val audioBecomingNoisyReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            pause()
        }
    }

    init {
        initializePlayer()
        initializeMediaSession()
        initializeBroadcastReceiver()
        notificationService.initializeNotification()
        manageAudioBecomingNoisy()
    }

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
    private fun play() {
        when (audioManager.requestAudioFocus(audioFocusRequest)) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                try {
                    PlayerUtils.playerViewModel.handler.isPlaying = true
                    player.start()
                    updateMediaSessionState()
                    notificationService.updateNotification()
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
    private fun pause() {
        try {
            PlayerUtils.playerViewModel.handler.isPlaying = false
            audioManager.abandonAudioFocusRequest(audioFocusRequest)
            player.pause()
            updateMediaSessionState()
            notificationService.updateNotification()
        } catch (_: IllegalStateException) {
        }
    }

    override fun togglePlayPause() {
        if (player.isPlaying) {
            pause()
        } else {
            play()
        }
    }

    override fun seekToPosition(position: Int) {
        player.seekTo(position)
        PlayerUtils.playerViewModel.handler.currentMusicPosition = position
        updateMediaSessionState()
        notificationService.updateNotification()
    }

    override fun next() {
        pause()
        PlayerUtils.playerViewModel.handler.setNextMusic()
        PlayerUtils.playerViewModel.handler.currentMusic?.let {
            setMusic(it)
            launchMusic()
        }
    }

    override fun previous() {
        pause()
        PlayerUtils.playerViewModel.handler.setPreviousMusic()
        PlayerUtils.playerViewModel.handler.currentMusic?.let {
            setMusic(it)
            launchMusic()
        }
    }

    override fun dismiss() {
        releaseDurationJob()

        player.release()
        mediaSession.release()
        context.unregisterReceiver(broadcastReceiver)
        notificationService.dismissNotification()
        audioManager.abandonAudioFocusRequest(audioFocusRequest)
        releaseAudioBecomingNoisyReceiver()
    }

    override fun getMusicPosition(): Int {
        return try {
            player.currentPosition
        } catch (e: IllegalStateException) {
            0
        }
    }

    override fun updateNotification() {
        updateMediaSessionMetadata()
        updateMediaSessionState()
        notificationService.updateNotification()
    }

    override fun getNotification(): Notification {
        return this.notificationService.getPlayerNotification()
    }

    private fun initializePlayer() {
        player.apply {
            setAudioAttributes(audioAttributes)
            setOnPreparedListener(this@SoulSearchingAndroidPlayerImpl)
            setOnCompletionListener(this@SoulSearchingAndroidPlayerImpl)
            setOnErrorListener(this@SoulSearchingAndroidPlayerImpl)
        }
    }

    /**
     * Initialize the media session used by the player.
     */
    @Suppress("DEPRECATION")
    private fun initializeMediaSession() {
        mediaSession.setCallback(object : MediaSessionCompat.Callback() {
            override fun onSeekTo(pos: Long) {
                seekToPosition(pos.toInt())
            }

            override fun onMediaButtonEvent(mediaButtonIntent: Intent): Boolean {
                val keyEvent = mediaButtonIntent.extras?.get(Intent.EXTRA_KEY_EVENT) as KeyEvent
                if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                    when (keyEvent.keyCode) {
                        KeyEvent.KEYCODE_MEDIA_PAUSE, KeyEvent.KEYCODE_MEDIA_PLAY -> togglePlayPause()
                    }
                }
                return super.onMediaButtonEvent(mediaButtonIntent)
            }

            override fun onPlay() {
                super.onPlay()
                play()
            }

            override fun onPause() {
                super.onPause()
                pause()
            }

            override fun onSkipToNext() {
                super.onSkipToNext()
                next()
            }

            override fun onSkipToPrevious() {
                super.onSkipToPrevious()
                previous()
            }
        })
        updateMediaSessionState()

        mediaSession.isActive = true
    }

    /**
     * Initialize the broadcast receiver used by the player.
     */
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun initializeBroadcastReceiver() {
        if (Build.VERSION.SDK_INT >= 33) {
            context.registerReceiver(
                broadcastReceiver, IntentFilter(BROADCAST_NOTIFICATION),
                Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            context.registerReceiver(broadcastReceiver, IntentFilter(BROADCAST_NOTIFICATION))
        }
    }

    override fun onCompletion(mp: MediaPlayer?) {
        CoroutineScope(Dispatchers.IO).launch {
            next()
        }
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        Log.d("MEDIA PLAYER", "ERROR CODE : $what, $extra")
        when (what) {
            MediaPlayer.MEDIA_ERROR_UNKNOWN -> {
                PlayerUtils.playerViewModel.handler.removeMusicFromCurrentPlaylist(
                    musicId = PlayerUtils.playerViewModel.handler.currentMusic!!.musicId
                )
            }
        }
        return true
    }

    override fun onPrepared(mp: MediaPlayer?) {
        if (isOnlyLoadingMusic) {
            if (PlayerUtils.playerViewModel.handler.currentMusicPosition <= player.duration) {
                player.seekTo(PlayerUtils.playerViewModel.handler.currentMusicPosition)
                releaseDurationJob()
                launchDurationJob()
            }
            isOnlyLoadingMusic = false
        } else {
            when (audioManager.requestAudioFocus(audioFocusRequest)) {
                AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> {
                    player.start()
                    PlayerUtils.playerViewModel.handler.currentMusic?.let {
                        PlayerUtils.playerViewModel.handler.updateNbPlayed(it.musicId)
                    }
                    PlayerUtils.playerViewModel.handler.isPlaying = true

                    releaseDurationJob()
                    launchDurationJob()

                    updateNotification()

                    settings.saveCurrentMusicInformation(
                        PlayerUtils.playerViewModel.handler.getIndexOfCurrentMusic(),
                        PlayerUtils.playerViewModel.handler.currentMusicPosition
                    )
                }

                else -> {}
            }
        }
    }

    override fun onAudioFocusChange(focusChange: Int) {
        Log.d("MEDIA PLAYER", "AUDIO FOCUS CHANGE : $focusChange")
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> play()
            AudioManager.AUDIOFOCUS_LOSS -> pause()
            else -> pause()
        }
    }

    /**
     * Launch a job that save the current music position to the shared preferences.
     * It is used to save the current music position between app sessions.
     */
    private fun launchDurationJob() {
        currentDurationJob = CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                withContext(Dispatchers.IO) {
                    Thread.sleep(1000)
                }
                PlayerUtils.playerViewModel.handler.currentMusicPosition =
                    PlayerService.getCurrentMusicPosition()
                if (player.isPlaying) {
                    settings.setInt(SoulSearchingSettings.PLAYER_MUSIC_POSITION_KEY, PlayerUtils.playerViewModel.handler.currentMusicPosition)
                }
            }
        }
    }

    /**
     * Release the duration job.
     */
    private fun releaseDurationJob() {
        if (currentDurationJob != null) {
            currentDurationJob!!.cancel()
            currentDurationJob = null
        }
    }

    /**
     * Update media session data with information the current played song in the player view model.
     */
    private fun updateMediaSessionMetadata() {
        val bitmap = if (PlayerUtils.playerViewModel.handler.currentMusicCover != null) {
            PlayerUtils.playerViewModel.handler.currentMusicCover?.asAndroidBitmap()
        } else {
            BitmapFactory.decodeResource(context.resources, R.drawable.notification_default)
        }

        mediaSession.setMetadata(
            MediaMetadataCompat.Builder()
                .putBitmap(
                    MediaMetadata.METADATA_KEY_ALBUM_ART,
                    bitmap
                )
                .putLong(
                    MediaMetadataCompat.METADATA_KEY_DURATION,
                    player.duration.toLong()
                )
                .putString(
                    MediaMetadata.METADATA_KEY_DISPLAY_TITLE,
                    PlayerUtils.playerViewModel.handler.currentMusic?.name
                )
                .putLong(
                    MediaMetadata.METADATA_KEY_TRACK_NUMBER,
                    PlayerUtils.playerViewModel.handler.getIndexOfCurrentMusic().toLong()
                )
                .putLong(
                    MediaMetadata.METADATA_KEY_NUM_TRACKS,
                    PlayerUtils.playerViewModel.handler.currentPlaylist.size.toLong()
                )
                // Pour les vieilles versions d'android
                .putString(
                    MediaMetadata.METADATA_KEY_TITLE,
                    PlayerUtils.playerViewModel.handler.currentMusic?.name
                )
                .putString(
                    MediaMetadata.METADATA_KEY_ARTIST,
                    PlayerUtils.playerViewModel.handler.currentMusic?.artist
                )
                // A small bitmap for the artwork is also recommended
                .putBitmap(
                    MediaMetadata.METADATA_KEY_ART,
                    bitmap
                )
                .build()
        )
    }

    /**
     * Update the state of the player's media session.
     */
    private fun updateMediaSessionState() {
        val musicState = if (player.isPlaying) {
            PlaybackState.STATE_PLAYING
        } else {
            PlaybackState.STATE_PAUSED
        }

        mediaSession.setPlaybackState(
            PlaybackStateCompat.Builder()
                .setActions(
                    PlaybackStateCompat.ACTION_PLAY
                            or PlaybackStateCompat.ACTION_SEEK_TO
                            or PlaybackStateCompat.ACTION_PAUSE
                            or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                            or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                            or PlaybackStateCompat.ACTION_PLAY_PAUSE
                )
                .setState(
                    musicState,
                    player.currentPosition.toLong(),
                    1.0F
                )
                .build()
        )
    }

    /**
     * Manage the audio becoming noisy event.
     */
    private fun manageAudioBecomingNoisy() {
        context.registerReceiver(
            audioBecomingNoisyReceiver,
            IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        )
    }

    /**
     * Release the broadcast receiver managing the audio becoming noisy event.
     */
    private fun releaseAudioBecomingNoisyReceiver() {
        context.unregisterReceiver(audioBecomingNoisyReceiver)
    }

    companion object {
        const val BROADCAST_NOTIFICATION = "BROADCAST_NOTIFICATION"
    }
}