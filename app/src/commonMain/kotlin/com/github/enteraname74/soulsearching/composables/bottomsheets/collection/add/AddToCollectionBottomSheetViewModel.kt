package com.github.enteraname74.soulsearching.composables.bottomsheets.collection.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.soulsearching.composables.dialog.CreateCollectionDialog
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.domain.usecase.collection.CommonCollectionUseCase
import com.github.enteraname74.soulsearching.feature.multiselection.MultiSelectionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.uuid.Uuid

class AddToCollectionBottomSheetViewModel(
    private val commonCollectionUseCase: CommonCollectionUseCase,
    private val loadingManager: LoadingManager,
    private val params: AddToCollectionBottomSheetDestination,
    private val navScope: AddToCollectionBottomSheetNavScope,
    private val multiSelectionManager: MultiSelectionManager,
) : ViewModel() {
    private val dialogState = MutableStateFlow<SoulDialog?>(null)
    private val selectedCollectionIds = MutableStateFlow<Set<Uuid>>(emptySet())

    private val alreadyContainingCollectionIds: Flow<List<Uuid>> = when {
        params.artistIds.size == 1 && selectedElementCount == 1 ->
            commonCollectionUseCase.getCollectionIdsContainingArtist(params.artistIds.first())
        params.albumIds.size == 1 && selectedElementCount == 1 ->
            commonCollectionUseCase.getCollectionIdsContainingAlbum(params.albumIds.first())
        params.playlistIds.size == 1 && selectedElementCount == 1 ->
            commonCollectionUseCase.getCollectionIdsContainingPlaylist(params.playlistIds.first())
        else -> flowOf(emptyList())
    }

    val state: StateFlow<AddToCollectionBottomSheetState> = combine(
        dialogState,
        selectedCollectionIds,
        commonCollectionUseCase.getAll(),
        alreadyContainingCollectionIds,
    ) { dialog, selectedIds, collections, containingIds ->
        AddToCollectionBottomSheetState(
            dialogState = dialog,
            selectedCollectionIds = selectedIds,
            collections = collections.filterNot { it.id in containingIds },
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = AddToCollectionBottomSheetState(),
    )

    private val selectedElementCount: Int
        get() = params.artistIds.size + params.albumIds.size + params.playlistIds.size

    fun showCreateCollectionDialog() {
        dialogState.value = CreateCollectionDialog(
            onDismiss = { dialogState.value = null },
            onConfirm = { name ->
                viewModelScope.launch {
                    loadingManager.withLoading {
                        val collection = commonCollectionUseCase.create(name)
                        addToCollections(listOf(collection.collectionId))
                    }
                    finish()
                }
            },
        )
    }

    fun toggleSelection(collectionId: Uuid) {
        selectedCollectionIds.value = if (collectionId in selectedCollectionIds.value) {
            selectedCollectionIds.value - collectionId
        } else {
            selectedCollectionIds.value + collectionId
        }
    }

    fun confirm() {
        viewModelScope.launch {
            loadingManager.withLoading {
                addToCollections(selectedCollectionIds.value.toList())
            }
            finish()
        }
    }

    private suspend fun addToCollections(collectionIds: List<Uuid>) {
        commonCollectionUseCase.addArtists(collectionIds, params.artistIds)
        commonCollectionUseCase.addAlbums(collectionIds, params.albumIds)
        commonCollectionUseCase.addPlaylists(collectionIds, params.playlistIds)
    }

    private fun finish() {
        dialogState.value = null
        multiSelectionManager.clearMultiSelection()
        navScope.onSave()
    }

    fun navigateBack() = navScope.navigateBack()
}
