package com.github.enteraname74.soulsearching.model

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import com.github.enteraname74.soulsearching.feature.player.domain.model.SoulSearchingPlayer
import kotlinx.coroutines.*
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import uk.co.caprica.vlcj.player.base.State
import uk.co.caprica.vlcj.player.component.AudioPlayerComponent

class SoulSearchingDesktopPlayerImpl(
    private val playbackManager: PlaybackManager,
) :
    SoulSearchingPlayer,
    MediaPlayerEventAdapter() {

    private var player: MediaPlayer = AudioPlayerComponent().mediaPlayer()
    private var isOnlyLoadingMusic: Boolean = false
    private var positionToReachWhenLoadingMusic: Int = 0

    override fun finished(mediaPlayer: MediaPlayer?) {
        super.finished(mediaPlayer)
        CoroutineScope(Dispatchers.IO).launch {
            playbackManager.next()
        }
    }

    override fun playing(mediaPlayer: MediaPlayer?) {
        super.playing(mediaPlayer)
        playbackManager.update()
    }

    override fun paused(mediaPlayer: MediaPlayer?) {
        playbackManager.update()
    }

    override fun stopped(mediaPlayer: MediaPlayer?) {
        super.stopped(mediaPlayer)
    }

    override fun mediaPlayerReady(mediaPlayer: MediaPlayer?) {
        super.mediaPlayerReady(mediaPlayer)
    }

    override fun error(mediaPlayer: MediaPlayer?) {
        super.error(mediaPlayer)
        playbackManager.skipAndRemoveCurrentSong()
    }

    override fun opening(mediaPlayer: MediaPlayer?) {
        super.opening(mediaPlayer)
    }

    override fun init() {
        NativeDiscovery().discover()
        player = AudioPlayerComponent().mediaPlayer()
        player.events().addMediaPlayerEventListener(this)
    }

    override fun setMusic(music: Music) {
        try {
            if (player.status().state() == State.PLAYING) {
                player.controls().pause()
            }
            // Necessary to avoid blocking the app.
            Thread.sleep(500)
            player.media().prepare(music.path)
        } catch (e: Exception) {
            println("SET MUSIC EXC: ${e.message}")
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
        } else {
            player.controls().play()
            playbackManager.update()
        }
    }

    override fun play() {
        player.controls().play()
    }

    override fun pause() {
        player.controls().pause()
    }

    override fun seekToPosition(position: Int) {
        try {
            player.controls().setTime(position.toLong())
        } catch (_: Exception) {
        }
    }

    override fun togglePlayPause() {
        super.togglePlayPause()
        playbackManager.update()
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
            0
        }

    /**
     * Returns the integer or 0 if it is negative.
     */
    private fun Int.positive(): Int = maxOf(this, 0)
}