package com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.domain.usecase.cover.CommonCoverUseCase
import com.github.enteraname74.domain.usecase.playlist.CommonPlaylistUseCase
import com.github.enteraname74.domain.util.WorkDispatcher
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.feature.editableelement.composable.EditableElementCoversBottomSheet
import com.github.enteraname74.soulsearching.feature.editableelement.domain.CoverListState
import com.github.enteraname74.soulsearching.feature.editableelement.domain.EditableElement
import com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.domain.state.ModifyPlaylistFormState
import com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.domain.state.ModifyPlaylistNavigationState
import com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.domain.state.ModifyPlaylistState
import com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.presentation.ModifyPlaylistDestination
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

    private val newCover: MutableStateFlow<ByteArray?> = MutableStateFlow(null)

    val state: StateFlow<ModifyPlaylistState> = combine(
        initialPlaylist,
        newCover,
    ) { initialPlaylist, newCover ->
        when {
            initialPlaylist == null -> ModifyPlaylistState.Loading
            else -> ModifyPlaylistState.Data(
                initialPlaylist = initialPlaylist,
                editableElement = EditableElement(
                    initialCover = initialPlaylist.cover,
                    newCover = newCover,
                )
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
    private val artistsCover: StateFlow<CoverListState> = state.mapLatest { state ->
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

    fun showCoversBottomSheet() {
        _bottomSheetState.value = EditableElementCoversBottomSheet(
            title = { strings.coversOfThePlaylist },
            coverStateFlow = artistsCover,
            onCoverSelected = { cover ->
                newCover.value = cover
            },
            onCoverFromStorageSelected = { imageFile ->
                setNewCover(imageFile = imageFile)
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

            if (!form.isFormValid()) return@launch

            loadingManager.startLoading()

            val coverFile: Uuid? = state.editableElement.newCover?.let { coverData ->
                val newCoverId: Uuid = Uuid.random()
                commonCoverUseCase.upsert(
                    id = newCoverId,
                    data = coverData,
                )
                newCoverId
            } ?: (state.initialPlaylist.cover as? Cover.CoverFile)?.fileCoverId

            val newPlaylistInformation = state.initialPlaylist.playlist.copy(
                cover = (state.initialPlaylist.cover as? Cover.CoverFile)?.copy(
                    fileCoverId = coverFile,
                ) ?: coverFile?.let { Cover.CoverFile(fileCoverId = it) },
                name = form.getPlaylistName().trim(),
            )

            commonPlaylistUseCase.upsert(
                playlist = newPlaylistInformation,
            )

            loadingManager.stopLoading()

            _navigationState.value = ModifyPlaylistNavigationState.Back
        }
    }

    fun consumeNavigation() {
        _navigationState.value = ModifyPlaylistNavigationState.Idle
    }

    private fun setNewCover(imageFile: PlatformFile) {
        viewModelScope.launch {
            newCover.value = imageFile.readBytes()
        }
    }

    fun navigateBack() {
        _navigationState.value = ModifyPlaylistNavigationState.Back
    }
}
