package com.github.enteraname74.soulsearching.commondelegate

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.MusicPlaylist
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.domain.usecase.music.DeleteAllMusicsUseCase
import com.github.enteraname74.domain.usecase.music.GetMusicUseCase
import com.github.enteraname74.domain.usecase.musicplaylist.DeleteMusicFromPlaylistUseCase
import com.github.enteraname74.domain.usecase.musicplaylist.UpsertMusicIntoPlaylistUseCase
import com.github.enteraname74.domain.usecase.playlist.GetAllPlaylistWithMusicsUseCase
import com.github.enteraname74.soulsearching.composables.bottomsheets.multimusic.MultiMusicBottomSheet
import com.github.enteraname74.soulsearching.composables.bottomsheets.music.AddToPlaylistBottomSheet
import com.github.enteraname74.soulsearching.composables.dialog.DeleteMultiMusicDialog
import com.github.enteraname74.soulsearching.composables.dialog.RemoveMultiMusicFromPlaylistDialog
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.coreui.multiselection.MultiSelectionManagerImpl
import com.github.enteraname74.soulsearching.domain.model.types.MusicBottomSheetState
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.*

interface MultiMusicBottomSheetDelegate {
    fun showMultiMusicBottomSheet(
        currentPlaylist: Playlist? = null
    )
}

class MultiMusicBottomSheetDelegateImpl(
    getAllPlaylistsWithMusicsUseCase: GetAllPlaylistWithMusicsUseCase,
    private val deleteAllMusicsUseCase: DeleteAllMusicsUseCase,
    private val deleteMusicFromPlaylistUseCase: DeleteMusicFromPlaylistUseCase,
    private val upsertMusicIntoPlaylistUseCase: UpsertMusicIntoPlaylistUseCase,
    private val getMusicUseCase: GetMusicUseCase,
    private val loadingManager: LoadingManager,
    private val playbackManager: PlaybackManager,
): MultiMusicBottomSheetDelegate {
    private val allPlaylists: StateFlow<List<PlaylistWithMusics>> = getAllPlaylistsWithMusicsUseCase()
        .stateIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.Eagerly,
            initialValue = emptyList(),
        )

    private var setDialogState: (SoulDialog?) -> Unit = {}
    private var setBottomSheetState: (SoulBottomSheet?) -> Unit = {}
    private var setAddToPlaylistBottomSheetState: (AddToPlaylistBottomSheet?) -> Unit = {}
    private var musicBottomSheetState: MusicBottomSheetState = MusicBottomSheetState.NORMAL
    private var multiSelectionManagerImpl: MultiSelectionManagerImpl? = null

    fun initDelegate(
        setDialogState: (SoulDialog?) -> Unit,
        setBottomSheetState: (SoulBottomSheet?) -> Unit,
        setAddToPlaylistBottomSheetState: (AddToPlaylistBottomSheet?) -> Unit,
        musicBottomSheetState: MusicBottomSheetState = MusicBottomSheetState.NORMAL,
        multiSelectionManagerImpl: MultiSelectionManagerImpl,
    ) {
        this.setDialogState = setDialogState
        this.setBottomSheetState = setBottomSheetState
        this.setAddToPlaylistBottomSheetState = setAddToPlaylistBottomSheetState
        this.musicBottomSheetState = musicBottomSheetState
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
                            deleteAllMusicsUseCase(selectedIdsToDelete)
                            playbackManager.removeSongsFromPlayedPlaylist(selectedIdsToDelete)
                            multiSelectionManagerImpl?.clearMultiSelection()
                        }
                    }
                    setDialogState(null)
                    // We make sure to close the bottom sheet after removing the selected musics.
                    setBottomSheetState(null)
                },
                onClose = { setDialogState(null) },
            )
        )
    }

    /**
     * Add a music to multiple playlists.
     */
    private fun addMusicToPlaylists(musicId: UUID, selectedPlaylists: List<PlaylistWithMusics>) {
        CoroutineScope(Dispatchers.IO).launch {
            for (selectedPlaylist in selectedPlaylists) {
                if (selectedPlaylist.musics.find { it.musicId == musicId } == null) {
                    upsertMusicIntoPlaylistUseCase(
                        MusicPlaylist(
                            musicId = musicId,
                            playlistId = selectedPlaylist.playlist.playlistId,
                        )
                    )
                }
            }
        }
    }

    private fun showAddToPlaylistsBottomSheet(
        selectedIds: List<UUID>
    ) {
        setAddToPlaylistBottomSheetState(
            AddToPlaylistBottomSheet(
                onClose = {
                    setAddToPlaylistBottomSheetState(null)
                },
                addMusicToSelectedPlaylists = { selectedPlaylists ->
                    selectedIds.forEach { musicId ->
                        addMusicToPlaylists(
                            musicId = musicId,
                            selectedPlaylists = selectedPlaylists,
                        )
                    }
                    multiSelectionManagerImpl?.clearMultiSelection()
                    setBottomSheetState(null)
                },
                playlistsWithMusics = allPlaylists.value,
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
                            deleteMusicFromPlaylistUseCase(
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
                onAddToPlaylist = {
                    showAddToPlaylistsBottomSheet(
                        selectedIds = selectedIds,
                    )
                },
                onPlayNext = {
                    CoroutineScope(Dispatchers.IO).launch {
                        val selectedMusics: List<Music> = selectedIds.mapNotNull { getMusicUseCase(it).firstOrNull() }
                        playbackManager.addMultipleMusicsToPlayNext(
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
                musicBottomSheetState = musicBottomSheetState,
                multiSelectionManagerImpl = multiSelectionManagerImpl,
            )
        )
    }
}