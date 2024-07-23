package com.github.enteraname74.soulsearching.commondelegate

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.usecase.album.DeleteAlbumUseCase
import com.github.enteraname74.domain.usecase.album.UpsertAlbumUseCase
import com.github.enteraname74.soulsearching.composables.bottomsheets.album.AlbumBottomSheet
import com.github.enteraname74.soulsearching.composables.dialog.DeleteAlbumDialog
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface AlbumBottomSheetDelegate {
    fun showAlbumBottomSheet(album: Album)
}

class AlbumBottomSheetDelegateImpl(
    private val deleteAlbumUseCase: DeleteAlbumUseCase,
    private val upsertAlbumUseCase: UpsertAlbumUseCase,
) : AlbumBottomSheetDelegate {
    private var setDialogState: (SoulDialog?) -> Unit = {}
    private var setBottomSheetState: (SoulBottomSheet?) -> Unit = {}
    private var onModifyAlbum: (album: Album) -> Unit = {}

    fun initDelegate(
        setDialogState: (SoulDialog?) -> Unit,
        setBottomSheetState: (SoulBottomSheet?) -> Unit,
        onModifyAlbum: (album: Album) -> Unit,
    ) {
        this.setDialogState = setDialogState
        this.setBottomSheetState = setBottomSheetState
        this.onModifyAlbum = onModifyAlbum
    }

    private fun showDeleteAlbumDialog(album: Album) {
        setDialogState(
            DeleteAlbumDialog(
                selectedAlbum = album,
                onDelete = {
                    CoroutineScope(Dispatchers.IO).launch {
                        deleteAlbumUseCase(album.albumId)
                    }
                    setDialogState(null)
                    // We make sure to close the bottom sheet after deleting the selected music.
                    setBottomSheetState(null)
                },
                onClose = { setDialogState(null) }
            )
        )
    }

    override fun showAlbumBottomSheet(album: Album) {
        setBottomSheetState(
            AlbumBottomSheet(
                selectedAlbum = album,
                onClose = { setBottomSheetState(null) },
                onDeleteAlbum = { showDeleteAlbumDialog(album = album) },
                onModifyAlbum = { onModifyAlbum(album) },
                toggleQuickAccess = {
                    CoroutineScope(Dispatchers.IO).launch {
                        upsertAlbumUseCase(
                            album = album.copy(
                                isInQuickAccess = !album.isInQuickAccess,
                            )
                        )
                    }
                }
            )
        )
    }
}