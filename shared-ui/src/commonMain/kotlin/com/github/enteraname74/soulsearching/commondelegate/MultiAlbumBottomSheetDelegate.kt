package com.github.enteraname74.soulsearching.commondelegate

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.domain.usecase.album.DeleteAllAlbumsUseCase
import com.github.enteraname74.domain.usecase.album.GetAlbumWithMusicsUseCase
import com.github.enteraname74.soulsearching.composables.bottomsheets.multialbum.MultiAlbumBottomSheet
import com.github.enteraname74.soulsearching.composables.dialog.DeleteMultiAlbumDialog
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.coreui.multiselection.MultiSelectionManagerImpl
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import java.util.UUID

interface MultiAlbumBottomSheetDelegate {
    fun showMultiAlbumBottomSheet()
}

class MultiAlbumBottomSheetDelegateImpl(
    private val deleteAllAlbumsUseCase: DeleteAllAlbumsUseCase,
    private val getAlbumWithMusicsUseCase: GetAlbumWithMusicsUseCase,
    private val loadingManager: LoadingManager,
    private val playbackManager: PlaybackManager,
): MultiAlbumBottomSheetDelegate {
    private var setDialogState: (SoulDialog?) -> Unit = {}
    private var setBottomSheetState: (SoulBottomSheet?) -> Unit = {}
    private var multiSelectionManagerImpl: MultiSelectionManagerImpl? = null

    fun initDelegate(
        setDialogState: (SoulDialog?) -> Unit,
        setBottomSheetState: (SoulBottomSheet?) -> Unit,
        multiSelectionManagerImpl: MultiSelectionManagerImpl,
    ) {
        this.setDialogState = setDialogState
        this.setBottomSheetState = setBottomSheetState
        this.multiSelectionManagerImpl = multiSelectionManagerImpl
    }

    private fun showDeleteMultiAlbumDialog(
        selectedIdsToDelete: List<UUID>,
    ) {
        setDialogState(
            DeleteMultiAlbumDialog(
                onDelete = {
                    CoroutineScope(Dispatchers.IO).launch {
                        loadingManager.withLoading {
                            selectedIdsToDelete.forEach { albumId ->
                                getAlbumWithMusicsUseCase(
                                    albumId = albumId
                                ).firstOrNull()?.let { albumWithMusics ->
                                    playbackManager.removeSongsFromPlayedPlaylist(
                                        musicIds = albumWithMusics.musics.map { it.musicId },
                                    )
                                }
                            }
                            deleteAllAlbumsUseCase(selectedIdsToDelete)
                            multiSelectionManagerImpl?.clearMultiSelection()
                        }
                        setDialogState(null)
                        setBottomSheetState(null)
                    }
                },
                onClose = { setDialogState(null) }
            )
        )
    }

    override fun showMultiAlbumBottomSheet() {
        val selectedIds = multiSelectionManagerImpl?.state?.value?.selectedIds ?: emptyList()

        setBottomSheetState(
            MultiAlbumBottomSheet(
                onClose = { setBottomSheetState(null) },
                selectedIds = selectedIds,
                onDelete = { showDeleteMultiAlbumDialog(selectedIds) }
            )
        )
    }
}