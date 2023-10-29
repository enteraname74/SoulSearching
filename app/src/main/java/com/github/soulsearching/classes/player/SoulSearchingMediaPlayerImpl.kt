package com.github.soulsearching.classes.player

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
import com.github.soulsearching.R
import com.github.soulsearching.classes.PlayerUtils
import com.github.soulsearching.classes.SharedPrefUtils
import com.github.soulsearching.classes.notification.SoulSearchingNotification
import com.github.soulsearching.classes.notification.notificationImpl.SoulSearchingNotificationAndroid13
import com.github.soulsearching.classes.notification.notificationImpl.SoulSearchingNotificationBelowAndroid13
import com.github.soulsearching.database.model.Music
import com.github.soulsearching.service.PlayerService
import kotlinx.coroutines.*
import java.io.File

class SoulSearchingMediaPlayerImpl(private val context: Context) :
    SoulSearchingPlayer,
    MediaPlayer.OnCompletionListener,
    MediaPlayer.OnPreparedListener,
    MediaPlayer.OnErrorListener,
    AudioManager.OnAudioFocusChangeListener {

    private val player: MediaPlayer = MediaPlayer()
    private val mediaSession: MediaSessionCompat =
        MediaSessionCompat(context, context.packageName + "soulSearchingMediaSession")
    private val notificationService: SoulSearchingNotification = if (Build.VERSION.SDK_INT >= 33) {
        SoulSearchingNotificationAndroid13(context, mediaSession.sessionToken)
    } else {
        SoulSearchingNotificationBelowAndroid13(context, mediaSession.sessionToken)
    }
    private var currentDurationJob : Job? = null
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

    private fun play() {
        when (audioManager.requestAudioFocus(audioFocusRequest)) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                try {
                    PlayerUtils.playerViewModel.isPlaying = true
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

    private fun pause() {
        try {
            PlayerUtils.playerViewModel.isPlaying = false
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
        PlayerUtils.playerViewModel.currentMusicPosition = position
        updateMediaSessionState()
        notificationService.updateNotification()
    }

    override fun next() {
        pause()
        PlayerUtils.playerViewModel.setNextMusic()
        PlayerUtils.playerViewModel.currentMusic?.let {
            setMusic(it)
            launchMusic()
        }
    }

    override fun previous() {
        pause()
        PlayerUtils.playerViewModel.setPreviousMusic()
        PlayerUtils.playerViewModel.currentMusic?.let {
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
            setOnPreparedListener(this@SoulSearchingMediaPlayerImpl)
            setOnCompletionListener(this@SoulSearchingMediaPlayerImpl)
            setOnErrorListener(this@SoulSearchingMediaPlayerImpl)
        }
    }

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

    private fun initializeBroadcastReceiver() {
        context.registerReceiver(broadcastReceiver, IntentFilter(BROADCAST_NOTIFICATION))
    }

    override fun onCompletion(mp: MediaPlayer?) {
        CoroutineScope(Dispatchers.IO).launch {
            next()
        }
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        Log.d("MEDIA PLAYER", "ERROR CODE : $what, $extra")
        when(what) {
            MediaPlayer.MEDIA_ERROR_UNKNOWN -> {
                PlayerUtils.playerViewModel.removeMusicFromCurrentPlaylist(
                    musicId = PlayerUtils.playerViewModel.currentMusic!!.musicId,
                    context = context
                )
            }
        }
        return true
    }

    override fun onPrepared(mp: MediaPlayer?) {
        if (isOnlyLoadingMusic) {
            if (PlayerUtils.playerViewModel.currentMusicPosition <= player.duration) {
                player.seekTo(PlayerUtils.playerViewModel.currentMusicPosition)
                releaseDurationJob()
                launchDurationJob()
            }
            isOnlyLoadingMusic = false
        } else {
            when (audioManager.requestAudioFocus(audioFocusRequest)) {
                AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> {
                    player.start()
                    PlayerUtils.playerViewModel.currentMusic?.let {
                        PlayerUtils.playerViewModel.updateNbPlayed(it.musicId)
                    }
                    PlayerUtils.playerViewModel.isPlaying = true

                    releaseDurationJob()
                    launchDurationJob()

                    updateNotification()

                    SharedPrefUtils.setPlayerSavedCurrentMusic()
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

    private fun launchDurationJob() {
        currentDurationJob = CoroutineScope(Dispatchers.IO).launch {
            while(true){
                withContext(Dispatchers.IO) {
                    Thread.sleep(1000)
                }
                PlayerUtils.playerViewModel.currentMusicPosition = PlayerService.getCurrentMusicPosition()
                if (player.isPlaying) {
                    SharedPrefUtils.setCurrentMusicPosition()
                }
            }
        }
    }

    private fun releaseDurationJob() {
        if (currentDurationJob != null) {
            currentDurationJob!!.cancel()
            currentDurationJob = null
        }
    }

    private fun updateMediaSessionMetadata() {
        val bitmap = if (PlayerUtils.playerViewModel.currentMusicCover != null) {
            PlayerUtils.playerViewModel.currentMusicCover
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
                    PlayerUtils.playerViewModel.currentMusic?.name
                )
                .putLong(
                    MediaMetadata.METADATA_KEY_TRACK_NUMBER,
                    PlayerUtils.playerViewModel.getIndexOfCurrentMusic().toLong()
                )
                .putLong(
                    MediaMetadata.METADATA_KEY_NUM_TRACKS,
                    PlayerUtils.playerViewModel.currentPlaylist.size.toLong()
                )
                // Pour les vieilles versions d'android
                .putString(
                    MediaMetadata.METADATA_KEY_TITLE,
                    PlayerUtils.playerViewModel.currentMusic?.name
                )
                .putString(
                    MediaMetadata.METADATA_KEY_ARTIST,
                    PlayerUtils.playerViewModel.currentMusic?.artist
                )
                // A small bitmap for the artwork is also recommended
                .putBitmap(
                    MediaMetadata.METADATA_KEY_ART,
                    bitmap
                )
                .build()
        )
    }

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

    private fun manageAudioBecomingNoisy() {
        context.registerReceiver(
            audioBecomingNoisyReceiver,
            IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        )
    }

    private fun releaseAudioBecomingNoisyReceiver() {
        context.unregisterReceiver(audioBecomingNoisyReceiver)
    }

    companion object {
        const val BROADCAST_NOTIFICATION = "BROADCAST_NOTIFICATION"
    }
}