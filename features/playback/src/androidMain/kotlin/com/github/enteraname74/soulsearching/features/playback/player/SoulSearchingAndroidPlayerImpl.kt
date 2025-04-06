package com.github.enteraname74.soulsearching.features.playback.player

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.util.Log
import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.soulsearching.features.filemanager.cloud.CloudCacheManager
import com.github.enteraname74.soulsearching.features.httpclient.safeReadBytes
import io.ktor.client.*
import io.ktor.client.request.*
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
    private val client: HttpClient,
    private val cloudCacheManager: CloudCacheManager
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

    private suspend fun setLocalMusic(music: Music): SoulResult<Unit> {
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
        return SoulResult.ofSuccess()
    }

    private suspend fun setRemoteMusic(music: Music): SoulResult<Unit> {
        // We first try to retrieve the path of an already cached music
        val cachedMusicPath = cloudCacheManager.getPath(id = music.musicId)
        if (cachedMusicPath != null) {
            mutex.withLock {
                player.setDataSource(cachedMusicPath)
            }
            return SoulResult.ofSuccess()
        }

        // Else, we fetch it from the cloud.
        val result = client.safeReadBytes {
            get(urlString = music.path)
        }

        (result as? SoulResult.Success)?.data?.let { musicData ->
            cloudCacheManager.save(
                id = music.musicId,
                data = musicData,
            )

            cloudCacheManager.getPath(id = music.musicId)?.let { musicPath ->
                mutex.withLock {
                    player.setDataSource(musicPath)
                }
            }
        }

        return result.toSimpleResult()
    }

    override suspend fun setMusic(music: Music): SoulResult<Unit> {
        return try {
            player.stop()
            player.reset()

            when(music.dataMode) {
                DataMode.Local -> setLocalMusic(music)
                DataMode.Cloud -> setRemoteMusic(music)
            }
        } catch (e: Exception) {
            Log.e("PLAYER", "UNABLE TO SET MUSIC. GOT ERROR: ${e.message}")
            // We don't want to show the error if an exception occurs (for now)
            SoulResult.ofSuccess()
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