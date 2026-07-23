package com.github.enteraname74.soulsearching.features.playback.player

import com.github.enteraname74.domain.model.Music
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import uk.co.caprica.vlcj.player.base.State
import uk.co.caprica.vlcj.player.component.AudioPlayerComponent

class SoulSearchingDesktopPlayerImpl :
    SoulSearchingPlayer,
    MediaPlayerEventAdapter() {

    private var player: MediaPlayer = AudioPlayerComponent().mediaPlayer()
    private var isOnlyLoadingMusic: Boolean = false
    private var positionToReachWhenLoadingMusic: Int = 0

    override var listener: SoulSearchingPlayer.Listener? = null

    init {
        init()
    }

    override suspend fun registerListener(listener: SoulSearchingPlayer.Listener) {
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
    }

    override fun paused(mediaPlayer: MediaPlayer?) {
        super.paused(mediaPlayer)
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

    private fun init() {
        NativeDiscovery().discover()
        player = AudioPlayerComponent().mediaPlayer()
        player.events().addMediaPlayerEventListener(this)
    }

    override suspend fun setMusic(music: Music) {
        try {
            if (player.status().state() == State.PLAYING) {
                player.controls().pause()
            }
            // Necessary to avoid blocking the app.
            delay(500)
            player.media().prepare(music.path)
        } catch (e: Exception) {
            println("SET MUSIC EXC: ${e.message}")
        }
    }

    override suspend fun onlyLoadMusic(seekTo: Int) {
        isOnlyLoadingMusic = true
        positionToReachWhenLoadingMusic = seekTo
        launchMusic()
    }

    override suspend fun launchMusic() {
        if (isOnlyLoadingMusic) {
            /*
             * When only loading the music, we try to seek to the last music position
             * (when loading a previous song which was at a given position)
             */
            seekToPosition(positionToReachWhenLoadingMusic)
            isOnlyLoadingMusic = false
            positionToReachWhenLoadingMusic = 0
        } else {
            player.controls().play()
        }
    }

    override suspend fun play() {
        player.controls().play()
    }

    override suspend fun pause() {
        player.controls().pause()
    }

    override suspend fun seekToPosition(millis: Int) {
        try {
            player.controls().setTime(millis.toLong())
        } catch (e: Exception) {
            println("PLAYER -- Exception on seekToPosition: $e")
        }
    }

    override suspend fun isPlaying(): Boolean {
        return player.status().isPlaying
    }

    override suspend fun dismiss() {
        try {
            player.controls().stop()
//            player.release()
        } catch (e: Exception) {
            println("Exception while stopping: $e")
        }
    }

    override suspend fun getProgress(): Int =
        try {
            player.status().time().toInt().positive()
        } catch (_: Exception) {
            0
        }

    override suspend fun getMusicDuration(): Int =
        try {
            player.status().length().toInt().positive()
        } catch (e: Exception) {
            println("PLAYER -- MUSIC DURATION EXC: $e")
            0
        }

    override suspend fun setPlayerVolume(volume: Float) {

        // On desktop impl, the volume is set from 0 to 200, but we will keep the max at 100.
        val fixedVolume: Int = (volume * 100).toInt().coerceIn(1, 100)
        player.audio().setVolume(fixedVolume)
    }

    /**
     * Returns the integer or 0 if it is negative.
     */
    private fun Int.positive(): Int = maxOf(this, 0)
}