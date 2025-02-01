package com.github.enteraname74.soulsearching.commondelegate

import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.domain.model.PlaylistWithMusicsNumber
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.usecase.playlist.DeletePlaylistUseCase
import com.github.enteraname74.domain.usecase.playlist.GetPlaylistWithMusicsUseCase
import com.github.enteraname74.domain.usecase.playlist.UpdatePlaylistUseCase
import com.github.enteraname74.soulsearching.composables.bottomsheets.playlist.PlaylistBottomSheet
import com.github.enteraname74.soulsearching.composables.dialog.DeletePlaylistDialog
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpManager
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.coreui.multiselection.MultiSelectionManagerImpl
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

interface PlaylistBottomSheetDelegate {
    fun showPlaylistBottomSheet(selectedPlaylist: PlaylistWithMusicsNumber)
}

class PlaylistBottomSheetDelegateImpl(
    private val deletePlaylistUseCase: DeletePlaylistUseCase,
    private val updatePlaylistUseCase: UpdatePlaylistUseCase,
    private val getPlaylistWithMusicsUseCase: GetPlaylistWithMusicsUseCase,
    private val playbackManager: PlaybackManager,
    private val feedbackPopUpManager: FeedbackPopUpManager,
    private val loadingManager: LoadingManager,
) : PlaylistBottomSheetDelegate {

    private var setDialogState: (SoulDialog?) -> Unit = {}
    private var setBottomSheetState: (SoulBottomSheet?) -> Unit = {}
    private var onModifyPlaylist: (playlist: Playlist) -> Unit = {}
    private var multiSelectionManagerImpl: MultiSelectionManagerImpl? = null

    fun initDelegate(
        setDialogState: (SoulDialog?) -> Unit,
        setBottomSheetState: (SoulBottomSheet?) -> Unit,
        onModifyPlaylist: (playlist: Playlist) -> Unit,
        multiSelectionManagerImpl: MultiSelectionManagerImpl,
    ) {
        this.setDialogState = setDialogState
        this.setBottomSheetState = setBottomSheetState
        this.onModifyPlaylist = onModifyPlaylist
        this.multiSelectionManagerImpl = multiSelectionManagerImpl
    }

    private fun showDeletePlaylistDialog(playlistToDelete: Playlist) {
        setDialogState(
            DeletePlaylistDialog(
                playlistToDelete = playlistToDelete,
                onDelete = {
                    setDialogState(null)
                    // We make sure to close the bottom sheet after deleting the selected music.
                    setBottomSheetState(null)

                    loadingManager.withLoadingOnIO {
                        val result = deletePlaylistUseCase(playlistToDelete)
                        feedbackPopUpManager.showResultErrorIfAny(result)
                        multiSelectionManagerImpl?.clearMultiSelection()
                    }
                },
                onClose = { setDialogState(null) }
            )
        )
    }

    override fun showPlaylistBottomSheet(selectedPlaylist: PlaylistWithMusicsNumber) {
        setBottomSheetState(
            PlaylistBottomSheet(
                selectedPlaylist = selectedPlaylist,
                onClose = { setBottomSheetState(null) },
                onDeletePlaylist = { showDeletePlaylistDialog(playlistToDelete = selectedPlaylist.playlist) },
                onModifyPlaylist = { onModifyPlaylist(selectedPlaylist.playlist) },
                toggleQuickAccess = {
                    loadingManager.withLoadingOnIO {
                        val result: SoulResult<Unit> = updatePlaylistUseCase(
                            playlist = selectedPlaylist.playlist.copy(
                                isInQuickAccess = !selectedPlaylist.isInQuickAccess,
                            )
                        )
                        feedbackPopUpManager.showResultErrorIfAny(result)
                        multiSelectionManagerImpl?.clearMultiSelection()
                    }
                },
                onPlayNext = {
                    CoroutineScope(Dispatchers.IO).launch {
                        val playlistWithMusics: PlaylistWithMusics = getPlaylistWithMusicsUseCase(
                            playlistId = selectedPlaylist.playlist.playlistId,
                        ).firstOrNull() ?: return@launch

                        playbackManager.addMultipleMusicsToPlayNext(
                            musics = playlistWithMusics.musics,
                        )
                        multiSelectionManagerImpl?.clearMultiSelection()
                    }
                },
                onRemoveFromPlayedList = {
                    CoroutineScope(Dispatchers.IO).launch {
                        val playlistWithMusics: PlaylistWithMusics = getPlaylistWithMusicsUseCase(
                            playlistId = selectedPlaylist.playlist.playlistId,
                        ).firstOrNull() ?: return@launch

                        playbackManager.removeSongsFromPlayedPlaylist(
                            musicIds = playlistWithMusics.musics.map { it.musicId },
                        )
                        multiSelectionManagerImpl?.clearMultiSelection()
                    }
                }
            )
        )
    }
}