package com.github.enteraname74.soulsearching.feature.editableelement.modifyartist.domain

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.usecase.artist.GetArtistWithMusicsUseCase
import com.github.enteraname74.domain.usecase.artist.GetArtistsNameFromSearchStringUseCase
import com.github.enteraname74.domain.usecase.cover.UpsertImageCoverUseCase
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.feature.editableelement.composable.EditableElementCoversBottomSheet
import com.github.enteraname74.soulsearching.feature.editableelement.domain.CoverListState
import com.github.enteraname74.soulsearching.feature.editableelement.domain.EditableElement
import com.github.enteraname74.soulsearching.feature.editableelement.modifyartist.domain.state.ModifyArtistFormState
import com.github.enteraname74.soulsearching.feature.editableelement.modifyartist.domain.state.ModifyArtistNavigationState
import com.github.enteraname74.soulsearching.feature.editableelement.modifyartist.domain.state.ModifyArtistState
import com.github.enteraname74.soulsearching.features.filemanager.cover.CoverRetriever
import com.github.enteraname74.soulsearching.features.filemanager.usecase.UpdateArtistUseCase
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*

class ModifyArtistViewModel(
    private val getArtistsNameFromSearchStringUseCase: GetArtistsNameFromSearchStringUseCase,
    private val getArtistWithMusicsUseCase: GetArtistWithMusicsUseCase,
    private val upsertImageCoverUseCase: UpsertImageCoverUseCase,
    private val updateArtistUseCase: UpdateArtistUseCase,
    private val loadingManager: LoadingManager,
    private val playbackManager: PlaybackManager,
    private val coverRetriever: CoverRetriever,
) : ScreenModel {
    private val artistId: MutableStateFlow<UUID?> = MutableStateFlow(null)
    private val newCover: MutableStateFlow<ByteArray?> = MutableStateFlow(null)
    private val _navigationState: MutableStateFlow<ModifyArtistNavigationState> = MutableStateFlow(
        ModifyArtistNavigationState.Idle,
    )
    val navigationState: StateFlow<ModifyArtistNavigationState> = _navigationState.asStateFlow()

    private val _bottomSheetState: MutableStateFlow<SoulBottomSheet?> = MutableStateFlow(null)
    val bottomSheetState: StateFlow<SoulBottomSheet?> = _bottomSheetState.asStateFlow()

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
                    initialCover = initialArtist.cover,
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

    @OptIn(ExperimentalCoroutinesApi::class)
    private val artistsCover: StateFlow<CoverListState> = state.mapLatest { state ->
        when (state) {
            is ModifyArtistState.Data -> CoverListState.Data(
                covers = coverRetriever.getAllUniqueCover(
                    covers = state.initialArtist.musics.map { it.cover }
                )
            )
            ModifyArtistState.Loading -> CoverListState.Loading
        }
    }.stateIn(
        scope = screenModelScope.plus(Dispatchers.IO),
        started = SharingStarted.Eagerly,
        initialValue = CoverListState.Loading,
    )

    fun init(artistId: UUID) {
        this.artistId.value = artistId
    }

    fun consumeNavigation() {
        _navigationState.value = ModifyArtistNavigationState.Idle
    }

    private fun setNewCover(imageFile: PlatformFile) {
        screenModelScope.launch {
            newCover.value = imageFile.readBytes()
        }
    }

    fun showCoversBottomSheet() {
        _bottomSheetState.value = EditableElementCoversBottomSheet(
            title = { strings.coversOfTheArtist },
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
                        upsertImageCoverUseCase(
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

                val newArtistWithMusics: ArtistWithMusics = getArtistWithMusicsUseCase(
                    artistId = newArtistInformation.artist.artistId,
                ).first() ?: return@withLoading

                // We need to update the artist's songs that are in the played list.
                for (music in newArtistWithMusics.musics) playbackManager.updateMusic(music)
            }

            _navigationState.value = ModifyArtistNavigationState.Back
        }
    }
}