package com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.domain

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.PlaylistWithMusics
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.usecase.cover.UpsertImageCoverUseCase
import com.github.enteraname74.domain.usecase.playlist.GetPlaylistWithMusicsUseCase
import com.github.enteraname74.domain.usecase.playlist.UpdatePlaylistUseCase
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpManager
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.feature.editableelement.domain.EditableElement
import com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.domain.state.ModifyPlaylistFormState
import com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.domain.state.ModifyPlaylistNavigationState
import com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.domain.state.ModifyPlaylistState
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*

class ModifyPlaylistViewModel(
    private val getPlaylistWithMusicsUseCase: GetPlaylistWithMusicsUseCase,
    private val upsertImageCoverUseCase: UpsertImageCoverUseCase,
    private val updatePlaylistUseCase: UpdatePlaylistUseCase,
    private val loadingManager: LoadingManager,
    private val feedbackPopUpManager: FeedbackPopUpManager,
) : ScreenModel {

    private val playlistId: MutableStateFlow<UUID?> = MutableStateFlow(null)
    private val _navigationState: MutableStateFlow<ModifyPlaylistNavigationState> = MutableStateFlow(
        ModifyPlaylistNavigationState.Idle
    )
    val navigationState: StateFlow<ModifyPlaylistNavigationState> = _navigationState.asStateFlow()

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
                initialPlaylist = initialPlaylist.playlist,
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

    /**
     * Update selected playlist information.
     */
    fun updatePlaylist() {
        CoroutineScope(Dispatchers.IO).launch {
            val state = (state.value as? ModifyPlaylistState.Data) ?: return@launch
            val form = (formState.value as? ModifyPlaylistFormState.Data) ?: return@launch

            if (!form.isFormValid()) return@launch

            loadingManager.withLoading {
                val coverFile: UUID? = state.editableElement.newCover?.let { coverData ->
                    val newCoverId: UUID = UUID.randomUUID()
                    upsertImageCoverUseCase(
                        id = newCoverId,
                        data = coverData,
                    )
                    newCoverId
                } ?: (state.initialPlaylist.cover as? Cover.CoverFile)?.fileCoverId

                val newPlaylistInformation = state.initialPlaylist.copy(
                    cover = (state.initialPlaylist.cover as? Cover.CoverFile)?.copy(
                        fileCoverId = coverFile,
                    ) ?: coverFile?.let { Cover.CoverFile(fileCoverId = it) },
                    name = form.getPlaylistName().trim(),
                )

                val result: SoulResult<Unit> = updatePlaylistUseCase(
                    playlist = newPlaylistInformation,
                )
                feedbackPopUpManager.showResultErrorIfAny(result)
            }

            _navigationState.value = ModifyPlaylistNavigationState.Back
        }
    }

    fun consumeNavigation() {
        _navigationState.value = ModifyPlaylistNavigationState.Idle
    }

    fun setNewCover(imageFile: PlatformFile) {
        screenModelScope.launch {
            newCover.value = imageFile.readBytes()
        }
    }

    fun init(playlistId: UUID) {
        this.playlistId.value = playlistId
    }
}