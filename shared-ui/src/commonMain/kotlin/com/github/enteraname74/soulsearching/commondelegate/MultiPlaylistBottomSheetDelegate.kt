package com.github.enteraname74.soulsearching.commondelegate

import com.github.enteraname74.domain.usecase.playlist.DeleteAllPlaylistsUseCase
import com.github.enteraname74.soulsearching.composables.bottomsheets.multiplaylist.MultiPlaylistBottomSheet
import com.github.enteraname74.soulsearching.composables.dialog.DeleteMultiPlaylistDialog
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.coreui.multiselection.MultiSelectionManagerImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

interface MultiPlaylistBottomSheetDelegate {
    fun showMultiPlaylistBottomSheet()
}

class MultiPlaylistBottomSheetDelegateImpl(
    private val deleteAllPlaylistsUseCase: DeleteAllPlaylistsUseCase,
    private val loadingManager: LoadingManager,
): MultiPlaylistBottomSheetDelegate {
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

    private fun showDeleteMultiPlaylistDialog(
        selectedIdsToDelete: List<UUID>,
    ) {
        setDialogState(
            DeleteMultiPlaylistDialog(
                onDelete = {
                    CoroutineScope(Dispatchers.IO).launch {
                        loadingManager.withLoading {
                            deleteAllPlaylistsUseCase(selectedIdsToDelete)
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

    override fun showMultiPlaylistBottomSheet() {
        val selectedIds = multiSelectionManagerImpl?.state?.value?.selectedIds ?: return

        setBottomSheetState(
            MultiPlaylistBottomSheet(
                onClose = { setBottomSheetState(null) },
                selectedIds = selectedIds,
                onDelete = { showDeleteMultiPlaylistDialog(selectedIds) }
            )
        )
    }
}