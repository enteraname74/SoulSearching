package com.github.enteraname74.soulsearching.features.playback.mediasession

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.annotation.OptIn
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.media3.common.C
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaLibraryService.MediaLibrarySession
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionCommands
import androidx.media3.session.SessionError
import androidx.media3.session.SessionResult
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.Scope
import com.github.enteraname74.domain.model.player.PlayedListScope
import com.github.enteraname74.domain.usecase.music.ToggleMusicFavoriteStatusUseCase
import com.github.enteraname74.domain.util.WorkDispatcher
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManagerState
import com.github.enteraname74.soulsearching.features.playback.model.UpdateData
import com.github.enteraname74.soulsearching.features.playback.player.SoulSearchingExoPlayerImpl
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.uuid.Uuid

/**
 * Manage Media3 session state exposed to system UI, headset controls and external controllers.
 */
@OptIn(UnstableApi::class)
class MediaSessionManager(
    private val context: Context,
    private val toggleMusicFavoriteStatusUseCase: ToggleMusicFavoriteStatusUseCase,
    private val soulSearchingPlayer: SoulSearchingExoPlayerImpl,
    private val mediaMetadataUtils: MediaMetadataUtils,
    workDispatcher: WorkDispatcher,
) : KoinComponent {
    private val playbackManager: PlaybackManager by inject()

    private var mediaSession: MediaSession? = null
    private var currentPlayedListScope: PlayedListScope? = null
    private var isFavoriteActionAvailable: Boolean = false
    private var isCurrentMusicInFavorite: Boolean = false
    private var playedListTimelineJob: Job? = null

    private val coroutineScope = CoroutineScope(workDispatcher.dispatcher)
    private val favoriteCommand = SessionCommand(FAVORITE_ACTION, Bundle.EMPTY)

    @SuppressLint("ObsoleteSdkInt")
    private val activityPendingIntent: PendingIntent = PendingIntent.getActivity(
        context,
        0,
        Intent().apply {
            setClassName(context.packageName, MAIN_ACTIVITY_CLASS_NAME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
        },
        PendingIntent.FLAG_UPDATE_CURRENT or
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_IMMUTABLE
            } else {
                0
            }
    )

    private val sessionPlayer: Player by lazy {
        SoulSearchingSessionPlayer(
            player = soulSearchingPlayer.player,
            playbackManager = { playbackManager },
            coroutineScope = coroutineScope,
            canControl = { currentPlayedListScope?.isAdmin == true },
        )
    }

    fun updateMediaSession(
        updateData: UpdateData,
    ): MediaSession {
        ensureListeningToPlayedListTimeline()
        currentPlayedListScope = updateData.playedListScope
        isFavoriteActionAvailable = updateData.music.scope != Scope.SharedPlayedList
        isCurrentMusicInFavorite = updateData.isInFavorite

        val session = mediaSession ?: init().also {
            mediaSession = it
        }

        updateMetadata(updateData)
        updateAvailableCommands(session)
        return session
    }

    private fun init(): MediaSession =
        MediaSession.Builder(context, sessionPlayer)
            .setSessionActivity(activityPendingIntent)
            .setCallback(
                object : MediaSession.Callback {
                    override fun onConnect(
                        session: MediaSession,
                        controller: MediaSession.ControllerInfo,
                    ): MediaSession.ConnectionResult =
                        this@MediaSessionManager.onConnect(session)

                    override fun onCustomCommand(
                        session: MediaSession,
                        controller: MediaSession.ControllerInfo,
                        customCommand: SessionCommand,
                        args: Bundle,
                    ): ListenableFuture<SessionResult> =
                        this@MediaSessionManager.onCustomCommand(customCommand)
                }
            )
            .setMediaButtonPreferences(buildMediaButtonPreferences())
            .build()

    fun getOrCreateMediaLibrarySession(
        callback: MediaLibrarySession.Callback,
    ): MediaLibrarySession {
        ensureListeningToPlayedListTimeline()
        (mediaSession as? MediaLibrarySession)?.let { return it }

        mediaSession?.release()
        return MediaLibrarySession.Builder(context, sessionPlayer, callback)
            .setSessionActivity(activityPendingIntent)
            .setMediaButtonPreferences(buildMediaButtonPreferences())
            .build()
            .also { mediaSession = it }
    }

    fun onConnect(
        session: MediaSession,
    ): MediaSession.ConnectionResult =
        MediaSession.ConnectionResult.AcceptedResultBuilder(session)
            .setAvailableSessionCommands(buildSessionCommands(session))
            .setAvailablePlayerCommands(buildPlayerCommands())
            .setMediaButtonPreferences(buildMediaButtonPreferences())
            .build()

    fun onCustomCommand(
        customCommand: SessionCommand,
    ): ListenableFuture<SessionResult> {
        if (
            customCommand.customAction == FAVORITE_ACTION &&
            isFavoriteActionAvailable
        ) {
            playbackManager.currentSong.value
                ?.takeIf { it.scope != Scope.SharedPlayedList }
                ?.musicId
                ?.let { musicId ->
                    coroutineScope.launch {
                        toggleMusicFavoriteStatusUseCase(musicId = musicId)
                    }
                }
            return Futures.immediateFuture(
                SessionResult(SessionResult.RESULT_SUCCESS)
            )
        }

        return Futures.immediateFuture(
            SessionResult(SessionError.ERROR_NOT_SUPPORTED)
        )
    }

    /**
     * Release all elements related to the media session.
     */
    fun release() {
        playedListTimelineJob?.cancel()
        playedListTimelineJob = null
        mediaSession?.release()
        mediaSession = null
    }

    /**
     * Clear the player state that keeps the media notification alive.
     */
    fun clearPlaybackState() {
        currentPlayedListScope = null
        isFavoriteActionAvailable = false
        isCurrentMusicInFavorite = false
        mediaSession?.let(::updateAvailableCommands)

        val player = soulSearchingPlayer.player
        Handler(player.applicationLooper).post {
            player.stop()
            player.clearMediaItems()
        }
    }

    private fun ensureListeningToPlayedListTimeline() {
        if (playedListTimelineJob != null) return

        playedListTimelineJob = coroutineScope.launch {
            combine(
                playbackManager.playedList,
                playbackManager.state,
            ) { playedList, state ->
                val currentMusicId = (state as? PlaybackManagerState.Data)
                    ?.currentMusic
                    ?.musicId

                PlayedListTimelineData(
                    musics = if (currentMusicId == null) emptyList() else playedList,
                    currentMusicId = currentMusicId,
                )
            }
                .distinctUntilChanged { old, new ->
                    old.currentMusicId == new.currentMusicId &&
                        old.musics.map { it.musicId } == new.musics.map { it.musicId }
                }
                .collectLatest { timelineData ->
                    soulSearchingPlayer.syncPlayedListTimeline(
                        musics = timelineData.musics,
                        currentMusicId = timelineData.currentMusicId,
                    )
                }
        }
    }

    /**
     * Update session metadata with information for the current played song.
     */
    private fun updateMetadata(updateData: UpdateData) {
        coroutineScope.launch {
            val metadata = mediaMetadataUtils
                .fromMusic(
                    music = updateData.music,
                    cover = updateData.cover?.asAndroidBitmap(),
                )
                .setTrackNumber(updateData.position.toInt())
                .setTotalTrackCount(updateData.playedListSize.toInt())
                .build()

            soulSearchingPlayer.playerDispatcher.handler.post {
                val currentMediaItem = soulSearchingPlayer.player.currentMediaItem ?: return@post
                val currentIndex = soulSearchingPlayer.player.currentMediaItemIndex

                if (currentIndex == C.INDEX_UNSET) return@post
                if (currentMediaItem.mediaMetadata.hasSameQueueVisibleContentAs(metadata)) return@post

                soulSearchingPlayer.player.replaceMediaItem(
                    currentIndex,
                    currentMediaItem.buildUpon()
                        .setMediaMetadata(metadata)
                        .build()
                )
            }
        }
    }

    private fun MediaMetadata.hasSameQueueVisibleContentAs(
        other: MediaMetadata,
    ): Boolean =
        title == other.title &&
            displayTitle == other.displayTitle &&
            artist == other.artist &&
            albumTitle == other.albumTitle &&
            albumArtist == other.albumArtist &&
            durationMs == other.durationMs &&
            trackNumber == other.trackNumber &&
            totalTrackCount == other.totalTrackCount &&
            isBrowsable == other.isBrowsable &&
            isPlayable == other.isPlayable &&
            mediaType == other.mediaType &&
            artworkData.contentEqualsNullable(other.artworkData)

    private fun ByteArray?.contentEqualsNullable(other: ByteArray?): Boolean =
        when {
            this === other -> true
            this == null || other == null -> false
            else -> contentEquals(other)
        }

    private fun updateAvailableCommands(session: MediaSession) {
        val sessionCommands = buildSessionCommands(session)
        val playerCommands = buildPlayerCommands()
        val mediaButtonPreferences = buildMediaButtonPreferences()

        session.setMediaButtonPreferences(mediaButtonPreferences)
        session.connectedControllers.forEach { controller ->
            session.setAvailableCommands(
                controller,
                sessionCommands,
                playerCommands,
            )
            session.setMediaButtonPreferences(
                controller,
                mediaButtonPreferences,
            )
        }
    }

    private fun buildSessionCommands(session: MediaSession): SessionCommands =
        if (session is MediaLibrarySession) {
            MediaSession.ConnectionResult.DEFAULT_SESSION_AND_LIBRARY_COMMANDS
        } else {
            MediaSession.ConnectionResult.DEFAULT_SESSION_COMMANDS
        }.buildUpon()
            .apply {
                if (isFavoriteActionAvailable) {
                    add(favoriteCommand)
                }
            }
            .build()

    private fun buildPlayerCommands(): Player.Commands =
        Player.Commands.Builder()
            .apply {
                add(Player.COMMAND_GET_CURRENT_MEDIA_ITEM)
                add(Player.COMMAND_GET_TIMELINE)
                add(Player.COMMAND_GET_METADATA)
                add(Player.COMMAND_GET_AUDIO_ATTRIBUTES)
                add(Player.COMMAND_GET_VOLUME)
                add(Player.COMMAND_GET_DEVICE_VOLUME)
                add(Player.COMMAND_GET_TRACKS)
                add(Player.COMMAND_SET_MEDIA_ITEM)
                add(Player.COMMAND_PREPARE)

                if (currentPlayedListScope?.isAdmin == true) {
                    add(Player.COMMAND_PLAY_PAUSE)
                    add(Player.COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM)
                    add(Player.COMMAND_SEEK_TO_PREVIOUS)
                    add(Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM)
                    add(Player.COMMAND_SEEK_TO_NEXT)
                    add(Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM)
                    add(Player.COMMAND_SEEK_TO_MEDIA_ITEM)
                    add(Player.COMMAND_SEEK_TO_DEFAULT_POSITION)
                }
            }
            .build()

    private fun buildMediaButtonPreferences(): List<CommandButton> =
        buildList {
            if (currentPlayedListScope?.isAdmin == true) {
                add(
                    CommandButton.Builder(CommandButton.ICON_PREVIOUS)
                        .setDisplayName("Previous")
                        .setPlayerCommand(Player.COMMAND_SEEK_TO_PREVIOUS)
                        .build()
                )
                add(
                    CommandButton.Builder(CommandButton.ICON_PLAY)
                        .setDisplayName("Play/Pause")
                        .setPlayerCommand(Player.COMMAND_PLAY_PAUSE)
                        .build()
                )
                add(
                    CommandButton.Builder(CommandButton.ICON_NEXT)
                        .setDisplayName("Next")
                        .setPlayerCommand(Player.COMMAND_SEEK_TO_NEXT)
                        .build()
                )
            }

            if (isFavoriteActionAvailable) {
                add(
                    CommandButton.Builder(
                        if (isCurrentMusicInFavorite) {
                            CommandButton.ICON_HEART_FILLED
                        } else {
                            CommandButton.ICON_HEART_UNFILLED
                        }
                    )
                        .setDisplayName(
                            if (isCurrentMusicInFavorite) {
                                "Remove from favorites"
                            } else {
                                "Add to favorites"
                            }
                        )
                        .setSessionCommand(favoriteCommand)
                        .build()
                )
            }
        }

    private companion object {
        private const val FAVORITE_ACTION: String = "FAVORITE_ACTION"
        private const val MAIN_ACTIVITY_CLASS_NAME: String =
            "com.github.enteraname74.soulsearching.MainActivity"
    }
}

