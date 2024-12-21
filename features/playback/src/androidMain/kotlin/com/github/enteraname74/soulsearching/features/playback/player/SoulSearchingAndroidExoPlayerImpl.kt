package com.github.enteraname74.soulsearching.features.playback.player

import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.github.enteraname74.domain.model.Music
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File

/**
 * Implementation of the SoulSearchingAndroidPlayer using ExoPlayer
 */
class SoulSearchingExoPlayerImpl(
    private val player: ExoPlayer
) :
    SoulSearchingPlayer,
    Player.Listener {

    private var isOnlyLoadingMusic: Boolean = false
    private var positionToReachWhenLoadingMusic: Long = 0L

    private val _state: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val state: Flow<Boolean> = _state.asStateFlow()
    override var listener: SoulSearchingPlayer.Listener? = null

    init {
        init()
    }

    override fun init() {
        player.addListener(this)
        player
    }

    override fun registerListener(listener: SoulSearchingPlayer.Listener) {
        this.listener = listener
    }

    override fun setMusic(music: Music) {
        runOnMainThreadAsync {
            try {
                if (File(music.path).exists()) {
                    val mediaItem = MediaItem.fromUri(music.path)
                    player.setMediaItem(mediaItem)
                } else {
                    listener?.onError()
                }
            } catch (e: Exception) {
                Log.e("ExoPlayer", "Error setting music: ${e.message}")
            }
        }
    }

    override fun onlyLoadMusic(seekTo: Int) {
        isOnlyLoadingMusic = true
        positionToReachWhenLoadingMusic = seekTo.toLong()
        launchMusic()
    }

    override fun isPlaying(): Boolean =
        runOnMainThread {
            try {
                player.isPlaying
            } catch (_: Exception) {
                false
            }
        }

    override fun launchMusic() {
        runOnMainThreadAsync {
            try {
                player.prepare()
            } catch (e: Exception) {
                Log.e("ExoPlayer", "Error preparing music: ${e.message}")
            }
        }
    }

    override fun play() {
        runOnMainThreadAsync {
            try {
                player.play()
                _state.value = true
            } catch (e: Exception) {
                Log.e("ExoPlayer", "Error starting playback: ${e.message}")
            }
        }
    }

    override fun pause() {
        runOnMainThreadAsync {
            try {
                player.pause()
                _state.value = false
            } catch (e: Exception) {
                Log.e("ExoPlayer", "Error pausing playback: ${e.message}")
            }
        }
    }

    override fun seekToPosition(position: Int) {
        runOnMainThreadAsync {
            try {
                player.seekTo(position.toLong())
            } catch (e: Exception) {
                Log.e("ExoPlayer", "Error seeking position: ${e.message}")
            }
        }
    }

    override fun dismiss() {
        runOnMainThreadAsync {
            pause()
            player.stop()
        }
    }

    override fun getMusicPosition(): Int =
        runOnMainThread {
            try {
                player.currentPosition.toInt()
            } catch (_: Exception) {
                0
            }
        }

    override fun getMusicDuration(): Int =
        runOnMainThread {
            try {
                player.duration.toInt()
            } catch (_: Exception) {
                0
            }
        }


    override fun setPlayerVolume(volume: Float) {
        runOnMainThreadAsync {
            player.volume = volume
        }
    }

    override fun onEvents(player: Player, events: Player.Events) {
        if (events.contains(Player.EVENT_IS_PLAYING_CHANGED)) {
            _state.value = player.isPlaying
        }
    }

    override fun onPlaybackStateChanged(state: Int) {
        when (state) {
            Player.STATE_ENDED -> {
                CoroutineScope(Dispatchers.IO).launch {
                    listener?.onCompletion()
                }
            }
            Player.STATE_READY -> {
                /*
                 * When only loading the music, we try to seek to the last music position
                 * (when loading a previous song which was at a given position)
                 */
                if (isOnlyLoadingMusic) {
                    seekToPosition(positionToReachWhenLoadingMusic.toInt())
                    isOnlyLoadingMusic = false
                    positionToReachWhenLoadingMusic = 0L
                }
            }
            Player.STATE_IDLE, Player.STATE_BUFFERING -> {
                /*no-op*/
            }
        }
    }

    override fun onPlayerError(error: PlaybackException) {
        Log.e("ExoPlayer", "Error: ${error.message}")
        runBlocking {
            listener?.onError()
        }
    }

    private fun runOnMainThreadAsync(block: suspend () -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            block()
        }
    }

    private fun <T> runOnMainThread(block: suspend () -> T): T = runBlocking(Dispatchers.Main) {
        block()
    }
}