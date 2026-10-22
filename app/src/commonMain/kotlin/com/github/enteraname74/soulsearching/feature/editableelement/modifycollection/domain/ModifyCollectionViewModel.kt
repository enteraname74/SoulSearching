package com.github.enteraname74.soulsearching.feature.editableelement.modifycollection.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpManager
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.domain.model.CollectionWithMusics
import com.github.enteraname74.soulsearching.domain.model.Cover
import com.github.enteraname74.soulsearching.domain.model.SoulResult
import com.github.enteraname74.soulsearching.domain.usecase.collection.CommonCollectionUseCase
import com.github.enteraname74.soulsearching.domain.usecase.cover.CommonCoverUseCase
import com.github.enteraname74.soulsearching.domain.util.WorkDispatcher
import com.github.enteraname74.soulsearching.feature.editableelement.composable.EditableElementCoversBottomSheet
import com.github.enteraname74.soulsearching.feature.editableelement.domain.CoverEditManager
import com.github.enteraname74.soulsearching.feature.editableelement.domain.CoverEditMode
import com.github.enteraname74.soulsearching.feature.editableelement.domain.CoverListState
import com.github.enteraname74.soulsearching.feature.editableelement.modifycollection.domain.state.ModifyCollectionFormState
import com.github.enteraname74.soulsearching.feature.editableelement.modifycollection.domain.state.ModifyCollectionNavigationState
import com.github.enteraname74.soulsearching.feature.editableelement.modifycollection.domain.state.ModifyCollectionState
import com.github.enteraname74.soulsearching.feature.editableelement.modifycollection.presentation.ModifyCollectionDestination
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

class ModifyCollectionViewModel(
    private val commonCollectionUseCase: CommonCollectionUseCase,
    private val commonCoverUseCase: CommonCoverUseCase,
    private val loadingManager: LoadingManager,
    private val workDispatcher: WorkDispatcher,
    private val coverEditManager: CoverEditManager,
    private val feedbackPopUpManager: FeedbackPopUpManager,
    destination: ModifyCollectionDestination,
) : ViewModel() {
    private val collectionId = destination.selectedCollectionId
    private val navigation = MutableStateFlow<ModifyCollectionNavigationState>(ModifyCollectionNavigationState.Idle)
    val navigationState: StateFlow<ModifyCollectionNavigationState> = navigation.asStateFlow()

    private val bottomSheet = MutableStateFlow<SoulBottomSheet?>(null)
    val bottomSheetState: StateFlow<SoulBottomSheet?> = bottomSheet.asStateFlow()

    private val initialCollection: Flow<CollectionWithMusics?> = commonCollectionUseCase
        .getFromIds(listOf(collectionId))
        .map { it.firstOrNull() }

    private val newSimpleCover: MutableStateFlow<ByteArray?> = MutableStateFlow(null)
    private val newGridCover: MutableStateFlow<CoverEditMode.Grid.GridContent> = MutableStateFlow(
        CoverEditMode.Grid.GridContent(
            topStart = null,
            topEnd = null,
            bottomStart = null,
            bottomEnd = null,
        )
    )

    private val selectedCoverMode: MutableStateFlow<CoverEditMode.Type?> = MutableStateFlow(null)

    val state: StateFlow<ModifyCollectionState> = combine(
        initialCollection,
        newSimpleCover,
        newGridCover,
        selectedCoverMode,
    ) { collection, newCover, newGridCover, selectedCoverMode ->
        if (collection == null) {
            ModifyCollectionState.Loading
        } else {
            ModifyCollectionState.Data(
                initialCollection = collection,
                coverEditMode = CoverEditMode(
                    simple = CoverEditMode.Simple(
                        initialCover = (collection.cover as? Cover.Simple),
                        newCover = newCover,
                    ),
                    grid = CoverEditMode.Grid(
                        initialCover = (collection.cover as? Cover.Grid),
                        newCover = newGridCover,
                    ),
                    selectedType = selectedCoverMode ?: CoverEditMode.Type.fromCover(collection.cover),
                ),
            )
        }
    }.stateIn(
        scope = viewModelScope.plus(workDispatcher.dispatcher),
        started = SharingStarted.Eagerly,
        initialValue = ModifyCollectionState.Loading,
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val formState: StateFlow<ModifyCollectionFormState> = initialCollection.mapLatest { collection ->
        collection?.let { ModifyCollectionFormState.Data(it.collection) }
            ?: ModifyCollectionFormState.NoData
    }.stateIn(
        scope = viewModelScope.plus(workDispatcher.dispatcher),
        started = SharingStarted.Eagerly,
        initialValue = ModifyCollectionFormState.NoData,
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val covers: StateFlow<CoverListState> = state.mapLatest { currentState ->
        when (currentState) {
            is ModifyCollectionState.Data -> CoverListState.Data(
                commonCoverUseCase.getAllUniqueCover(currentState.initialCollection.musics.map { it.cover })
            )
            ModifyCollectionState.Loading -> CoverListState.Loading
        }
    }.stateIn(
        scope = viewModelScope.plus(workDispatcher.dispatcher),
        started = SharingStarted.Eagerly,
        initialValue = CoverListState.Loading,
    )

    fun showCoversBottomSheet(pos: Int) {
        bottomSheet.value = EditableElementCoversBottomSheet(
            title = { strings.coversOfTheCollection },
            coverStateFlow = covers,
            onCoverSelected = { cover ->
                viewModelScope.launch {
                    setNewCover(
                        bytes = cover,
                        pos = pos,
                    )
                }
            },
            onCoverFromStorageSelected = { imageFile ->
                viewModelScope.launch {
                    setNewCover(
                        bytes = imageFile.readBytes(),
                        pos = pos,
                    )
                }
            },
            onClose = { bottomSheet.value = null },
        )
    }

    private fun setNewCover(
        bytes: ByteArray,
        pos: Int,
    ) {
        val selectedCoverModeType = (state.value as? ModifyCollectionState.Data)?.coverEditMode?.selectedType ?: return
        when (selectedCoverModeType) {
            CoverEditMode.Type.Simple -> newSimpleCover.value = bytes
            CoverEditMode.Type.Grid -> newGridCover.value = newGridCover.value.setAt(pos, bytes)
        }
    }

    fun updateCollection() {
        CoroutineScope(workDispatcher.dispatcher).launch {
            val state = state.value as? ModifyCollectionState.Data ?: return@launch
            val form = formState.value as? ModifyCollectionFormState.Data ?: return@launch
            if (!form.isFormValid() || !state.coverEditMode.isValid()) return@launch

            loadingManager.startLoading()
            val newCoverResult = coverEditManager.getUpdatedCover(
                coverEditMode = state.coverEditMode,
            )
            when (newCoverResult) {
                is SoulResult.Error -> {
                    loadingManager.stopLoading()
                    feedbackPopUpManager.showErrorIfAny(newCoverResult)
                }
                is SoulResult.Success -> {
                    commonCollectionUseCase.upsert(
                        state.initialCollection.collection.copy(
                            name = form.getCollectionName().trim(),
                            cover = newCoverResult.data,
                        )
                    )
                    loadingManager.stopLoading()
                    loadingManager.stopLoading()
                    navigation.value = ModifyCollectionNavigationState.Back
                }
            }
        }
    }

    fun consumeNavigation() {
        navigation.value = ModifyCollectionNavigationState.Idle
    }

    fun navigateBack() {
        navigation.value = ModifyCollectionNavigationState.Back
    }

    fun switchCoverEditModeType(
        type: CoverEditMode.Type,
    ) {
        selectedCoverMode.value = type
    }
}
