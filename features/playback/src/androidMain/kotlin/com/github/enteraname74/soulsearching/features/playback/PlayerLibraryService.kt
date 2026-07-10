package com.github.enteraname74.soulsearching.features.playback

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionError
import androidx.media3.session.SessionResult
import com.github.enteraname74.domain.model.AlbumPreview
import com.github.enteraname74.domain.model.ArtistPreview
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.MusicFolderPreview
import com.github.enteraname74.domain.model.PlaylistPreview
import com.github.enteraname74.domain.usecase.album.CommonAlbumUseCase
import com.github.enteraname74.domain.usecase.artist.CommonArtistUseCase
import com.github.enteraname74.domain.usecase.music.CommonMusicUseCase
import com.github.enteraname74.domain.usecase.playlist.CommonPlaylistUseCase
import com.github.enteraname74.domain.util.WorkDispatcher
import com.github.enteraname74.soulsearching.coreui.strings.StringsUtils
import com.github.enteraname74.soulsearching.features.filemanager.cover.CoverFileManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import com.github.enteraname74.soulsearching.features.playback.mediasession.AndroidAutoMediaIds
import com.github.enteraname74.soulsearching.features.playback.mediasession.AndroidAutoMediaIdsUtils
import com.github.enteraname74.soulsearching.features.playback.mediasession.MediaMetadataUtils
import com.github.enteraname74.soulsearching.features.playback.mediasession.MediaSessionManager
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.guava.future
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.firstOrNull
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.lang.ref.WeakReference

@OptIn(UnstableApi::class)
class PlayerLibraryService : MediaLibraryService(), KoinComponent {
    private val commonAlbumUseCase: CommonAlbumUseCase by inject()
    private val commonArtistUseCase: CommonArtistUseCase by inject()
    private val commonMusicUseCase: CommonMusicUseCase by inject()
    private val commonPlaylistUseCase: CommonPlaylistUseCase by inject()
    private val mediaSessionManager: MediaSessionManager by inject()
    private val playbackManager: PlaybackManager by inject()
    private val workDispatcher: WorkDispatcher by inject()
    private val mediaMetadataUtils: MediaMetadataUtils by inject()
    private val coverFileManager: CoverFileManager by inject()

