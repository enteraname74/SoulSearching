package com.github.enteraname74.soulsearching.commondelegate

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.usecase.album.DeleteAlbumUseCase
import com.github.enteraname74.domain.usecase.album.ToggleAlbumQuickAccessStateUseCase
import com.github.enteraname74.soulsearching.composables.bottomsheets.album.AlbumBottomSheet
import com.github.enteraname74.soulsearching.composables.dialog.DeleteAlbumDialog
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpManager
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.coreui.multiselection.MultiSelectionManagerImpl
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface AlbumBottomSheetDelegate {
    fun showAlbumBottomSheet(albumWithMusics: AlbumWithMusics)
}

class AlbumBottomSheetDelegateImpl(
    private val deleteAlbumUseCase: DeleteAlbumUseCase,
    private val toggleAlbumQuickAccessStateUseCase: ToggleAlbumQuickAccessStateUseCase,
    private val playbackManager: PlaybackManager,
    private val feedbackPopUpManager: FeedbackPopUpManager,
    private val loadingManager: LoadingManager,
) : AlbumBottomSheetDelegate {
    private var setDialogState: (SoulDialog?) -> Unit = {}
    private var setBottomSheetState: (SoulBottomSheet?) -> Unit = {}
    private var onModifyAlbum: (album: Album) -> Unit = {}
    private var multiSelectionManagerImpl: MultiSelectionManagerImpl? = null

    fun initDelegate(
        setDialogState: (SoulDialog?) -> Unit,
        setBottomSheetState: (SoulBottomSheet?) -> Unit,
        onModifyAlbum: (album: Album) -> Unit,
        multiSelectionManagerImpl: MultiSelectionManagerImpl,
    ) {
        this.setDialogState = setDialogState
        this.setBottomSheetState = setBottomSheetState
        this.onModifyAlbum = onModifyAlbum
        this.multiSelectionManagerImpl = multiSelectionManagerImpl
    }

    private fun showDeleteAlbumDialog(album: Album) {
        setDialogState(
            DeleteAlbumDialog(
                selectedAlbum = album,
                onDelete = {
                    CoroutineScope(Dispatchers.IO).launch {
                        loadingManager.withLoading {
                            val result: SoulResult<String> = deleteAlbumUseCase(album)
                            feedbackPopUpManager.showResultErrorIfAny(result)
                        }
                        multiSelectionManagerImpl?.clearMultiSelection()

                        setDialogState(null)
                        // We make sure to close the bottom sheet after deleting the selected music.
                        setBottomSheetState(null)
                        multiSelectionManagerImpl?.clearMultiSelection()
                    }
                },
                onClose = { setDialogState(null) }
            )
        )
    }

    override fun showAlbumBottomSheet(albumWithMusics: AlbumWithMusics) {
        setBottomSheetState(
            AlbumBottomSheet(
                selectedAlbum = albumWithMusics,
                onClose = { setBottomSheetState(null) },
                onDeleteAlbum = { showDeleteAlbumDialog(album = albumWithMusics.album) },
                onModifyAlbum = { onModifyAlbum(albumWithMusics.album) },
                toggleQuickAccess = {
                    CoroutineScope(Dispatchers.IO).launch {
                        loadingManager.withLoading {
                            val result: SoulResult<String> = toggleAlbumQuickAccessStateUseCase(album = albumWithMusics.album)
                            feedbackPopUpManager.showResultErrorIfAny(result)
                        }
                        multiSelectionManagerImpl?.clearMultiSelection()
                        setBottomSheetState(null)
                    }
                },
                onPlayNext = {
                    CoroutineScope(Dispatchers.IO).launch {
                        playbackManager.addMultipleMusicsToPlayNext(
                            musics = albumWithMusics.musics,
                        )
                        multiSelectionManagerImpl?.clearMultiSelection()
                        setBottomSheetState(null)
                    }
                },
                onRemoveFromPlayedList = {
                    CoroutineScope(Dispatchers.IO).launch {
                        playbackManager.removeSongsFromPlayedPlaylist(
                            musicIds = albumWithMusics.musics.map { it.musicId },
                        )
                        multiSelectionManagerImpl?.clearMultiSelection()
                        setBottomSheetState(null)
                    }
                }
            )
        )
    }
}