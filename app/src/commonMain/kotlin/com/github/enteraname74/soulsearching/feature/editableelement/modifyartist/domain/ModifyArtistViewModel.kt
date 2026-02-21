package com.github.enteraname74.soulsearching.feature.editableelement.modifyartist.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.usecase.artist.CommonArtistUseCase
import com.github.enteraname74.domain.usecase.cover.CommonCoverUseCase
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.feature.editableelement.composable.EditableElementCoversBottomSheet
import com.github.enteraname74.soulsearching.feature.editableelement.domain.CoverListState
import com.github.enteraname74.soulsearching.feature.editableelement.domain.EditableElement
import com.github.enteraname74.soulsearching.feature.editableelement.modifyartist.domain.state.ModifyArtistFormState
import com.github.enteraname74.soulsearching.feature.editableelement.modifyartist.domain.state.ModifyArtistNavigationState
import com.github.enteraname74.soulsearching.feature.editableelement.modifyartist.domain.state.ModifyArtistState
import com.github.enteraname74.soulsearching.feature.editableelement.modifyartist.presentation.ModifyArtistDestination
import com.github.enteraname74.soulsearching.features.filemanager.cover.CoverRetriever
import com.github.enteraname74.soulsearching.features.filemanager.usecase.UpdateArtistUseCase
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
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import java.util.UUID

class ModifyArtistViewModel(
    private val commonArtistUseCase: CommonArtistUseCase,
    private val commonCoverUseCase: CommonCoverUseCase,
    private val updateArtistUseCase: UpdateArtistUseCase,
    private val loadingManager: LoadingManager,
    private val coverRetriever: CoverRetriever,
    destination: ModifyArtistDestination,
) : ViewModel() {
    private val artistId: UUID = destination.selectedArtistId
    private val newCover: MutableStateFlow<ByteArray?> = MutableStateFlow(null)
    private val _navigationState: MutableStateFlow<ModifyArtistNavigationState> = MutableStateFlow(
        ModifyArtistNavigationState.Idle,
    )
    val navigationState: StateFlow<ModifyArtistNavigationState> = _navigationState.asStateFlow()

    private val _bottomSheetState: MutableStateFlow<SoulBottomSheet?> = MutableStateFlow(null)
    val bottomSheetState: StateFlow<SoulBottomSheet?> = _bottomSheetState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val initialArtist: Flow<ArtistWithMusics?> = commonArtistUseCase
        .getArtistWithMusic(artistId = artistId)

    val state: StateFlow<ModifyArtistState> = combine(
        initialArtist,
        newCover
    ) { initialArtist, newCover ->
        when {
            initialArtist == null -> ModifyArtistState.Loading
            else -> ModifyArtistState.Data(
                initialArtist = initialArtist,
                editableElement = EditableElement(
                    initialCover = initialArtist.cover,
                    newCover = newCover,
                )
            )
        }
    }.stateIn(
        scope = viewModelScope.plus(Dispatchers.IO),
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
                updateFoundArtists = { commonArtistUseCase.getArtistsNameFromSearch(it) }
            )
        }
    }.stateIn(
        scope = viewModelScope.plus(Dispatchers.IO),
        started = SharingStarted.Eagerly,
        initialValue = ModifyArtistFormState.NoData,
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val artistAllMusicCovers: StateFlow<CoverListState> = state.mapLatest { state ->
        when (state) {
            is ModifyArtistState.Data -> CoverListState.Data(
                covers = coverRetriever.getAllUniqueCover(
                    covers = state.initialArtist.musics.map { it.cover }
                )
            )

            ModifyArtistState.Loading -> CoverListState.Loading
        }
    }.stateIn(
        scope = viewModelScope.plus(Dispatchers.IO),
        started = SharingStarted.Eagerly,
        initialValue = CoverListState.Loading,
    )

    fun consumeNavigation() {
        _navigationState.value = ModifyArtistNavigationState.Idle
    }

    private fun setNewCover(imageFile: PlatformFile) {
        viewModelScope.launch {
            newCover.value = imageFile.readBytes()
        }
    }

    fun showCoversBottomSheet() {
        _bottomSheetState.value = EditableElementCoversBottomSheet(
            title = { strings.coversOfTheArtist },
            coverStateFlow = artistAllMusicCovers,
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
     * Update the artist information.
     */
    fun updateArtist() {
        CoroutineScope(Dispatchers.IO).launch {

            val state = (state.value as? ModifyArtistState.Data) ?: return@launch
            val form = (formState.value as? ModifyArtistFormState.Data)?.takeIf { it.isFormValid() } ?: return@launch

            loadingManager.withLoading {
                val coverFile: UUID? =
                    state.editableElement.newCover?.let { coverData ->
                        val newCoverId: UUID = UUID.randomUUID()
                        commonCoverUseCase.upsert(
                            id = newCoverId,
                            data = coverData,
                        )
                        newCoverId
                    } ?: (state.initialArtist.artist.cover as? Cover.CoverFile)?.fileCoverId

                val newArtistInformation = state.initialArtist.copy(
                    artist = state.initialArtist.artist.copy(
                        cover = (state.initialArtist.artist.cover as? Cover.CoverFile)?.copy(
                            fileCoverId = coverFile
                        ) ?: coverFile?.let { Cover.CoverFile(fileCoverId = it) },
                        artistName = form.getArtistName().trim(),
                    )
                )

                updateArtistUseCase(newArtistWithMusicsInformation = newArtistInformation)
            }

            _navigationState.value = ModifyArtistNavigationState.Back
        }
    }

    fun navigateBack() {
        _navigationState.value = ModifyArtistNavigationState.Back
    }
}