@OptIn(UnstableApi::class)
private class SoulSearchingSessionPlayer(
    player: Player,
    private val playbackManager: () -> PlaybackManager,
    private val coroutineScope: CoroutineScope,
    private val canControl: () -> Boolean,
) : ForwardingPlayer(player) {
    private var shouldIgnoreNextPrepare: Boolean = false

    override fun setMediaItems(mediaItems: List<MediaItem>) {
        if (mediaItems.isFromAndroidAutoLibrary()) {
            shouldIgnoreNextPrepare = true
        } else {
            super.setMediaItems(mediaItems)
        }
    }

    override fun setMediaItems(mediaItems: List<MediaItem>, resetPosition: Boolean) {
        if (mediaItems.isFromAndroidAutoLibrary()) {
            shouldIgnoreNextPrepare = true
        } else {
            super.setMediaItems(mediaItems, resetPosition)
        }
    }

    override fun setMediaItems(
        mediaItems: List<MediaItem>,
        startIndex: Int,
        startPositionMs: Long,
    ) {
        if (mediaItems.isFromAndroidAutoLibrary()) {
            shouldIgnoreNextPrepare = true
        } else {
            super.setMediaItems(mediaItems, startIndex, startPositionMs)
        }
    }

    override fun setMediaItem(mediaItem: MediaItem) {
        if (mediaItem.isFromAndroidAutoLibrary()) {
            shouldIgnoreNextPrepare = true
        } else {
            super.setMediaItem(mediaItem)
        }
    }

    override fun setMediaItem(mediaItem: MediaItem, startPositionMs: Long) {
        if (mediaItem.isFromAndroidAutoLibrary()) {
            shouldIgnoreNextPrepare = true
        } else {
            super.setMediaItem(mediaItem, startPositionMs)
        }
    }

    override fun setMediaItem(mediaItem: MediaItem, resetPosition: Boolean) {
        if (mediaItem.isFromAndroidAutoLibrary()) {
            shouldIgnoreNextPrepare = true
        } else {
            super.setMediaItem(mediaItem, resetPosition)
        }
    }

    override fun prepare() {
        if (shouldIgnoreNextPrepare) {
            shouldIgnoreNextPrepare = false
        } else {
            super.prepare()
        }
    }

    override fun getAvailableCommands(): Player.Commands =
        super.getAvailableCommands()
            .buildUpon()
            .apply {
                if (canControl()) {
                    add(COMMAND_PLAY_PAUSE)
                    add(COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM)
                    add(COMMAND_SEEK_TO_PREVIOUS)
                    add(COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM)
                    add(COMMAND_SEEK_TO_NEXT)
                    add(COMMAND_SEEK_TO_NEXT_MEDIA_ITEM)
                    add(COMMAND_SEEK_TO_MEDIA_ITEM)
                    add(COMMAND_SEEK_TO_DEFAULT_POSITION)
                } else {
                    remove(COMMAND_PLAY_PAUSE)
                    remove(COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM)
                    remove(COMMAND_SEEK_TO_PREVIOUS)
                    remove(COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM)
                    remove(COMMAND_SEEK_TO_NEXT)
                    remove(COMMAND_SEEK_TO_NEXT_MEDIA_ITEM)
                    remove(COMMAND_SEEK_TO_MEDIA_ITEM)
                    remove(COMMAND_SEEK_TO_DEFAULT_POSITION)
                }
            }
            .build()

    override fun isCommandAvailable(command: Int): Boolean =
        when (command) {
            COMMAND_PLAY_PAUSE,
            COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM,
            COMMAND_SEEK_TO_PREVIOUS,
            COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM,
            COMMAND_SEEK_TO_NEXT,
            COMMAND_SEEK_TO_NEXT_MEDIA_ITEM,
            COMMAND_SEEK_TO_MEDIA_ITEM,
            COMMAND_SEEK_TO_DEFAULT_POSITION -> canControl()

            else -> super.isCommandAvailable(command)
        }

    override fun play() {
        if (canControl()) {
            playbackManager().play()
        }
    }

    override fun pause() {
        if (canControl()) {
            playbackManager().pause()
        }
    }

    override fun seekTo(positionMs: Long) {
        if (canControl()) {
            coroutineScope.launch {
                playbackManager().seekToPosition(positionMs.toInt())
            }
        }
    }

    override fun seekTo(mediaItemIndex: Int, positionMs: Long) {
        selectMediaItem(mediaItemIndex, positionMs)
    }

    override fun seekToDefaultPosition() {
        if (!canControl()) return

        coroutineScope.launch {
            playbackManager().seekToPosition(0)
        }
    }

    override fun seekToDefaultPosition(mediaItemIndex: Int) {
        selectMediaItem(mediaItemIndex, 0L)
    }

    private fun selectMediaItem(mediaItemIndex: Int, positionMs: Long) {
        if (!canControl()) return

        if (mediaItemIndex == currentMediaItemIndex) {
            if (positionMs > 0) {
                seekTo(positionMs)
            }
            return
        }

        val musicId = getMediaItemAtOrNull(mediaItemIndex)?.musicId() ?: return
        super.seekTo(mediaItemIndex, positionMs)
        coroutineScope.launch {
            val music = playbackManager()
                .playedList
                .firstOrNull()
                ?.firstOrNull { it.musicId == musicId }
                ?: return@launch

            playbackManager().setAndPlayMusicFromCurrentPlayedList(music)
            if (positionMs > 0) {
                playbackManager().seekToPosition(positionMs.toInt())
            }
        }
    }

    override fun seekToPrevious() {
        if (canControl()) {
            coroutineScope.launch {
                playbackManager().previous()
            }
        }
    }

    override fun seekToPreviousMediaItem() {
        seekToPrevious()
    }

    override fun seekToNext() {
        if (canControl()) {
            coroutineScope.launch {
                playbackManager().next()
            }
        }
    }

    override fun seekToNextMediaItem() {
        seekToNext()
    }

    private fun List<MediaItem>.isFromAndroidAutoLibrary(): Boolean =
        any { it.isFromAndroidAutoLibrary() }

    private fun MediaItem.isFromAndroidAutoLibrary(): Boolean =
        AndroidAutoMediaIdsUtils.musicRequestFrom(mediaId) != null

    private fun getMediaItemAtOrNull(index: Int): MediaItem? =
        if (index in 0 until mediaItemCount) {
            getMediaItemAt(index)
        } else {
            null
        }

    private fun MediaItem.musicId(): Uuid? =
        runCatching { Uuid.parse(mediaId) }.getOrNull()
}

private data class PlayedListTimelineData(
    val musics: List<Music>,
    val currentMusicId: Uuid?,
)
