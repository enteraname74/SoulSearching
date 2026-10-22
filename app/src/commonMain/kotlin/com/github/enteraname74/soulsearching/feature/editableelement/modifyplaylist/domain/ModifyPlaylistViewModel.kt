package com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpManager
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.domain.model.Cover
import com.github.enteraname74.soulsearching.domain.model.PlaylistWithMusics
import com.github.enteraname74.soulsearching.domain.model.SoulResult
import com.github.enteraname74.soulsearching.domain.usecase.cover.CommonCoverUseCase
import com.github.enteraname74.soulsearching.domain.usecase.playlist.CommonPlaylistUseCase
import com.github.enteraname74.soulsearching.domain.util.WorkDispatcher
import com.github.enteraname74.soulsearching.feature.editableelement.composable.EditableElementCoversBottomSheet
import com.github.enteraname74.soulsearching.feature.editableelement.domain.CoverEditManager
import com.github.enteraname74.soulsearching.feature.editableelement.domain.CoverEditMode
import com.github.enteraname74.soulsearching.feature.editableelement.domain.CoverListState
import com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.domain.state.ModifyPlaylistFormState
import com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.domain.state.ModifyPlaylistNavigationState
import com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.domain.state.ModifyPlaylistState
import com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.presentation.ModifyPlaylistDestination
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlin.uuid.Uuid

class ModifyPlaylistViewModel(
    private val commonPlaylistUseCase: CommonPlaylistUseCase,
    private val commonCoverUseCase: CommonCoverUseCase,
    private val loadingManager: LoadingManager,
    private val workDispatcher: WorkDispatcher,
    private val coverEditManager: CoverEditManager,
    private val feedbackPopUpManager: FeedbackPopUpManager,
    destination: ModifyPlaylistDestination,
) : ViewModel() {
    private val playlistId: Uuid = destination.selectedPlaylistId
    private val _navigationState: MutableStateFlow<ModifyPlaylistNavigationState> = MutableStateFlow(
        ModifyPlaylistNavigationState.Idle
    )
    val navigationState: StateFlow<ModifyPlaylistNavigationState> = _navigationState.asStateFlow()

    private val _bottomSheetState: MutableStateFlow<SoulBottomSheet?> = MutableStateFlow(null)
    val bottomSheetState: StateFlow<SoulBottomSheet?> = _bottomSheetState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val initialPlaylist: Flow<PlaylistWithMusics?> = commonPlaylistUseCase
        .getWithMusics(playlistId = playlistId)

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

    val state: StateFlow<ModifyPlaylistState> = combine(
        initialPlaylist,
        newSimpleCover,
        newGridCover,
        selectedCoverMode,
    ) { initialPlaylist, newCover, newGridCover, selectedCoverMode ->
        when {
            initialPlaylist == null -> ModifyPlaylistState.Loading
            else -> ModifyPlaylistState.Data(
                initialPlaylist = initialPlaylist,
                coverEditMode = CoverEditMode(
                    simple = CoverEditMode.Simple(
                        initialCover = (initialPlaylist.cover as? Cover.Simple),
                        newCover = newCover,
                    ),
                    grid = CoverEditMode.Grid(
                        initialCover = (initialPlaylist.cover as? Cover.Grid),
                        newCover = newGridCover,
                    ),
                    selectedType = selectedCoverMode ?: CoverEditMode.Type.fromCover(initialPlaylist.cover),
                ),
            )
        }
    }.stateIn(
        scope = viewModelScope.plus(workDispatcher.dispatcher),
        started = SharingStarted.Eagerly,
        initialValue = ModifyPlaylistState.Loading,
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val formState: StateFlow<ModifyPlaylistFormState> = initialPlaylist.mapLatest { playlistWithMusics ->
        if (playlistWithMusics == null) {
            ModifyPlaylistFormState.NoData
        } else {
            ModifyPlaylistFormState.Data(initialPlaylist = playlistWithMusics.playlist)
        }
    }.stateIn(
        scope = viewModelScope.plus(workDispatcher.dispatcher),
        started = SharingStarted.Eagerly,
        initialValue = ModifyPlaylistFormState.NoData,
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val playlistCovers: StateFlow<CoverListState> = state.mapLatest { state ->
        when (state) {
            is ModifyPlaylistState.Data -> CoverListState.Data(
                covers = commonCoverUseCase.getAllUniqueCover(
                    covers = state.initialPlaylist.musics.map { it.cover }
                )
            )

            ModifyPlaylistState.Loading -> CoverListState.Loading
        }
    }.stateIn(
        scope = viewModelScope.plus(workDispatcher.dispatcher),
        started = SharingStarted.Eagerly,
        initialValue = CoverListState.Loading,
    )

    fun showCoversBottomSheet(pos: Int) {
        _bottomSheetState.value = EditableElementCoversBottomSheet(
            title = { strings.coversOfThePlaylist },
            coverStateFlow = playlistCovers,
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
            onClose = {
                _bottomSheetState.value = null
            }
        )
    }

    /**
     * Update selected playlist information.
     */
    fun updatePlaylist() {
        CoroutineScope(workDispatcher.dispatcher).launch {
            val state = (state.value as? ModifyPlaylistState.Data) ?: return@launch
            val form = (formState.value as? ModifyPlaylistFormState.Data) ?: return@launch

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
                    val newPlaylistInformation = state.initialPlaylist.playlist.copy(
                        cover = newCoverResult.data,
                        name = form.getPlaylistName().trim(),
                    )

                    commonPlaylistUseCase.upsert(
                        playlist = newPlaylistInformation,
                    )
                    loadingManager.stopLoading()
                    _navigationState.value = ModifyPlaylistNavigationState.Back
                }
            }

        }
    }

    fun consumeNavigation() {
        _navigationState.value = ModifyPlaylistNavigationState.Idle
    }

    private fun setNewCover(
        bytes: ByteArray,
        pos: Int,
    ) {
        val selectedCoverModeType = (state.value as? ModifyPlaylistState.Data)?.coverEditMode?.selectedType ?: return
        when (selectedCoverModeType) {
            CoverEditMode.Type.Simple -> newSimpleCover.value = bytes
            CoverEditMode.Type.Grid -> newGridCover.value = newGridCover.value.setAt(pos, bytes)
        }
    }

    fun switchCoverEditModeType(
        type: CoverEditMode.Type,
    ) {
        selectedCoverMode.value = type
    }

    fun navigateBack() {
        _navigationState.value = ModifyPlaylistNavigationState.Back
    }
}
