package com.github.enteraname74.soulsearching.features.playback.player

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.util.WorkDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import uk.co.caprica.vlcj.player.base.State
import uk.co.caprica.vlcj.player.component.AudioPlayerComponent
import java.io.File
import kotlin.time.Duration.Companion.milliseconds

class SoulSearchingDesktopPlayerImpl(
    workDispatcher: WorkDispatcher,
    private val signedPlaybackUrlProvider: SignedPlaybackUrlProvider,
) :
    SoulSearchingPlayer,
    MediaPlayerEventAdapter() {
    private var startPlayBuffered: Boolean = false
    private var startPauseBuffered: Boolean = false
    private val workScope = CoroutineScope(workDispatcher.dispatcher)

    private var player: MediaPlayer = AudioPlayerComponent().mediaPlayer()
    override var listener: SoulSearchingPlayer.Listener? = null

    init {
        init()
    }

    override fun finished(mediaPlayer: MediaPlayer?) {
        super.finished(mediaPlayer)
        workScope.launch {
            listener?.onCompletion()
        }
    }

    override fun playing(mediaPlayer: MediaPlayer?) {
        if (startPlayBuffered) {
            startPlayBuffered = false
            return
        }
        super.playing(mediaPlayer)
        workScope.launch {
            listener?.onPlay()
        }
    }

    override fun paused(mediaPlayer: MediaPlayer?) {
        if (startPauseBuffered) {
            startPauseBuffered = false
            return
        }
        super.paused(mediaPlayer)
        workScope.launch {
            listener?.onPause()
        }
    }

    override fun stopped(mediaPlayer: MediaPlayer?) {
        super.stopped(mediaPlayer)
        workScope.launch {
            listener?.onPause()
        }
    }

    override fun error(mediaPlayer: MediaPlayer?) {
        super.error(mediaPlayer)
        workScope.launch {
            listener?.onError()
        }
    }

    private fun init() {
        NativeDiscovery().discover()
        player = AudioPlayerComponent().mediaPlayer()
        player.events().addMediaPlayerEventListener(this)
    }

    override suspend fun setMusic(music: Music) {
        try {
            if (player.status().state() == State.PLAYING) {
                player.controls().stop()
            }
            // Necessary to avoid blocking the app.
            delay(500.milliseconds)
            when {
                music.localPath != null && File(music.localPath.orEmpty()).exists() -> {
                    safeStartPaused(music.localPath!!)
                }
                music.remotePath != null -> {
                    setFromRemote(music = music)
                }
                else -> {
                    listener?.onError()
                }
            }

        } catch (e: Exception) {
            println("SET MUSIC EXC: ${e.message}")
        }
    }

    /**
     * Because player startPaused method quickly start playing the song then pauses it,
     * we need to avoid emitting events.
     */
    private fun safeStartPaused(path: String) {
        startPlayBuffered = true
        startPauseBuffered = true
        player.media().startPaused(path)
    }

    private suspend fun setFromRemote(music: Music) {
        val token = signedPlaybackUrlProvider.getUpdatedToken()

        if (token == null || music.remoteId == null) {
            listener?.onError()
            return
        }
        val musicUrl = signedPlaybackUrlProvider.getMusicUrl(
            token = token,
            remoteId = music.remoteId.orEmpty(),
        )
        safeStartPaused(musicUrl)
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

    override suspend fun getState(): SoulSearchingPlayer.State {
        val state = player.status().state()

        return when (state) {
            State.PLAYING -> SoulSearchingPlayer.State.Playing
            State.PAUSED -> SoulSearchingPlayer.State.Paused
            else -> SoulSearchingPlayer.State.Idle
        }
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