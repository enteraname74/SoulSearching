package com.github.enteraname74.soulsearching.feature.editableelement.modifycollection.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.domain.model.CollectionWithMusics
import com.github.enteraname74.soulsearching.domain.model.Cover
import com.github.enteraname74.soulsearching.domain.usecase.collection.CommonCollectionUseCase
import com.github.enteraname74.soulsearching.domain.usecase.cover.CommonCoverUseCase
import com.github.enteraname74.soulsearching.domain.util.WorkDispatcher
import com.github.enteraname74.soulsearching.feature.editableelement.composable.EditableElementCoversBottomSheet
import com.github.enteraname74.soulsearching.feature.editableelement.domain.CoverListState
import com.github.enteraname74.soulsearching.feature.editableelement.domain.EditableElement
import com.github.enteraname74.soulsearching.feature.editableelement.modifycollection.domain.state.ModifyCollectionFormState
import com.github.enteraname74.soulsearching.feature.editableelement.modifycollection.domain.state.ModifyCollectionNavigationState
import com.github.enteraname74.soulsearching.feature.editableelement.modifycollection.domain.state.ModifyCollectionState
import com.github.enteraname74.soulsearching.feature.editableelement.modifycollection.presentation.ModifyCollectionDestination
import io.github.vinceglb.filekit.PlatformFile
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
import kotlin.uuid.Uuid

class ModifyCollectionViewModel(
    private val commonCollectionUseCase: CommonCollectionUseCase,
    private val commonCoverUseCase: CommonCoverUseCase,
    private val loadingManager: LoadingManager,
    private val workDispatcher: WorkDispatcher,
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
    private val newCover = MutableStateFlow<ByteArray?>(null)

    val state: StateFlow<ModifyCollectionState> = combine(initialCollection, newCover) { collection, cover ->
        if (collection == null) {
            ModifyCollectionState.Loading
        } else {
            ModifyCollectionState.Data(
                initialCollection = collection,
                editableElement = EditableElement(initialCover = collection.cover, newCover = cover),
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

    fun showCoversBottomSheet() {
        bottomSheet.value = EditableElementCoversBottomSheet(
            title = { strings.coversOfTheCollection },
            coverStateFlow = covers,
            onCoverSelected = { newCover.value = it },
            onCoverFromStorageSelected = ::setNewCover,
            onClose = { bottomSheet.value = null },
        )
    }

    fun updateCollection() {
        CoroutineScope(workDispatcher.dispatcher).launch {
            val currentState = state.value as? ModifyCollectionState.Data ?: return@launch
            val form = formState.value as? ModifyCollectionFormState.Data ?: return@launch
            if (!form.isFormValid()) return@launch

            loadingManager.startLoading()
            val cover = currentState.editableElement.newCover?.let { data ->
                val id = Uuid.random()
                commonCoverUseCase.upsert(id = id, data = data)
                Cover.CoverFile(fileCoverId = id)
            } ?: currentState.initialCollection.collection.cover

            commonCollectionUseCase.upsert(
                currentState.initialCollection.collection.copy(
                    name = form.getCollectionName().trim(),
                    cover = cover,
                )
            )
            loadingManager.stopLoading()
            navigation.value = ModifyCollectionNavigationState.Back
        }
    }

    fun consumeNavigation() {
        navigation.value = ModifyCollectionNavigationState.Idle
    }

    fun navigateBack() {
        navigation.value = ModifyCollectionNavigationState.Back
    }

    private fun setNewCover(file: PlatformFile) {
        viewModelScope.launch { newCover.value = file.readBytes() }
    }
}
