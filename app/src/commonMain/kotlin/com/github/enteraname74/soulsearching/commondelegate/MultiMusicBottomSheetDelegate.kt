package com.github.enteraname74.soulsearching.commondelegate

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.usecase.music.CommonMusicUseCase
import com.github.enteraname74.domain.usecase.musicplaylist.CommonMusicPlaylistUseCase
import com.github.enteraname74.domain.usecase.playlist.CommonPlaylistUseCase
import com.github.enteraname74.soulsearching.composables.bottomsheets.multimusic.MultiMusicBottomSheet
import com.github.enteraname74.soulsearching.composables.dialog.DeleteMultiMusicDialog
import com.github.enteraname74.soulsearching.composables.dialog.RemoveMultiMusicFromPlaylistDialog
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.coreui.multiselection.MultiSelectionManagerImpl
import com.github.enteraname74.soulsearching.domain.model.types.MusicBottomSheetMode
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.UUID

interface MultiMusicBottomSheetDelegate {
    fun showMultiMusicBottomSheet(
        currentPlaylist: Playlist? = null
    )
}

class MultiMusicBottomSheetDelegateImpl(
    commonPlaylistUseCase: CommonPlaylistUseCase,
    private val commonMusicUseCase: CommonMusicUseCase,
    private val commonMusicPlaylistUseCase: CommonMusicPlaylistUseCase,
    private val loadingManager: LoadingManager,
    private val playbackManager: PlaybackManager,
): MultiMusicBottomSheetDelegate {
    private var setDialogState: (SoulDialog?) -> Unit = {}
    private var setBottomSheetState: (SoulBottomSheet?) -> Unit = {}
    private var musicBottomSheetMode: MusicBottomSheetMode = MusicBottomSheetMode.NORMAL
    private var multiSelectionManagerImpl: MultiSelectionManagerImpl? = null

    fun initDelegate(
        setDialogState: (SoulDialog?) -> Unit,
        setBottomSheetState: (SoulBottomSheet?) -> Unit,
        musicBottomSheetMode: MusicBottomSheetMode = MusicBottomSheetMode.NORMAL,
        multiSelectionManagerImpl: MultiSelectionManagerImpl,
    ) {
        this.setDialogState = setDialogState
        this.setBottomSheetState = setBottomSheetState
        this.musicBottomSheetMode = musicBottomSheetMode
        this.multiSelectionManagerImpl = multiSelectionManagerImpl
    }

    private fun showDeleteMultiMusicDialog(
        selectedIdsToDelete: List<UUID>,
    ) {
        setDialogState(
            DeleteMultiMusicDialog(
                onDelete = {
                    CoroutineScope(Dispatchers.IO).launch {
                        loadingManager.withLoading {
                            commonMusicUseCase.deleteAll(selectedIdsToDelete)
                            playbackManager.removeSongsFromPlayedPlaylist(selectedIdsToDelete)

                            setDialogState(null)
                            // We make sure to close the bottom sheet after removing the selected musics.
                            setBottomSheetState(null)
                            multiSelectionManagerImpl?.clearMultiSelection()
                        }
                    }
                },
                onClose = { setDialogState(null) },
            )
        )
    }

    private fun removeMusicFromPlaylistDialog(
        musicIdsToRemove: List<UUID>,
        currentPlaylist: Playlist,
    ) {
        setDialogState(
            RemoveMultiMusicFromPlaylistDialog(
                onConfirm = {
                    CoroutineScope(Dispatchers.IO).launch {
                        musicIdsToRemove.forEach { musicId ->
                            commonMusicPlaylistUseCase.delete(
                                musicId = musicId,
                                playlistId = currentPlaylist.playlistId,
                            )
                        }
                    }
                    multiSelectionManagerImpl?.clearMultiSelection()
                    setDialogState(null)
                    // We make sure to close the bottom sheet after removing the selected music from the playlist.
                    setBottomSheetState(null)
                },
                onClose = { setDialogState(null) }
            )
        )
    }

    override fun showMultiMusicBottomSheet(
        currentPlaylist: Playlist?
    ) {
        val selectedIds = multiSelectionManagerImpl?.state?.value?.selectedIds ?: return

        setBottomSheetState(
            MultiMusicBottomSheet(
                selectedIds = selectedIds,
                onClose = {
                    setBottomSheetState(null)
                },
                onDeleteAll = { showDeleteMultiMusicDialog(selectedIds) },
                onRemoveFromPlaylist = {
                    currentPlaylist?.let {
                        removeMusicFromPlaylistDialog(
                            musicIdsToRemove = selectedIds,
                            currentPlaylist = it,
                        )
                    }
                },
                onAddToPlaylist = { },
                onPlayNext = {
                    CoroutineScope(Dispatchers.IO).launch {
                        val selectedMusics: List<Music> = selectedIds.mapNotNull { commonMusicUseCase.getFromId(it).firstOrNull() }
                        playbackManager.addMultipleMusicsToPlayNext(
                            musics = selectedMusics,
                        )
                    }
                    multiSelectionManagerImpl?.clearMultiSelection()
                    setBottomSheetState(null)
                },
                onAddToQueue = {
                    CoroutineScope(Dispatchers.IO).launch {
                        val selectedMusics: List<Music> = selectedIds.mapNotNull { commonMusicUseCase.getFromId(it).firstOrNull() }
                        playbackManager.addMultipleMusicsToQueue(
                            musics = selectedMusics,
                        )
                    }
                    multiSelectionManagerImpl?.clearMultiSelection()
                    setBottomSheetState(null)
                },
                onRemoveFromPlayedList = {
                    CoroutineScope(Dispatchers.IO).launch {
                        playbackManager.removeSongsFromPlayedPlaylist(
                            musicIds = selectedIds,
                        )
                    }
                    multiSelectionManagerImpl?.clearMultiSelection()
                    setBottomSheetState(null)
                },
                musicBottomSheetMode = musicBottomSheetMode,
            )
        )
    }
}