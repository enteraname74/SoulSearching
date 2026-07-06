package com.github.enteraname74.soulsearching.features.playback.mediasession

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.core.graphics.scale
import androidx.media3.common.C
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionError
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionCommands
import androidx.media3.session.SessionResult
import com.github.enteraname74.domain.model.Scope
import com.github.enteraname74.domain.model.player.PlayedListScope
import com.github.enteraname74.domain.usecase.music.ToggleMusicFavoriteStatusUseCase
import com.github.enteraname74.domain.util.WorkDispatcher
import com.github.enteraname74.soulsearching.features.playback.R
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import com.github.enteraname74.soulsearching.features.playback.model.UpdateData
import com.github.enteraname74.soulsearching.features.playback.player.SoulSearchingExoPlayerImpl
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

/**
 * Manage Media3 session state exposed to system UI, headset controls and external controllers.
 */
@OptIn(UnstableApi::class)
class MediaSessionManager(
    private val context: Context,
    private val playbackManager: PlaybackManager,
    private val toggleMusicFavoriteStatusUseCase: ToggleMusicFavoriteStatusUseCase,
    private val soulSearchingPlayer: SoulSearchingExoPlayerImpl,
    workDispatcher: WorkDispatcher,
) {
    private var mediaSession: MediaSession? = null
    private var currentPlayedListScope: PlayedListScope? = null
    private var isFavoriteActionAvailable: Boolean = false

    private val coroutineScope = CoroutineScope(workDispatcher.dispatcher)
    private val favoriteCommand = SessionCommand(FAVORITE_ACTION, Bundle.EMPTY)

    private val sessionPlayer: Player by lazy {
        SoulSearchingSessionPlayer(
            player = soulSearchingPlayer.media3Player,
            playbackManager = playbackManager,
            coroutineScope = coroutineScope,
            canControl = { currentPlayedListScope?.isAdmin == true },
        )
    }

    private val standardNotificationBitmap: Bitmap =
        BitmapFactory.decodeResource(context.resources, R.drawable.new_notification_default)
            .scale(DEFAULT_NOTIFICATION_SIZE, DEFAULT_NOTIFICATION_SIZE, false)

    fun getUpdatedMediaSession(
        updateData: UpdateData,
    ): MediaSession {
        currentPlayedListScope = updateData.playedListScope
        isFavoriteActionAvailable = updateData.music.scope != Scope.SharedPlayedList

        val session = mediaSession ?: init().also {
            mediaSession = it
        }

        updateMetadata(updateData)
        updateAvailableCommands(session)
        return session
    }

    private fun init(): MediaSession =
        MediaSession.Builder(context, sessionPlayer)
            .setCallback(
                object : MediaSession.Callback {
                    override fun onConnect(
                        session: MediaSession,
                        controller: MediaSession.ControllerInfo,
                    ): MediaSession.ConnectionResult =
                        MediaSession.ConnectionResult.AcceptedResultBuilder(session)
                            .setAvailableSessionCommands(buildSessionCommands())
                            .setAvailablePlayerCommands(buildPlayerCommands())
                            .setMediaButtonPreferences(buildMediaButtonPreferences())
                            .build()

                    override fun onCustomCommand(
                        session: MediaSession,
                        controller: MediaSession.ControllerInfo,
                        customCommand: SessionCommand,
                        args: Bundle,
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
                }
            )
            .setMediaButtonPreferences(buildMediaButtonPreferences())
            .build()

    /**
     * Release all elements related to the media session.
     */
    fun release() {
        mediaSession?.release()
        mediaSession = null
    }

    /**
     * Update session metadata with information for the current played song.
     */
    private fun updateMetadata(updateData: UpdateData) {
        val metadata = MediaMetadata.Builder()
            .setArtworkData(
                (updateData.cover?.asAndroidBitmap() ?: standardNotificationBitmap).toPngBytes(),
                MediaMetadata.PICTURE_TYPE_FRONT_COVER,
            )
            .setDurationMs(updateData.music.duration)
            .setDisplayTitle(updateData.music.name)
            .setTitle(updateData.music.name)
            .setArtist(updateData.music.artistsNames)
            .setAlbumTitle(updateData.music.album.albumName)
            .setAlbumArtist(updateData.music.album.artist.artistName)
            .setTrackNumber(updateData.position.toInt())
            .setTotalTrackCount(updateData.playedListSize.toInt())
            .build()

        val currentMediaItem = soulSearchingPlayer.media3Player.currentMediaItem ?: return
        val currentIndex = soulSearchingPlayer.media3Player.currentMediaItemIndex
        if (currentIndex == C.INDEX_UNSET) return

        soulSearchingPlayer.media3Player.replaceMediaItem(
            currentIndex,
            currentMediaItem.buildUpon()
                .setMediaMetadata(metadata)
                .build()
        )
    }

    private fun updateAvailableCommands(session: MediaSession) {
        val sessionCommands = buildSessionCommands()
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

    private fun buildSessionCommands(): SessionCommands =
        SessionCommands.Builder()
            .apply {
                if (isFavoriteActionAvailable) {
                    add(favoriteCommand)
                }
            }
            .build()

    private fun buildPlayerCommands(): Player.Commands =
        Player.Commands.Builder()
            .apply {
                if (currentPlayedListScope?.isAdmin == true) {
                    add(Player.COMMAND_PLAY_PAUSE)
                    add(Player.COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM)
                    add(Player.COMMAND_SEEK_TO_PREVIOUS)
                    add(Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM)
                    add(Player.COMMAND_SEEK_TO_NEXT)
                    add(Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM)
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
                    CommandButton.Builder(CommandButton.ICON_HEART_UNFILLED)
                        .setDisplayName("Favorite")
                        .setSessionCommand(favoriteCommand)
                        .build()
                )
            }
        }

    private fun Bitmap.toPngBytes(): ByteArray =
        ByteArrayOutputStream().use { outputStream ->
            compress(Bitmap.CompressFormat.PNG, BITMAP_COMPRESS_QUALITY, outputStream)
            outputStream.toByteArray()
        }

    private companion object {
        private const val DEFAULT_NOTIFICATION_SIZE: Int = 300
        private const val BITMAP_COMPRESS_QUALITY: Int = 100
        private const val FAVORITE_ACTION: String = "FAVORITE_ACTION"
    }
}

@OptIn(UnstableApi::class)
private class SoulSearchingSessionPlayer(
    player: Player,
    private val playbackManager: PlaybackManager,
    private val coroutineScope: CoroutineScope,
    private val canControl: () -> Boolean,
) : ForwardingPlayer(player) {

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
                } else {
                    remove(COMMAND_PLAY_PAUSE)
                    remove(COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM)
                    remove(COMMAND_SEEK_TO_PREVIOUS)
                    remove(COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM)
                    remove(COMMAND_SEEK_TO_NEXT)
                    remove(COMMAND_SEEK_TO_NEXT_MEDIA_ITEM)
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
            COMMAND_SEEK_TO_NEXT_MEDIA_ITEM -> canControl()

            else -> super.isCommandAvailable(command)
        }

    override fun play() {
        if (canControl()) {
            playbackManager.play()
        }
    }

    override fun pause() {
        if (canControl()) {
            playbackManager.pause()
        }
    }

    override fun seekTo(positionMs: Long) {
        if (canControl()) {
            coroutineScope.launch {
                playbackManager.seekToPosition(positionMs.toInt())
            }
        }
    }

    override fun seekTo(mediaItemIndex: Int, positionMs: Long) {
        seekTo(positionMs)
    }

    override fun seekToPrevious() {
        if (canControl()) {
            coroutineScope.launch {
                playbackManager.previous()
            }
        }
    }

    override fun seekToPreviousMediaItem() {
        seekToPrevious()
    }

    override fun seekToNext() {
        if (canControl()) {
            coroutineScope.launch {
                playbackManager.next()
            }
        }
    }

    override fun seekToNextMediaItem() {
        seekToNext()
    }
}