    private val strings by lazy { StringsUtils.getStrings(applicationContext) }
    private val allSongsItem: MediaItem by lazy {
        AndroidAutoMediaIds.AllSongs.browsableItem(
            title = strings.musics,
            mediaType = MediaMetadata.MEDIA_TYPE_FOLDER_MIXED,
        )
    }
    private val allAlbumsItem: MediaItem by lazy {
        AndroidAutoMediaIds.AllAlbums.browsableItem(
            title = strings.albums,
            mediaType = MediaMetadata.MEDIA_TYPE_FOLDER_ALBUMS,
        )
    }
    private val allArtistsItem: MediaItem by lazy {
        AndroidAutoMediaIds.AllArtists.browsableItem(
            title = strings.artists,
            mediaType = MediaMetadata.MEDIA_TYPE_FOLDER_ARTISTS,
        )
    }
    private val allPlaylistsItem: MediaItem by lazy {
        AndroidAutoMediaIds.AllPlaylists.browsableItem(
            title = strings.playlists,
            mediaType = MediaMetadata.MEDIA_TYPE_FOLDER_PLAYLISTS,
        )
    }
    private val allFoldersItem: MediaItem by lazy {
        AndroidAutoMediaIds.AllFolders.browsableItem(
            title = strings.folders,
            mediaType = MediaMetadata.MEDIA_TYPE_FOLDER_MIXED,
        )
    }
    private val rootItem: MediaItem by lazy {
        AndroidAutoMediaIds.Root.browsableItem(
            title = "Soul Searching",
            mediaType = MediaMetadata.MEDIA_TYPE_FOLDER_MIXED,
        )
    }
    private val rootChildren: List<MediaItem>
        get() = listOf(
            allSongsItem,
            allAlbumsItem,
            allArtistsItem,
            allPlaylistsItem,
            allFoldersItem,
        )

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
                mediaSessionManager.onConnect(session)

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
            ): ListenableFuture<LibraryResult<MediaItem>> {
                return Futures.immediateFuture(
                    LibraryResult.ofItem(
                        AndroidAutoMediaIds.Root.browsableItem(
                            title = "Soul Searching",
                            mediaType = MediaMetadata.MEDIA_TYPE_FOLDER_MIXED,
                        ), params
                    )
                )
            }

            override fun onGetItem(
                session: MediaLibrarySession,
                browser: MediaSession.ControllerInfo,
                mediaId: String,
            ): ListenableFuture<LibraryResult<MediaItem>> =
                serviceScope.future {
                    when (AndroidAutoMediaIds.fromString(mediaId)) {
                        AndroidAutoMediaIds.Root -> LibraryResult.ofItem(rootItem, null)
                        AndroidAutoMediaIds.AllSongs -> LibraryResult.ofItem(allSongsItem, null)
                        AndroidAutoMediaIds.AllAlbums -> LibraryResult.ofItem(allAlbumsItem, null)
                        AndroidAutoMediaIds.AllArtists -> LibraryResult.ofItem(allArtistsItem, null)
                        AndroidAutoMediaIds.AllPlaylists -> LibraryResult.ofItem(allPlaylistsItem, null)
                        AndroidAutoMediaIds.AllFolders -> LibraryResult.ofItem(allFoldersItem, null)
                        else -> getMediaItemFromMediaId(mediaId)
                            ?.let { LibraryResult.ofItem(it, null) }
                            ?: LibraryResult.ofError<MediaItem>(
                                SessionError.ERROR_BAD_VALUE
                            )
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
                    val children = when (AndroidAutoMediaIds.fromString(parentId)) {
                        AndroidAutoMediaIds.Root -> rootChildren.page(page, pageSize)
                        AndroidAutoMediaIds.AllSongs -> commonMusicUseCase
                            .getAllSorted(
                                page = page,
                                pageSize = pageSize,
                            )
                            .map { it.toMediaItem() }
                        AndroidAutoMediaIds.AllAlbums -> commonAlbumUseCase
                            .getAll(
                                page = page,
                                pageSize = pageSize,
                            )
                            .map { it.toMediaItem() }
                        AndroidAutoMediaIds.AllArtists -> commonArtistUseCase
                            .getAll(
                                page = page,
                                pageSize = pageSize,
                            )
                            .map { it.toMediaItem() }
                        AndroidAutoMediaIds.AllPlaylists -> commonPlaylistUseCase
                            .getAll(
                                page = page,
                                pageSize = pageSize,
                            )
                            .map { it.toMediaItem() }
                        AndroidAutoMediaIds.AllFolders -> commonMusicUseCase
                            .getAllMusicFolders(
                                page = page,
                                pageSize = pageSize,
                            )
                            .map { it.toMediaItem() }
                        null -> getChildrenFromMediaId(
                            mediaId = parentId,
                            page = page,
                            pageSize = pageSize,
                        )
                    }

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
                    ?.let(AndroidAutoMediaIdsUtils::musicIdFrom)
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
        activeService = WeakReference(this)
        setMediaNotificationProvider(createNotificationProvider())
        mediaLibrarySession = mediaSessionManager.getOrCreateMediaLibrarySession(callback)
    }

    override fun onDestroy() {
        if (activeService?.get() === this) {
            activeService = null
        }
        mediaSessionManager.release()
        mediaLibrarySession = null
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        serviceScope.launch {
            playbackManager.stopPlayback(resetPlayedList = false)
            withContext(Dispatchers.Main) {
                removeForegroundNotification()
                stopSelf()
            }
        }
    }

    private fun removeForegroundNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
    }

    private fun createNotificationProvider(): DefaultMediaNotificationProvider =
        DefaultMediaNotificationProvider.Builder(this)
            .setNotificationId(MEDIA_NOTIFICATION_ID)
            .setChannelId(MUSIC_NOTIFICATION_CHANNEL_ID)
            .build()
            .also { provider ->
                provider.setSmallIcon(R.drawable.app_logo_uni_xml)
            }

    private suspend fun getMusicFromMediaId(mediaId: String): Music? {
        val musicId = AndroidAutoMediaIdsUtils.musicIdFrom(mediaId) ?: return null
        return commonMusicUseCase.getFromId(musicId).firstOrNull()
    }

    private suspend fun getMediaItemFromMediaId(mediaId: String): MediaItem? =
        getMusicFromMediaId(mediaId)?.toMediaItem()
            ?: getAlbumFromMediaId(mediaId)?.toMediaItem()
            ?: getArtistFromMediaId(mediaId)?.toMediaItem()
            ?: getPlaylistFromMediaId(mediaId)?.toMediaItem()
            ?: getFolderFromMediaId(mediaId)?.toMediaItem()

    private suspend fun getAlbumFromMediaId(mediaId: String): AlbumPreview? {
        val albumId = AndroidAutoMediaIdsUtils.albumIdFrom(mediaId) ?: return null
        return commonAlbumUseCase.getAlbumPreview(albumId).firstOrNull()
    }

    private suspend fun getArtistFromMediaId(mediaId: String): ArtistPreview? {
        val artistId = AndroidAutoMediaIdsUtils.artistIdFrom(mediaId) ?: return null
        return commonArtistUseCase.getArtistPreview(artistId).firstOrNull()
    }

    private suspend fun getPlaylistFromMediaId(mediaId: String): PlaylistPreview? {
        val playlistId = AndroidAutoMediaIdsUtils.playlistIdFrom(mediaId) ?: return null
        return commonPlaylistUseCase.getPlaylistPreview(playlistId).firstOrNull()
    }

    private suspend fun getFolderFromMediaId(mediaId: String): MusicFolderPreview? {
        val folder = AndroidAutoMediaIdsUtils.folderFrom(mediaId) ?: return null
        return commonMusicUseCase.getMusicFolderPreview(folder).firstOrNull()
    }

    private suspend fun getChildrenFromMediaId(
        mediaId: String,
        page: Int,
        pageSize: Int,
    ): List<MediaItem> {
        val musics = AndroidAutoMediaIdsUtils.albumIdFrom(mediaId)
            ?.let { albumId ->
                commonMusicUseCase.getAllMusicFromAlbum(
                    albumId = albumId,
                    page = page,
                    pageSize = pageSize,
                )
            }
            ?: AndroidAutoMediaIdsUtils.artistIdFrom(mediaId)
                ?.let { artistId ->
                    commonMusicUseCase.getAllMusicFromArtist(
                        artistId = artistId,
                        page = page,
                        pageSize = pageSize,
                    )
                }
            ?: AndroidAutoMediaIdsUtils.playlistIdFrom(mediaId)
                ?.let { playlistId ->
                    commonMusicUseCase.getAllMusicFromPlaylist(
                        playlistId = playlistId,
                        page = page,
                        pageSize = pageSize,
                    )
                }
            ?: AndroidAutoMediaIdsUtils.folderFrom(mediaId)
                ?.let { folder ->
                    commonMusicUseCase.getAllMusicFromFolder(
                        folder = folder,
                        page = page,
                        pageSize = pageSize,
                    )
                }
            ?: emptyList()

        return musics.map { it.toMediaItem() }
    }

    private fun AndroidAutoMediaIds.browsableItem(
        title: String,
        mediaType: Int,
    ): MediaItem =
        MediaItem.Builder()
            .setMediaId(value)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(title)
                    .setIsBrowsable(true)
                    .setIsPlayable(false)
                    .setMediaType(mediaType)
                    .build()
            )
            .build()

    private fun Music.toMediaItem(): MediaItem =
        MediaItem.Builder()
            .setMediaId(AndroidAutoMediaIdsUtils.forMusic(musicId))
            .setMediaMetadata(
                mediaMetadataUtils
                    .fromMusic(
                        music = this,
                        // Should be set from PlaybackManager, but maybe not in the case of Android Auto?
                        cover = null,
                    ).build()
            )
            .build()

    private fun AlbumPreview.toMediaItem(): MediaItem =
        MediaItem.Builder()
            .setMediaId(AndroidAutoMediaIdsUtils.forAlbum(id))
            .setMediaMetadata(
                mediaMetadataUtils
                    .fromAlbum(
                        album = this,
                        cover = null,
                    ).build()
            )
            .build()

    private fun ArtistPreview.toMediaItem(): MediaItem =
        MediaItem.Builder()
            .setMediaId(AndroidAutoMediaIdsUtils.forArtist(id))
            .setMediaMetadata(
                mediaMetadataUtils
                    .fromArtist(
                        artist = this,
                        cover = null,
                    ).build()
            )
            .build()

    private fun PlaylistPreview.toMediaItem(): MediaItem =
        MediaItem.Builder()
            .setMediaId(AndroidAutoMediaIdsUtils.forPlaylist(id))
            .setMediaMetadata(
                mediaMetadataUtils
                    .fromPlaylist(
                        playlist = this,
                        cover = null,
                    ).build()
            )
            .build()

    private fun MusicFolderPreview.toMediaItem(): MediaItem =
        MediaItem.Builder()
            .setMediaId(AndroidAutoMediaIdsUtils.forFolder(folder))
            .setMediaMetadata(
                mediaMetadataUtils
                    .fromFolder(
                        folder = this,
                        cover = null,
                    ).build()
            )
            .build()

    private fun List<MediaItem>.page(page: Int, pageSize: Int): List<MediaItem> {
        if (pageSize <= 0) return this

        val fromIndex = page * pageSize
        if (fromIndex >= size) return emptyList()

        val toIndex = minOf(fromIndex + pageSize, size)
        return subList(fromIndex, toIndex)
    }

    companion object {
        private const val MEDIA_NOTIFICATION_ID = 69
        private const val MUSIC_NOTIFICATION_CHANNEL_ID = "SoulSearchingMusicNotificationChannel"

        private var activeService: WeakReference<PlayerLibraryService>? = null

        fun triggerActiveNotificationUpdate() {
            activeService?.get()?.triggerNotificationUpdate()
        }
    }
}
