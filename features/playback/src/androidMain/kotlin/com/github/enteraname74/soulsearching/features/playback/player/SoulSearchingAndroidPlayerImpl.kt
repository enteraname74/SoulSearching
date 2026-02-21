package com.github.enteraname74.soulsearching.features.playback.player

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.util.Log
import com.github.enteraname74.domain.model.Music
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File

/**
 * Implementation of the SoulSearchingAndroidPlayer
 */
class SoulSearchingAndroidPlayerImpl(
    context: Context,
) :
    SoulSearchingPlayer,
    MediaPlayer.OnCompletionListener,
    MediaPlayer.OnPreparedListener,
    MediaPlayer.OnErrorListener,
    PlayerAudioManager.Listener {

    private var player: MediaPlayer = MediaPlayer()
    private var isOnlyLoadingMusic: Boolean = false
    private var positionToReachWhenLoadingMusic: Int = 0
    private val audioManager: PlayerAudioManager = PlayerAudioManager(context, this)
    private val mutex = Mutex()
//    private val normalizer: AndroidPlayerNormalizer = AndroidPlayerNormalizer()

    private val _state: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val state: Flow<Boolean> = _state.asStateFlow()
    override var listener: SoulSearchingPlayer.Listener? = null

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

    override fun registerListener(listener: SoulSearchingPlayer.Listener) {
        this.listener = listener
    }

    override suspend fun setMusic(music: Music) {
        try {
            player.stop()
            player.reset()
            if (File(music.path).exists()) {
                mutex.withLock {
                    player.setDataSource(music.path)
                }

//            CoroutineScope(Dispatchers.IO).launch {
//                val volumeMultiplier: Float = normalizer.getVolumeMultiplier(music = music) ?: return@launch
//                println("Multiplier: $volumeMultiplier")
//                val newVolume: Float = 0.5f * volumeMultiplier
//                setPlayerVolume(newVolume)
//            }
            }
        } catch (e: Exception) {
            Log.e("PLAYER", "UNABLE TO SET MUSIC. GOT ERROR: ${e.message}")
        }
    }

    override fun onlyLoadMusic(seekTo: Int) {
        isOnlyLoadingMusic = true
        positionToReachWhenLoadingMusic = seekTo
        launchMusic()
    }

    override fun isPlaying(): Boolean? =
        runCatching {
            player.isPlaying
        }.getOrNull()

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

    override fun seekToPosition(millis: Int) {
        try {
            player.seekTo(millis)
        } catch (_: IllegalStateException) {

        }
    }

    override fun dismiss() {
        pause()
    }

    override fun getProgress(): Int {
        return try {
            player.currentPosition
        } catch (_: IllegalStateException) {
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
            listener?.onCompletion()
        }
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        Log.d("MEDIA PLAYER", "ERROR CODE : $what, $extra")
        when (what) {
            MediaPlayer.MEDIA_ERROR_UNKNOWN -> {
                runBlocking {
                    listener?.onError()
                }
            }
        }
        return true
    }

    override fun onPrepared(mp: MediaPlayer?) {
        if (isOnlyLoadingMusic) {
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

    override fun setPlayerVolume(volume: Float) {
        player.setVolume(volume, volume)
    }

    /*************** AUDIO MANAGER LISTENER IMPL ***************/

    override fun onPlay() {
        play()
    }

    override fun onPause() {
        pause()
    }
}