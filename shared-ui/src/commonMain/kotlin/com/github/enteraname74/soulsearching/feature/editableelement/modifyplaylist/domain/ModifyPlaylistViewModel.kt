package com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.domain

import androidx.compose.ui.graphics.ImageBitmap
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.ext.toImageBitmap
import com.github.enteraname74.domain.model.ImageCover
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.domain.usecase.imagecover.UpsertImageCoverUseCase
import com.github.enteraname74.domain.usecase.playlist.GetPlaylistUseCase
import com.github.enteraname74.domain.usecase.playlist.UpsertPlaylistUseCase
import com.github.enteraname74.soulsearching.feature.editableelement.domain.EditableElement
import com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.domain.state.ModifyPlaylistFormState
import com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.domain.state.ModifyPlaylistNavigationState
import com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.domain.state.ModifyPlaylistState
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*

class ModifyPlaylistViewModel(
    private val getPlaylistUseCase: GetPlaylistUseCase,
    private val upsertImageCoverUseCase: UpsertImageCoverUseCase,
    private val upsertPlaylistUseCase: UpsertPlaylistUseCase,
) : ScreenModel {

    private val playlistId: MutableStateFlow<UUID?> = MutableStateFlow(null)
    private val _navigationState: MutableStateFlow<ModifyPlaylistNavigationState> = MutableStateFlow(
        ModifyPlaylistNavigationState.Idle
    )
    val navigationState: StateFlow<ModifyPlaylistNavigationState> = _navigationState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val initialPlaylist: Flow<Playlist?> = playlistId.flatMapLatest { id ->
        if (id == null) {
            flowOf(null)
        } else {
            getPlaylistUseCase(playlistId = id)
        }
    }

    private val newCover: MutableStateFlow<ImageBitmap?> = MutableStateFlow(null)

    val state: StateFlow<ModifyPlaylistState> = combine(
        initialPlaylist,
        newCover,
    ) { initialPlaylist, newCover ->
        when {
            initialPlaylist == null -> ModifyPlaylistState.Loading
            else -> ModifyPlaylistState.Data(
                initialPlaylist = initialPlaylist,
                editableElement = EditableElement(
                    initialCoverId = initialPlaylist.coverId,
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
    val formState: StateFlow<ModifyPlaylistFormState> = initialPlaylist.mapLatest { playlist ->
        if (playlist == null) {
            ModifyPlaylistFormState.NoData
        } else {
            ModifyPlaylistFormState.Data(initialPlaylist = playlist)
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

            val coverId = if (state.editableElement.newCover != null) {
                val imageCover = ImageCover(
                    cover = state.editableElement.newCover
                )
                upsertImageCoverUseCase(imageCover = imageCover)
                imageCover.coverId
            } else {
                state.initialPlaylist.coverId
            }

            val newPlaylistInformation = state.initialPlaylist.copy(
                coverId = coverId,
                name = form.getPlaylistName().trim(),
            )

            upsertPlaylistUseCase(
                playlist = newPlaylistInformation,
            )

            _navigationState.value = ModifyPlaylistNavigationState.Back
        }
    }

    fun consumeNavigation() {
        _navigationState.value = ModifyPlaylistNavigationState.Idle
    }

    fun setNewCover(imageFile: PlatformFile) {
        screenModelScope.launch {
            newCover.value = imageFile.readBytes().toImageBitmap()
        }
    }

     fun init(playlistId: UUID) {
        this.playlistId.value = playlistId
    }
}