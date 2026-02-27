package com.github.enteraname74.soulsearching.commondelegate

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.domain.usecase.album.CommonAlbumUseCase
import com.github.enteraname74.domain.usecase.album.DeleteAlbumUseCase
import com.github.enteraname74.soulsearching.composables.bottomsheets.album.AlbumBottomSheet
import com.github.enteraname74.soulsearching.composables.dialog.DeleteAlbumDialog
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.feature.multiselection.MultiSelectionManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface AlbumBottomSheetDelegate {
    fun showAlbumBottomSheet(albumWithMusics: AlbumWithMusics)
}

class AlbumBottomSheetDelegateImpl(
    private val deleteAlbumUseCase: DeleteAlbumUseCase,
    private val commonAlbumUseCase: CommonAlbumUseCase,
    private val playbackManager: PlaybackManager,
) : AlbumBottomSheetDelegate {
    private var setDialogState: (SoulDialog?) -> Unit = {}
    private var setBottomSheetState: (SoulBottomSheet?) -> Unit = {}
    private var onModifyAlbum: (album: Album) -> Unit = {}
    private var multiSelectionManager: MultiSelectionManager? = null

    private fun showDeleteAlbumDialog(album: Album) {
        setDialogState(
            DeleteAlbumDialog(
                selectedAlbum = album,
                onDelete = {
                    CoroutineScope(Dispatchers.IO).launch {
                        deleteAlbumUseCase(album.albumId)
                        multiSelectionManager?.clearMultiSelection()

                        setDialogState(null)
                        // We make sure to close the bottom sheet after deleting the selected music.
                        setBottomSheetState(null)
                        multiSelectionManager?.clearMultiSelection()
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
                        commonAlbumUseCase.upsert(
                            album = albumWithMusics.album.copy(
                                isInQuickAccess = !albumWithMusics.album.isInQuickAccess,
                            )
                        )
                        multiSelectionManager?.clearMultiSelection()
                        setBottomSheetState(null)
                    }
                },
                onPlayNext = {
                    CoroutineScope(Dispatchers.IO).launch {
                        playbackManager.addMultipleMusicsToPlayNext(
                            musics = albumWithMusics.musics,
                        )
                        multiSelectionManager?.clearMultiSelection()
                        setBottomSheetState(null)
                    }
                },
                onAddToQueue = {
                    CoroutineScope(Dispatchers.IO).launch {
                        playbackManager.addMultipleMusicsToQueue(
                            musics = albumWithMusics.musics,
                        )
                        multiSelectionManager?.clearMultiSelection()
                        setBottomSheetState(null)
                    }
                },
                onRemoveFromPlayedList = {
                    CoroutineScope(Dispatchers.IO).launch {
                        playbackManager.removeSongsFromPlayedPlaylist(
                            musicIds = albumWithMusics.musics.map { it.musicId },
                        )
                        multiSelectionManager?.clearMultiSelection()
                        setBottomSheetState(null)
                    }
                }
            )
        )
    }
}