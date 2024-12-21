package com.github.enteraname74.soulsearching.features.playback.player

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager

/**
 * Manage the audio of the application.
 * Define if the application is able to play.
 */
class PlayerAudioManager(
    private val context: Context,
    private val listener: Listener,
): AudioManager.OnAudioFocusChangeListener {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    val audioAttributes: AudioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .build()
    private val audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
        .setAudioAttributes(audioAttributes)
        .setAcceptsDelayedFocusGain(true)
        .setOnAudioFocusChangeListener(this)
        .build()

    private val audioBecomingNoisyReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            listener.onPause()
        }
    }

    /**
     * Initialize the audio manager.
     */
    fun init() {
        manageAudioBecomingNoisy()
    }

    /**
     * Release the hold on the audio.
     */
    fun abandonAudioFocusRequest() {
        audioManager.abandonAudioFocusRequest(audioFocusRequest)
    }

    /**
     * Release all elements related to the audio manager.
     */
    fun release() {
        abandonAudioFocusRequest()
        releaseAudioBecomingNoisyReceiver()
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> listener.onPlay()
            AudioManager.AUDIOFOCUS_LOSS -> listener.onPause()
            else -> listener.onPause()
        }
    }

    /**
     * Tries to retrieve the audio focus.
     */
    fun requestAudioFocus(): Int = audioManager.requestAudioFocus(audioFocusRequest)

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


    interface Listener {
        fun onPlay()
        fun onPause()
    }
}