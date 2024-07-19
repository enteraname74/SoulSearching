package com.github.enteraname74.soulsearching.commondelegate

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.domain.usecase.playlist.DeletePlaylistUseCase
import com.github.enteraname74.domain.usecase.playlist.UpsertPlaylistUseCase
import com.github.enteraname74.soulsearching.composables.bottomsheets.music.AddToPlaylistBottomSheet
import com.github.enteraname74.soulsearching.composables.bottomsheets.playlist.PlaylistBottomSheet
import com.github.enteraname74.soulsearching.composables.dialog.DeletePlaylistDialog
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface PlaylistBottomSheetDelegate {
    fun showPlaylistBottomSheet(selectedPlaylist: PlaylistWithMusics)
}

class PlaylistBottomSheetDelegateImpl(
    private val deletePlaylistUseCase: DeletePlaylistUseCase,
    private val upsertPlaylistUseCase: UpsertPlaylistUseCase,
) : PlaylistBottomSheetDelegate {

    private var setDialogState: (SoulDialog?) -> Unit = {}
    private var setBottomSheetState: (SoulBottomSheet?) -> Unit = {}
    private var onModifyPlaylist: (playlist: Playlist) -> Unit = {}

    fun initDelegate(
        setDialogState: (SoulDialog?) -> Unit,
        setBottomSheetState: (SoulBottomSheet?) -> Unit,
        onModifyPlaylist: (playlist: Playlist) -> Unit,
    ) {
        this.setDialogState = setDialogState
        this.setBottomSheetState = setBottomSheetState
        this.onModifyPlaylist = onModifyPlaylist
    }

    private fun showDeletePlaylistDialog(playlistToDelete: PlaylistWithMusics) {
        setDialogState(
            DeletePlaylistDialog(
                playlistToDelete = playlistToDelete,
                onDelete = {
                    CoroutineScope(Dispatchers.IO).launch {
                        deletePlaylistUseCase(playlistToDelete.playlist)
                    }
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
                selectedPlaylist = selectedPlaylist.playlist,
                onClose = { setBottomSheetState(null) },
                onDeletePlaylist = { showDeletePlaylistDialog(playlistToDelete = selectedPlaylist) },
                onModifyPlaylist = { onModifyPlaylist(selectedPlaylist.playlist) },
                toggleQuickAccess = {
                    CoroutineScope(Dispatchers.IO).launch {
                        upsertPlaylistUseCase(
                            playlist = selectedPlaylist.playlist.copy(
                                isInQuickAccess = !selectedPlaylist.playlist.isInQuickAccess,
                            )
                        )
                    }
                }
            )
        )
    }
}