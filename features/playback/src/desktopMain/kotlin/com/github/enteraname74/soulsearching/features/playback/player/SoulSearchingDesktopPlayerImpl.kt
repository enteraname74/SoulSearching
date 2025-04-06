package com.github.enteraname74.soulsearching.features.playback.player

import com.github.enteraname74.domain.model.DataMode
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.soulsearching.features.filemanager.cloud.CloudCacheManager
import com.github.enteraname74.soulsearching.features.httpclient.safeReadBytes
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import uk.co.caprica.vlcj.player.base.State
import uk.co.caprica.vlcj.player.component.AudioPlayerComponent

class SoulSearchingDesktopPlayerImpl(
    private val client: HttpClient,
    private val cloudCacheManager: CloudCacheManager
) :
    SoulSearchingPlayer,
    MediaPlayerEventAdapter() {

    private var player: MediaPlayer = AudioPlayerComponent().mediaPlayer()
    private var isOnlyLoadingMusic: Boolean = false
    private var positionToReachWhenLoadingMusic: Int = 0

    private val _state: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val state: Flow<Boolean> = _state.asStateFlow()
    override var listener: SoulSearchingPlayer.Listener? = null

    init {
        init()
    }

    override fun registerListener(listener: SoulSearchingPlayer.Listener) {
        this.listener = listener
    }

    override fun finished(mediaPlayer: MediaPlayer?) {
        super.finished(mediaPlayer)
        CoroutineScope(Dispatchers.IO).launch {
            listener?.onCompletion()
        }
    }

    override fun playing(mediaPlayer: MediaPlayer?) {
        super.playing(mediaPlayer)
        _state.value = player.status().isPlaying
    }

    override fun paused(mediaPlayer: MediaPlayer?) {
        super.paused(mediaPlayer)
        _state.value = player.status().isPlaying
    }

    override fun stopped(mediaPlayer: MediaPlayer?) {
        super.stopped(mediaPlayer)
    }

    override fun mediaPlayerReady(mediaPlayer: MediaPlayer?) {
        super.mediaPlayerReady(mediaPlayer)
    }

    override fun error(mediaPlayer: MediaPlayer?) {
        super.error(mediaPlayer)
        runBlocking {
            listener?.onError()
        }
    }

    override fun opening(mediaPlayer: MediaPlayer?) {
        super.opening(mediaPlayer)
    }

    override fun init() {
        NativeDiscovery().discover()
        player = AudioPlayerComponent().mediaPlayer()
        player.events().addMediaPlayerEventListener(this)
    }

    private suspend fun getMusicBytes(music: Music): SoulResult<ByteArray> =
        client.safeReadBytes { client.get(urlString = music.path) }

    private fun setLocalMusic(music: Music): SoulResult<Unit> {
        player.media().prepare(music.path)
        return SoulResult.ofSuccess()
    }

    private suspend fun setRemoteMusic(music: Music): SoulResult<Unit> {
        // We first try to retrieve the path of an already cached music
        val cachedMusicPath = cloudCacheManager.getPath(id = music.musicId)
        if (cachedMusicPath != null) {
            player.media().prepare(cachedMusicPath)
            return SoulResult.ofSuccess()
        }

        // Else, we fetch it from the cloud.
        val result = getMusicBytes(music)
        (result as? SoulResult.Success)?.data?.let { musicData ->
            cloudCacheManager.save(
                id = music.musicId,
                data = musicData,
            )

            cloudCacheManager.getPath(id = music.musicId)?.let { musicPath ->
                player.media().prepare(musicPath)
            }
        }

        return result.toSimpleResult()
    }

    override suspend fun setMusic(music: Music): SoulResult<Unit> {
        return try {
            if (player.status().state() == State.PLAYING) {
                player.controls().pause()
            }
            // Necessary to avoid blocking the app.
            delay(500)

            when(music.dataMode) {
                DataMode.Local -> setLocalMusic(music)
                DataMode.Cloud -> setRemoteMusic(music)
            }
        } catch (e: Exception) {
            println("SET MUSIC EXC: ${e.message}")
            // We don't want to show the error if an exception occurs (for now)
            SoulResult.ofSuccess()
        }
    }

    override fun onlyLoadMusic(seekTo: Int) {
        isOnlyLoadingMusic = true
        positionToReachWhenLoadingMusic = seekTo
        launchMusic()
    }

    override fun launchMusic() {
        if (isOnlyLoadingMusic) {
            /*
             * When only loading the music, we try to seek to the last music position
             * (when loading a previous song which was at a given position)
             */
            seekToPosition(positionToReachWhenLoadingMusic)
            isOnlyLoadingMusic = false
            positionToReachWhenLoadingMusic = 0
            _state.value = false
        } else {
            player.controls().play()
            _state.value = true
        }
    }

    override fun play() {
        player.controls().play()
        _state.value = true
    }

    override fun pause() {
        player.controls().pause()
        _state.value = false
    }

    override fun seekToPosition(position: Int) {
        try {
            player.controls().setTime(position.toLong())
        } catch (e: Exception) {
            println("PLAYER -- Exception on seekToPosition: $e")
        }
    }

    override fun isPlaying(): Boolean {
        return player.status().isPlaying
    }

    override fun dismiss() {
        try {
            player.controls().stop()
//            player.release()
        } catch (e: Exception) {
            println("Exception while stopping: $e")
        }
    }

    override fun getMusicPosition(): Int =
        try {
            player.status().time().toInt().positive()
        } catch (e: Exception) {
            0
        }

    override fun getMusicDuration(): Int =
        try {
            player.status().length().toInt().positive()
        } catch (e: Exception) {
            println("PLAYER -- MUSIC DURATION EXC: $e")
            0
        }

    override fun setPlayerVolume(volume: Float) {

        // On desktop impl, the volume is set from 0 to 200 ,but we will keep the max at 100.
        val fixedVolume: Int = (volume * 100).toInt().coerceIn(1, 100)
        player.audio().setVolume(fixedVolume)
    }

    /**
     * Returns the integer or 0 if it is negative.
     */
    private fun Int.positive(): Int = maxOf(this, 0)
}