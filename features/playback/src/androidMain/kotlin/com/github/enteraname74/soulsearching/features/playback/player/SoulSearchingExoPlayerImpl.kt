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
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.ResolvingDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.usecase.cloud.CommonCloudPreferencesUseCase
import com.github.enteraname74.domain.usecase.user.CommonUserUseCase
import com.github.enteraname74.soulsearching.features.playback.mediasession.MediaMetadataUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.CoroutineContext
import kotlin.uuid.Uuid

@UnstableApi
class SoulSearchingExoPlayerImpl(
    context: Context,
    commonCloudPreferencesUseCase: CommonCloudPreferencesUseCase,
    commonUserUseCase: CommonUserUseCase,
    private val mediaMetadataUtils: MediaMetadataUtils,
) : SoulSearchingPlayer {
    private var lastReportedIsPlaying: Boolean? = null

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

            val isPlayingState = when {
                player.isPlaying -> true

                !player.playWhenReady &&
                    player.playbackState != Player.STATE_ENDED -> false

                else -> null
            }

            if (isPlayingState == null || isPlayingState == lastReportedIsPlaying) return

            lastReportedIsPlaying = isPlayingState
            playerCoroutineScope.launch {
                if (isPlayingState) {
                    listener?.onPlay()
                } else {
                    listener?.onPause()
                }
            }
        }
    }

    private val httpFactory = DefaultHttpDataSource.Factory()
        .setUserAgent("Soul Searching")

    private val resolvingDataSourceFactory: DataSource.Factory =
        ResolvingDataSource.Factory(
            DefaultDataSource.Factory(context, httpFactory)
        ) { dataSpec: DataSpec -> resolveDataSpec(dataSpec) }

    private val mediaSourceFactory = DefaultMediaSourceFactory(context)
        .setDataSourceFactory(resolvingDataSourceFactory)

    val player: ExoPlayer = ExoPlayer
        .Builder(context)
        .setMediaSourceFactory(mediaSourceFactory)
        .build()
        .apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .build(),
                true
            )
            addListener(playerListener)
        }
    val playerDispatcher: PlayerDispatcher = PlayerDispatcher(player.applicationLooper)
    private val playerCoroutineScope = CoroutineScope(playerDispatcher)
    private val workScope = CoroutineScope(Dispatchers.IO)

    private var accessToken: StateFlow<String?> = commonUserUseCase
        .observeUser()
        .map { it?.accessToken }
        .stateIn(
            scope = workScope,
            started = SharingStarted.Eagerly,
            initialValue = null,
        )
    private var baseUrl: StateFlow<String?> = commonCloudPreferencesUseCase
        .observeUrl()
        .stateIn(
            scope = workScope,
            started = SharingStarted.Eagerly,
            initialValue = null,
        )

    override var listener: SoulSearchingPlayer.Listener? = null

    private suspend fun <T> onPlayerThread(block: suspend () -> T): Result<T> =
        withContext(playerDispatcher) {
            runCatching {
                block()
            }.onFailure {
                Log.e("PLAYER", "got player error: $it")
            }
        }

    private fun resolveDataSpec(original: DataSpec): DataSpec {
        val originalUri = original.uri
        val scheme = originalUri.scheme?.lowercase()

        // Local file / content / asset => do nothing
        if (
            scheme == "file" ||
            scheme == "content" ||
            scheme == "asset" ||
            scheme == "android.resource"
        ) {
            return original
        }

        val bearerToken = accessToken.value ?: return original
        val baseUrl = baseUrl.value ?: return original

        val mergedHeaders = HashMap(original.httpRequestHeaders)
        mergedHeaders["Authorization"] = "Bearer $bearerToken"

        val resolvedUri = resolveRelativeUri(
            baseUrl = baseUrl,
            relativePath = originalUri.toString()
        )

        return original
            .withUri(resolvedUri)
            .withRequestHeaders(mergedHeaders)
    }

    private fun resolveRelativeUri(baseUrl: String, relativePath: String): Uri {
        val base = baseUrl.toUri()
        val cleanedPath = relativePath.removePrefix("/")
        return base.buildUpon()
            .appendEncodedPath(cleanedPath)
            .build()
    }

    override suspend fun setMusic(music: Music) {
        onPlayerThread {
            val timelineIndex = player.timelineIndexOf(music.musicId)
            if (timelineIndex != C.INDEX_UNSET) {
                player.seekTo(timelineIndex, 0L)
                player.prepare()
                return@onPlayerThread
            }

            player.setMediaItem(music.toPlayableMediaItem())
            player.prepare()
        }
    }

    suspend fun syncPlayedListTimeline(
        musics: List<Music>,
        currentMusicId: Uuid?,
    ) {

        if (musics.isEmpty() || currentMusicId == null) {
            onPlayerThread {
                if (player.mediaItemCount > 0) {
                    player.clearMediaItems()
                }
            }
            return
        }

        val mediaItems = musics.mapIndexed { index, music ->
            music.toPlayableMediaItem(
                trackNumber = index + 1,
                totalTrackCount = musics.size,
            )
        }

        val targetIndex = musics.indexOfFirst { it.musicId == currentMusicId }
            .takeIf { it != -1 }
            ?: C.INDEX_UNSET

        onPlayerThread {
            val currentTimelineMusicId = player.currentMediaItem?.musicId()
            val shouldPrepareAfterTimelineUpdate =
                player.playWhenReady || player.playbackState != Player.STATE_IDLE

            if (!player.hasSameTimeline(mediaItems)) {
                player.setMediaItems(
                    mediaItems,
                    targetIndex.takeIf { it != C.INDEX_UNSET } ?: 0,
                    if (currentTimelineMusicId == currentMusicId) {
                        player.currentPosition.coerceAtLeast(0L)
                    } else {
                        0L
                    },
                )

                if (shouldPrepareAfterTimelineUpdate) {
                    player.prepare()
                }
                return@onPlayerThread
            }

            if (
                targetIndex != C.INDEX_UNSET &&
                player.currentMediaItemIndex != targetIndex
            ) {
                player.seekTo(targetIndex, 0L)
            }
        }
    }

    private fun Music.toPlayableMediaItem(
        trackNumber: Int? = null,
        totalTrackCount: Int? = null,
    ): MediaItem =
        MediaItem.Builder()
            .setMediaId(musicId.toString())
            .setUriFromMusic(this)
            .setMediaMetadata(
                mediaMetadataUtils
                    .fromMusic(
                        music = this,
                        // Will be set later, on notification callbacks from PlaybackManager
                        cover = null,
                    )
                    .apply {
                        trackNumber?.let(::setTrackNumber)
                        totalTrackCount?.let(::setTotalTrackCount)
                    }
                    .build()
            )
            .build()

    private fun ExoPlayer.hasSameTimeline(mediaItems: List<MediaItem>): Boolean {
        if (mediaItemCount != mediaItems.size) return false

        return mediaItems.indices.all { index ->
            getMediaItemAt(index).mediaId == mediaItems[index].mediaId
        }
    }

    private fun ExoPlayer.timelineIndexOf(musicId: Uuid): Int =
        (0 until mediaItemCount)
            .firstOrNull { index ->
                getMediaItemAt(index).musicId() == musicId
            } ?: C.INDEX_UNSET

    private fun MediaItem.musicId(): Uuid? =
        runCatching { Uuid.parse(mediaId) }.getOrNull()

    private fun MediaItem.Builder.setUriFromMusic(
        music: Music
    ): MediaItem.Builder =
        when {
            music.localPath != null && File(music.localPath.orEmpty()).exists() -> {
                val file = File(music.localPath.orEmpty())
                setUri(Uri.fromFile(file))
            }
            music.remotePath != null -> setUri(music.remotePath.orEmpty())
            else -> this
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
            player.stop()
            player.clearMediaItems()
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

class PlayerDispatcher(
    private val looper: Looper
) : CoroutineDispatcher() {
    val handler: Handler = Handler(looper)

    override fun isDispatchNeeded(context: CoroutineContext): Boolean =
        Looper.myLooper() != looper

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        if (Looper.myLooper() == looper) {
            block.run()
        } else {
            handler.post(block)
        }
    }
}
