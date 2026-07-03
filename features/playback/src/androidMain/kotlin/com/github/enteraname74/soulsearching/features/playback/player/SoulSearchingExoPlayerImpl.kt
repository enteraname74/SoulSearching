package com.github.enteraname74.soulsearching.features.playback.player

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.net.toUri
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.usecase.music.CommonMusicUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.CoroutineContext

@UnstableApi
class SoulSearchingExoPlayerImpl(
    context: Context,
    private val commonMusicUseCase: CommonMusicUseCase,
) : SoulSearchingPlayer {
    private val player = ExoPlayer
        .Builder(context)
        .build()
    private val playerDispatcher = PlayerDispatcher(player.applicationLooper)
    private val playerCoroutineScope = CoroutineScope(playerDispatcher)

    private val playerListener = object : Player.Listener {
        override fun onPlayerError(error: PlaybackException) {
            playerCoroutineScope.launch {
                listener?.onError()
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            if (playbackState == Player.STATE_ENDED) {
                playerCoroutineScope.launch {
                    listener?.onCompletion()
                }
            }
        }

        override fun onEvents(player: Player, events: Player.Events) {
            if (!events.contains(Player.EVENT_IS_PLAYING_CHANGED) &&
                !events.contains(Player.EVENT_PLAY_WHEN_READY_CHANGED) &&
                !events.contains(Player.EVENT_PLAYBACK_STATE_CHANGED)
            ) return

            playerCoroutineScope.launch {
                when {
                    player.isPlaying -> listener?.onPlay()

                    !player.playWhenReady &&
                        player.playbackState != Player.STATE_ENDED -> {
                        listener?.onPause()
                    }

                    else -> {
                        // isPlaying=false but playWhenReady=true:
                        // seeking, buffering, waiting, preparing. Not a real pause.
                    }
                }
            }
        }
    }
    override var listener: SoulSearchingPlayer.Listener? = null

    private suspend fun <T> onPlayerThread(block: suspend () -> T): Result<T> =
        withContext(playerDispatcher) {
            runCatching {
                block()
            }.onFailure {
                Log.e("PLAYER", "got player error: $it")
            }
        }

    init {
        runBlocking { init() }
    }

    suspend fun init() {
        onPlayerThread {
            player.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .build(),
                true
            )
            player.addListener(playerListener)
        }
    }

    override suspend fun setMusic(music: Music) {
        val uri = music.toPlayableUri()

        if (uri == null) {
            listener?.onError()
            return
        }

        onPlayerThread {
            val mediaItem = MediaItem.fromUri(uri)
            player.setMediaItem(mediaItem)
            player.prepare()
        }
    }

    private suspend fun Music.toPlayableUri(): Uri? {
        localPath
            ?.takeIf { File(it).exists() }
            ?.let { return Uri.fromFile(File(it)) }

        return remotePath
            ?.let { commonMusicUseCase.getSignedUrl(it) }
            ?.toUri()
    }

    override suspend fun onlyLoadMusic(seekTo: Int) {
        onPlayerThread {
            player.prepare()
            player.seekTo(seekTo.toLong())
        }
    }

    override suspend fun launchMusic() {
        onPlayerThread {
            player.play()
        }
    }

    override suspend fun play() {
        onPlayerThread {
            player.play()
        }
    }

    override suspend fun pause() {
        onPlayerThread {
            player.pause()
        }
    }

    override suspend fun seekToPosition(millis: Int) {
        onPlayerThread {
            player.seekTo(millis.toLong())
        }
    }

    override suspend fun isPlaying(): Boolean? =
        onPlayerThread {
            player.isPlaying
        }.getOrNull()

    override suspend fun dismiss() {
        onPlayerThread {
            // TODO PLAYER: Really dismiss player?
            player.pause()
        }
    }

    override suspend fun getProgress(): Int =
        onPlayerThread {
            player.currentPosition.toInt()
        }.getOrElse { 0 }

    override suspend fun getMusicDuration(): Int =
        onPlayerThread {
            player.duration.toInt()
        }.getOrElse { 0 }

    override suspend fun setPlayerVolume(volume: Float) {
        onPlayerThread {
            player.volume = volume
        }
    }
}

private class PlayerDispatcher(
    looper: Looper
) : CoroutineDispatcher() {
    private val handler = Handler(looper)

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        handler.post(block)
    }
}
