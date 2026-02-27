package com.github.enteraname74.soulsearching.commondelegate

import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.domain.usecase.playlist.CommonPlaylistUseCase
import com.github.enteraname74.soulsearching.composables.bottomsheets.playlist.PlaylistBottomSheet
import com.github.enteraname74.soulsearching.composables.dialog.DeletePlaylistDialog
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.feature.multiselection.MultiSelectionManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

interface PlaylistBottomSheetDelegate {
    fun showPlaylistBottomSheet(selectedPlaylist: PlaylistWithMusics)
}

class PlaylistBottomSheetDelegateImpl(
    private val commonPlaylistUseCase: CommonPlaylistUseCase,
    private val playbackManager: PlaybackManager,
) : PlaylistBottomSheetDelegate {

    private var setDialogState: (SoulDialog?) -> Unit = {}
    private var setBottomSheetState: (SoulBottomSheet?) -> Unit = {}
    private var onModifyPlaylist: (playlist: Playlist) -> Unit = {}
    private var multiSelectionManager: MultiSelectionManager? = null

    private fun showDeletePlaylistDialog(playlistToDelete: Playlist) {
        setDialogState(
            DeletePlaylistDialog(
                playlistToDelete = playlistToDelete,
                onDelete = {
                    CoroutineScope(Dispatchers.IO).launch {
                        commonPlaylistUseCase.delete(playlistToDelete)
                    }
                    multiSelectionManager?.clearMultiSelection()
                    setDialogState(null)
                    // We make sure to close the bottom sheet after deleting the selected music.
                    setBottomSheetState(null)
                },
                onClose = { setDialogState(null) }
            )
        )
    }

    override fun showPlaylistBottomSheet(selectedPlaylist: PlaylistWithMusics) {
        setBottomSheetState(
            PlaylistBottomSheet(
                selectedPlaylist = selectedPlaylist.toPlaylistPreview(),
                onClose = { setBottomSheetState(null) },
                onDeletePlaylist = { showDeletePlaylistDialog(playlistToDelete = selectedPlaylist.playlist) },
                onModifyPlaylist = { onModifyPlaylist(selectedPlaylist.playlist) },
                toggleQuickAccess = {
                    CoroutineScope(Dispatchers.IO).launch {
                        commonPlaylistUseCase.upsert(
                            playlist = selectedPlaylist.playlist.copy(
                                isInQuickAccess = !selectedPlaylist.playlist.isInQuickAccess,
                            )
                        )
                        multiSelectionManager?.clearMultiSelection()
                        setBottomSheetState(null)
                    }
                },
                onPlayNext = {
                    CoroutineScope(Dispatchers.IO).launch {
                        val playlistWithMusics: PlaylistWithMusics = commonPlaylistUseCase.getWithMusics(
                            playlistId = selectedPlaylist.playlist.playlistId,
                        ).firstOrNull() ?: return@launch

                        playbackManager.addMultipleMusicsToPlayNext(
                            musics = playlistWithMusics.musics,
                        )
                        multiSelectionManager?.clearMultiSelection()
                        setBottomSheetState(null)
                    }
                },
                onAddToQueue = {
                    CoroutineScope(Dispatchers.IO).launch {
                        val playlistWithMusics: PlaylistWithMusics = commonPlaylistUseCase.getWithMusics(
                            playlistId = selectedPlaylist.playlist.playlistId,
                        ).firstOrNull() ?: return@launch

                        playbackManager.addMultipleMusicsToQueue(
                            musics = playlistWithMusics.musics,
                        )
                        multiSelectionManager?.clearMultiSelection()
                        setBottomSheetState(null)
                    }
                },
                onRemoveFromPlayedList = {
                    CoroutineScope(Dispatchers.IO).launch {
                        val playlistWithMusics: PlaylistWithMusics = commonPlaylistUseCase.getWithMusics(
                            playlistId = selectedPlaylist.playlist.playlistId,
                        ).firstOrNull() ?: return@launch

                        playbackManager.removeSongsFromPlayedPlaylist(
                            musicIds = playlistWithMusics.musics.map { it.musicId },
                        )
                        multiSelectionManager?.clearMultiSelection()
                        setBottomSheetState(null)
                    }
                }
            )
        )
    }
}