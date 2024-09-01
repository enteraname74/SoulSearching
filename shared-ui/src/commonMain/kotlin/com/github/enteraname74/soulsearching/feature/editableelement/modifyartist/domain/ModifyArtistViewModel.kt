package com.github.enteraname74.soulsearching.feature.editableelement.modifyartist.domain

import androidx.compose.ui.graphics.ImageBitmap
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.ext.toImageBitmap
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.model.ImageCover
import com.github.enteraname74.domain.usecase.artist.GetArtistWithMusicsUseCase
import com.github.enteraname74.domain.usecase.artist.GetArtistsNameFromSearchStringUseCase
import com.github.enteraname74.domain.usecase.artist.UpdateArtistUseCase
import com.github.enteraname74.domain.usecase.imagecover.UpsertImageCoverUseCase
import com.github.enteraname74.soulsearching.feature.editableelement.domain.EditableElement
import com.github.enteraname74.soulsearching.feature.editableelement.modifyartist.domain.state.ModifyArtistFormState
import com.github.enteraname74.soulsearching.feature.editableelement.modifyartist.domain.state.ModifyArtistNavigationState
import com.github.enteraname74.soulsearching.feature.editableelement.modifyartist.domain.state.ModifyArtistState
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*

class ModifyArtistViewModel(
    private val getArtistsNameFromSearchStringUseCase: GetArtistsNameFromSearchStringUseCase,
    private val getArtistWithMusicsUseCase: GetArtistWithMusicsUseCase,
    private val upsertImageCoverUseCase: UpsertImageCoverUseCase,
    private val updateArtistUseCase: UpdateArtistUseCase,
) : ScreenModel {
    private val artistId: MutableStateFlow<UUID?> = MutableStateFlow(null)
    private val newCover: MutableStateFlow<ImageBitmap?> = MutableStateFlow(null)
    private val _navigationState: MutableStateFlow<ModifyArtistNavigationState> = MutableStateFlow(
        ModifyArtistNavigationState.Idle,
    )
    val navigationState: StateFlow<ModifyArtistNavigationState> = _navigationState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val initialArtist: Flow<ArtistWithMusics?> = artistId.flatMapLatest { id ->
        if (id == null) {
            flowOf(null)
        } else {
            getArtistWithMusicsUseCase(artistId = id)
        }
    }

    val state: StateFlow<ModifyArtistState> = combine(
        initialArtist,
        newCover
    ) { initialArtist, newCover ->
        when {
            initialArtist == null -> ModifyArtistState.Loading
            else -> ModifyArtistState.Data(
                initialArtist = initialArtist,
                editableElement = EditableElement(
                    initialCoverId = initialArtist.artist.coverId,
                    newCover = newCover,
                )
            )
        }
    }.stateIn(
        scope = screenModelScope.plus(Dispatchers.IO),
        started = SharingStarted.Eagerly,
        initialValue = ModifyArtistState.Loading,
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val formState: StateFlow<ModifyArtistFormState> = initialArtist.mapLatest { artistWithMusics ->
        if (artistWithMusics == null) {
            ModifyArtistFormState.NoData
        } else {
            ModifyArtistFormState.Data(
                initialArtist = artistWithMusics.artist,
                updateFoundArtists = { getArtistsNameFromSearchStringUseCase(it) }
            )
        }
    }.stateIn(
        scope = screenModelScope.plus(Dispatchers.IO),
        started = SharingStarted.Eagerly,
        initialValue = ModifyArtistFormState.NoData,
    )

    fun init(artistId: UUID) {
        this.artistId.value = artistId
    }

    fun consumeNavigation() {
        _navigationState.value = ModifyArtistNavigationState.Idle
    }

    fun setNewCover(imageFile: PlatformFile) {
        screenModelScope.launch {
            newCover.value = imageFile.readBytes().toImageBitmap()
        }
    }

    /**
     * Update the artist information.
     */
    fun updateArtist() {
        CoroutineScope(Dispatchers.IO).launch {

            val state = (state.value as? ModifyArtistState.Data) ?: return@launch
            val form = (formState.value as? ModifyArtistFormState.Data) ?: return@launch

            if (!form.isFormValid()) return@launch

            val coverId =
                if (state.editableElement.newCover != null) {
                    val imageCover = ImageCover(
                        cover = state.editableElement.newCover,
                    )
                    upsertImageCoverUseCase(imageCover = imageCover)
                    imageCover.coverId
                } else {
                    state.initialArtist.artist.coverId
                }

            val newArtistInformation = state.initialArtist.copy(
                artist = state.initialArtist.artist.copy(
                    coverId = coverId,
                    artistName = form.getArtistName().trim(),
                )
            )

            updateArtistUseCase(newArtistWithMusicsInformation = newArtistInformation)
            _navigationState.value = ModifyArtistNavigationState.Back
        }
    }
}