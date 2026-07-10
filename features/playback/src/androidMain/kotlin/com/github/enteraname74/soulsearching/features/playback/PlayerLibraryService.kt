package com.github.enteraname74.soulsearching.features.playback

import android.os.Bundle
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.usecase.music.CommonMusicUseCase
import com.github.enteraname74.domain.util.WorkDispatcher
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import com.github.enteraname74.soulsearching.features.playback.mediasession.AndroidAutoMediaIds
import com.github.enteraname74.soulsearching.features.playback.mediasession.MediaSessionManager
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.guava.future
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(UnstableApi::class)
class PlayerLibraryService : MediaLibraryService(), KoinComponent {
    private val commonMusicUseCase: CommonMusicUseCase by inject()
    private val mediaSessionManager: MediaSessionManager by inject()
    private val playbackManager: PlaybackManager by inject()
    private val workDispatcher: WorkDispatcher by inject()

    private val serviceScope: CoroutineScope by lazy {
        CoroutineScope(SupervisorJob() + workDispatcher.dispatcher)
    }

    private var mediaLibrarySession: MediaLibrarySession? = null
    private val callback: MediaLibrarySession.Callback =
        object : MediaLibrarySession.Callback {
            override fun onConnect(
                session: MediaSession,
                controller: MediaSession.ControllerInfo,
            ): MediaSession.ConnectionResult =
                mediaSessionManager.onConnect(session, controller)

            override fun onCustomCommand(
                session: MediaSession,
                controller: MediaSession.ControllerInfo,
                customCommand: SessionCommand,
                args: Bundle,
            ): ListenableFuture<SessionResult> =
                mediaSessionManager.onCustomCommand(customCommand)

            override fun onGetLibraryRoot(
                session: MediaLibrarySession,
                browser: MediaSession.ControllerInfo,
                params: LibraryParams?,
            ): ListenableFuture<LibraryResult<MediaItem>> =
                Futures.immediateFuture(
                    LibraryResult.ofItem(rootItem(), params)
                )

            override fun onGetItem(
                session: MediaLibrarySession,
                browser: MediaSession.ControllerInfo,
                mediaId: String,
            ): ListenableFuture<LibraryResult<MediaItem>> =
                serviceScope.future {
                    when (mediaId) {
                        AndroidAutoMediaIds.ROOT -> LibraryResult.ofItem(rootItem(), null)
                        AndroidAutoMediaIds.ALL_SONGS -> LibraryResult.ofItem(allSongsItem(), null)
                        else -> getMusicFromMediaId(mediaId)
                            ?.let { LibraryResult.ofItem(it.toMediaItem(), null) }
                            ?: LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE)
                    }
                }

            override fun onGetChildren(
                session: MediaLibrarySession,
                browser: MediaSession.ControllerInfo,
                parentId: String,
                page: Int,
                pageSize: Int,
                params: LibraryParams?,
            ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> =
                serviceScope.future {
                    val children = when (parentId) {
                        AndroidAutoMediaIds.ROOT -> listOf(allSongsItem())
                        AndroidAutoMediaIds.ALL_SONGS -> commonMusicUseCase
                            .getAllSorted()
                            .map { it.toMediaItem() }
                        else -> emptyList()
                    }.page(page, pageSize)

                    LibraryResult.ofItemList(children, params)
                }

            override fun onSetMediaItems(
                mediaSession: MediaSession,
                controller: MediaSession.ControllerInfo,
                mediaItems: List<MediaItem>,
                startIndex: Int,
                startPositionMs: Long,
            ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> {
                val selectedMediaItem = mediaItems.getOrNull(
                    startIndex.takeIf { it != C.INDEX_UNSET } ?: 0
                )
                val selectedMusicId = selectedMediaItem
                    ?.mediaId
                    ?.let(AndroidAutoMediaIds::musicIdFrom)
                    ?: return super.onSetMediaItems(
                        mediaSession,
                        controller,
                        mediaItems,
                        startIndex,
                        startPositionMs,
                    )

                return serviceScope.future {
                    val allMusics = commonMusicUseCase.getAllSorted()
                    val selectedMusic = allMusics.firstOrNull { it.musicId == selectedMusicId }
                        ?: throw IllegalArgumentException("Unknown music id: $selectedMusicId")

                    playbackManager.setCurrentPlaylistAndMusic(
                        music = selectedMusic,
                        musicList = allMusics,
                        playlistId = null,
                        isMainPlaylist = true,
                        isForcingNewPlaylist = true,
                    )

                    MediaSession.MediaItemsWithStartPosition(
                        listOf(selectedMusic.toMediaItem()),
                        0,
                        startPositionMs,
                    )
                }
            }
        }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? =
        mediaLibrarySession

    override fun onCreate() {
        super.onCreate()
        mediaLibrarySession = mediaSessionManager.getOrCreateMediaLibrarySession(callback)
    }

    override fun onDestroy() {
        mediaSessionManager.release()
        mediaLibrarySession = null
        serviceScope.cancel()
        super.onDestroy()
    }

    private suspend fun getMusicFromMediaId(mediaId: String): Music? {
        val musicId = AndroidAutoMediaIds.musicIdFrom(mediaId) ?: return null
        return commonMusicUseCase
            .getAllSorted()
            .firstOrNull { it.musicId == musicId }
    }

    private fun rootItem(): MediaItem =
        MediaItem.Builder()
            .setMediaId(AndroidAutoMediaIds.ROOT)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle("Soul Searching")
                    .setIsBrowsable(true)
                    .setIsPlayable(false)
                    .setMediaType(MediaMetadata.MEDIA_TYPE_FOLDER_MIXED)
                    .build()
            )
            .build()

    private fun allSongsItem(): MediaItem =
        MediaItem.Builder()
            .setMediaId(AndroidAutoMediaIds.ALL_SONGS)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle("All songs")
                    .setIsBrowsable(true)
                    .setIsPlayable(false)
                    .setMediaType(MediaMetadata.MEDIA_TYPE_FOLDER_MIXED)
                    .build()
            )
            .build()

    private fun Music.toMediaItem(): MediaItem =
        MediaItem.Builder()
            .setMediaId(AndroidAutoMediaIds.forMusic(musicId))
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(name)
                    .setDisplayTitle(name)
                    .setArtist(artistsNames)
                    .setAlbumTitle(album.albumName)
                    .setAlbumArtist(album.artist.artistName)
                    .setTrackNumber(albumPosition)
                    .setDurationMs(duration)
                    .setIsBrowsable(false)
                    .setIsPlayable(true)
                    .setMediaType(MediaMetadata.MEDIA_TYPE_MUSIC)
                    .build()
            )
            .build()

    private fun List<MediaItem>.page(page: Int, pageSize: Int): List<MediaItem> {
        if (pageSize <= 0) return this

        val fromIndex = page * pageSize
        if (fromIndex >= size) return emptyList()

        val toIndex = minOf(fromIndex + pageSize, size)
        return subList(fromIndex, toIndex)
    }
}
