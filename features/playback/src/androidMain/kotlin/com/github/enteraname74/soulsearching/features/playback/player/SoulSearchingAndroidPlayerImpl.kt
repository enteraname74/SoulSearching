package com.github.enteraname74.soulsearching.features.playback.player

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.util.Log
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.features.playback.SoulSearchingPlayer
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File

/**
 * Implementation of the SoulSearchingAndroidPlayer
 */
class SoulSearchingAndroidPlayerImpl(
    context: Context,
    private val playbackManager: PlaybackManager,
) :
    SoulSearchingPlayer,
    MediaPlayer.OnCompletionListener,
    MediaPlayer.OnPreparedListener,
    MediaPlayer.OnErrorListener {

    private var player: MediaPlayer = MediaPlayer()
    private var isOnlyLoadingMusic: Boolean = false
    private var positionToReachWhenLoadingMusic: Int = 0
    private val audioManager: PlayerAudioManager = PlayerAudioManager(context, playbackManager)

    private val _state: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val state: Flow<Boolean> = _state.asStateFlow()

    override fun init() {
        audioManager.init()
        player = MediaPlayer()
        player.apply {
            setAudioAttributes(audioManager.audioAttributes)
            setOnPreparedListener(this@SoulSearchingAndroidPlayerImpl)
            setOnCompletionListener(this@SoulSearchingAndroidPlayerImpl)
            setOnErrorListener(this@SoulSearchingAndroidPlayerImpl)
        }
    }

    init {
        init()
    }

    override fun setMusic(music: Music) {
        try {
            player.stop()
            player.reset()
        } catch (_: Exception) {}
        if (File(music.path).exists()) {
            player.setDataSource(music.path)
        }
    }

    override fun onlyLoadMusic(seekTo: Int) {
        isOnlyLoadingMusic = true
        positionToReachWhenLoadingMusic = seekTo
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
                    player.start()
                    _state.value = true
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
        try {
            audioManager.abandonAudioFocusRequest()
            player.pause()
            _state.value = false
        } catch (_: IllegalStateException) {
        }
    }

    override fun seekToPosition(position: Int) {
        try {
            player.seekTo(position)
        } catch (_: IllegalStateException) {

        }
    }

    override fun dismiss() {
        pause()
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

    override fun onCompletion(mp: MediaPlayer?) {
        CoroutineScope(Dispatchers.IO).launch {
            playbackManager.next()
        }
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        Log.d("MEDIA PLAYER", "ERROR CODE : $what, $extra")
        when (what) {
            MediaPlayer.MEDIA_ERROR_UNKNOWN -> {
                runBlocking {
                    playbackManager.skipAndRemoveCurrentSong()
                }
            }
        }
        return true
    }

    override fun onPrepared(mp: MediaPlayer?) {
        if (isOnlyLoadingMusic) {
            MediaPlayer.MEDIA_ERROR_UNKNOWN
            /*
             * When only loading the music, we try to seek to the last music position
             * (when loading a previous song which was at a given position)
             */
            seekToPosition(positionToReachWhenLoadingMusic)
            isOnlyLoadingMusic = false
            positionToReachWhenLoadingMusic = 0
        } else {
            when (audioManager.requestAudioFocus()) {
                AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> {
                    player.start()
                    _state.value = true
                }
                else -> {}
            }
        }
    }
}