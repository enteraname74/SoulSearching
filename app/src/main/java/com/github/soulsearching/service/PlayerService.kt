package com.github.soulsearching.service

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.KeyEvent
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.MediaItemTransitionReason
import androidx.media3.exoplayer.ExoPlayer

class PlayerService : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("START COMMAND","")
        player = ExoPlayer.Builder(this).build()

        player.addListener(
            object : Player.Listener {
                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    super.onMediaItemTransition(mediaItem, reason)
                    // Update app ui
                }
            }
        )

        audioManager = applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()

        onAudioFocusChange = AudioManager.OnAudioFocusChangeListener { focusChange ->
            Log.d("focusChange", focusChange.toString())
            when (focusChange) {
                AudioManager.AUDIOFOCUS_GAIN -> {
                    Log.d("PLAYBACK SERVICE", "GAIN FOCUS WHEN OTHER APP WAS PLAYING")
                }
                AudioManager.AUDIOFOCUS_LOSS -> {
                }
                else -> {
                }
            }
        }

        //mediaSession = MediaSession(applicationContext, packageName+"mediaSessionPlayer")
        //mediaSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS or MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS)

        mediaSession = MediaSessionCompat(applicationContext, packageName+"mediaSessionPlayer")

        mediaSession.setCallback(object : MediaSessionCompat.Callback() {
            override fun onSeekTo(pos: Long) {
                /*
                mediaPlayer.seekTo(pos.toInt())
                val intentForNotification = Intent("BROADCAST_NOTIFICATION")
                intentForNotification.putExtra("STOP", !mediaPlayer.isPlaying)
                applicationContext.sendBroadcast(intentForNotification)
                 */
            }

            override fun onMediaButtonEvent(mediaButtonIntent: Intent): Boolean {
                Log.d("PLAYBACK SERVICE", mediaButtonIntent.extras?.get(Intent.EXTRA_KEY_EVENT).toString())
                /*
                if (MyMediaPlayer.currentIndex != -1) {
                    val keyEvent = mediaButtonIntent.extras?.get(Intent.EXTRA_KEY_EVENT) as KeyEvent
                    if (keyEvent.action == KeyEvent.ACTION_DOWN){
                        when(keyEvent.keyCode){
                            KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                                val audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                                    .setAudioAttributes(audioAttributes)
                                    .setAcceptsDelayedFocusGain(true)
                                    .setOnAudioFocusChangeListener(onAudioFocusChange)
                                    .build()

                                when (audioManager.requestAudioFocus(audioFocusRequest)) {
                                    AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> {
                                    }
                                    else -> {}
                                }
                            }
                            KeyEvent.KEYCODE_MEDIA_PLAY -> {
                                val audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                                    .setAudioAttributes(audioAttributes)
                                    .setAcceptsDelayedFocusGain(true)
                                    .setOnAudioFocusChangeListener(onAudioFocusChange)
                                    .build()

                                when (audioManager.requestAudioFocus(audioFocusRequest)) {
                                    AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> {
                                    }
                                    else -> {}
                                }
                            }
                            KeyEvent.KEYCODE_MEDIA_NEXT -> {
                                val audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                                    .setAudioAttributes(audioAttributes)
                                    .setAcceptsDelayedFocusGain(true)
                                    .setOnAudioFocusChangeListener(onAudioFocusChange)
                                    .build()

                                try {
                                    when (audioManager.requestAudioFocus(audioFocusRequest)) {
                                        AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> {
                                        }
                                    }
                                } catch (error : Error){
                                    Log.d("error","")
                                }
                            }
                            KeyEvent.KEYCODE_MEDIA_PREVIOUS -> {
                                val audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                                    .setAudioAttributes(audioAttributes)
                                    .setAcceptsDelayedFocusGain(true)
                                    .setOnAudioFocusChangeListener(onAudioFocusChange)
                                    .build()

                                try {
                                    when (audioManager.requestAudioFocus(audioFocusRequest)) {
                                        AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> {
                                        }
                                    }
                                } catch (error : Error){
                                    Log.d("error","")
                                }
                            }
                        }
                    }
                }

                 */
                return super.onMediaButtonEvent(mediaButtonIntent)
            }
        })

        mediaSession.setPlaybackState(updateMediaSessionState(PlaybackStateCompat.STATE_PLAYING, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN))
        mediaSession.isActive = true

        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("ON CREATE", "")
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Log.d("PLAYBACK SERVICE", "REMOVED")
        stopMusic()
    }

    private fun stopMusic(){
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(1)
    }

    companion object {
        lateinit var onAudioFocusChange : AudioManager.OnAudioFocusChangeListener
        lateinit var audioAttributes : AudioAttributes
        lateinit var audioManager : AudioManager
        lateinit var mediaSession : MediaSessionCompat
        lateinit var player : ExoPlayer

        fun updateMediaSessionState(musicState: Int, musicPosition: Long): PlaybackStateCompat {
            return PlaybackStateCompat.Builder()
                .setActions(
                    PlaybackStateCompat.ACTION_PLAY
                            or PlaybackStateCompat.ACTION_SEEK_TO
                            or PlaybackStateCompat.ACTION_PAUSE
                            or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                            or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                            or PlaybackStateCompat.ACTION_PLAY_PAUSE
                )
                .setState(
                    musicState,
                    musicPosition,
                    1.0F
                )
                .build()
        }
    }
}