package com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.domain

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.domain.usecase.cover.UpsertImageCoverUseCase
import com.github.enteraname74.domain.usecase.playlist.GetPlaylistWithMusicsUseCase
import com.github.enteraname74.domain.usecase.playlist.UpsertPlaylistUseCase
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.feature.editableelement.composable.EditableElementCoversBottomSheet
import com.github.enteraname74.soulsearching.feature.editableelement.domain.CoverListState
import com.github.enteraname74.soulsearching.feature.editableelement.domain.EditableElement
import com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.domain.state.ModifyPlaylistFormState
import com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.domain.state.ModifyPlaylistNavigationState
import com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.domain.state.ModifyPlaylistState
import com.github.enteraname74.soulsearching.features.filemanager.cover.CoverRetriever
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import java.util.UUID

class ModifyPlaylistViewModel(
    private val getPlaylistWithMusicsUseCase: GetPlaylistWithMusicsUseCase,
    private val upsertImageCoverUseCase: UpsertImageCoverUseCase,
    private val upsertPlaylistUseCase: UpsertPlaylistUseCase,
    private val loadingManager: LoadingManager,
    private val coverRetriever: CoverRetriever,
) : ScreenModel {

    private val playlistId: MutableStateFlow<UUID?> = MutableStateFlow(null)
    private val _navigationState: MutableStateFlow<ModifyPlaylistNavigationState> = MutableStateFlow(
        ModifyPlaylistNavigationState.Idle
    )
    val navigationState: StateFlow<ModifyPlaylistNavigationState> = _navigationState.asStateFlow()

    private val _bottomSheetState: MutableStateFlow<SoulBottomSheet?> = MutableStateFlow(null)
    val bottomSheetState: StateFlow<SoulBottomSheet?> = _bottomSheetState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val initialPlaylist: Flow<PlaylistWithMusics?> = playlistId.flatMapLatest { id ->
        if (id == null) {
            flowOf(null)
        } else {
            getPlaylistWithMusicsUseCase(playlistId = id)
        }
    }

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
        scope = screenModelScope.plus(Dispatchers.IO),
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
        scope = screenModelScope.plus(Dispatchers.IO),
        started = SharingStarted.Eagerly,
        initialValue = ModifyPlaylistFormState.NoData,
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val artistsCover: StateFlow<CoverListState> = state.mapLatest { state ->
        when (state) {
            is ModifyPlaylistState.Data -> CoverListState.Data(
                covers = coverRetriever.getAllUniqueCover(
                    covers = state.initialPlaylist.musics.map { it.cover }
                )
            )

            ModifyPlaylistState.Loading -> CoverListState.Loading
        }
    }.stateIn(
        scope = screenModelScope.plus(Dispatchers.IO),
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
        CoroutineScope(Dispatchers.IO).launch {
            val state = (state.value as? ModifyPlaylistState.Data) ?: return@launch
            val form = (formState.value as? ModifyPlaylistFormState.Data) ?: return@launch

            if (!form.isFormValid()) return@launch

            loadingManager.startLoading()

            val coverFile: UUID? = state.editableElement.newCover?.let { coverData ->
                val newCoverId: UUID = UUID.randomUUID()
                upsertImageCoverUseCase(
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

            upsertPlaylistUseCase(
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
        screenModelScope.launch {
            newCover.value = imageFile.readBytes()
        }
    }

    fun init(playlistId: UUID) {
        this.playlistId.value = playlistId
    }
}