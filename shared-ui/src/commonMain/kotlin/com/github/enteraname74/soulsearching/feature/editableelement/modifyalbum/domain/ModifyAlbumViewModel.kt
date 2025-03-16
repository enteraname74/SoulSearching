package com.github.enteraname74.soulsearching.feature.editableelement.modifyalbum.domain

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.AlbumWithArtist
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.SoulResult
import com.github.enteraname74.domain.usecase.album.GetAlbumWithMusicsUseCase
import com.github.enteraname74.domain.usecase.album.GetAlbumsNameFromSearchStringUseCase
import com.github.enteraname74.domain.usecase.artist.GetArtistsNameFromSearchStringUseCase
import com.github.enteraname74.domain.usecase.cover.UpsertImageCoverUseCase
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpManager
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.feature.editableelement.composable.EditableElementCoversBottomSheet
import com.github.enteraname74.soulsearching.feature.editableelement.domain.CoverListState
import com.github.enteraname74.soulsearching.feature.editableelement.domain.EditableElement
import com.github.enteraname74.soulsearching.feature.editableelement.modifyalbum.domain.state.ModifyAlbumFormState
import com.github.enteraname74.soulsearching.feature.editableelement.modifyalbum.domain.state.ModifyAlbumNavigationState
import com.github.enteraname74.soulsearching.feature.editableelement.modifyalbum.domain.state.ModifyAlbumState
import com.github.enteraname74.soulsearching.features.filemanager.cover.CoverRetriever
import com.github.enteraname74.soulsearching.features.filemanager.usecase.UpdateAlbumUseCase
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*

class ModifyAlbumViewModel(
    private val getAlbumsNameFromSearchStringUseCase: GetAlbumsNameFromSearchStringUseCase,
    private val getArtistsNameFromSearchStringUseCase: GetArtistsNameFromSearchStringUseCase,
    private val getAlbumWithMusicsUseCase: GetAlbumWithMusicsUseCase,
    private val upsertImageCoverUseCase: UpsertImageCoverUseCase,
    private val updateAlbumUseCase: UpdateAlbumUseCase,
    private val playbackManager: PlaybackManager,
    private val loadingManager: LoadingManager,
    private val feedbackPopUpManager: FeedbackPopUpManager,
    private val coverRetriever: CoverRetriever,
) : ScreenModel {
    private val albumId: MutableStateFlow<UUID?> = MutableStateFlow(null)
    private val newCover: MutableStateFlow<ByteArray?> = MutableStateFlow(null)
    private val _navigationState: MutableStateFlow<ModifyAlbumNavigationState> = MutableStateFlow(
        ModifyAlbumNavigationState.Idle,
    )
    val navigationState: StateFlow<ModifyAlbumNavigationState> = _navigationState.asStateFlow()

    private val _bottomSheetState: MutableStateFlow<SoulBottomSheet?> = MutableStateFlow(null)
    val bottomSheetState: StateFlow<SoulBottomSheet?> = _bottomSheetState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val initialAlbum: Flow<AlbumWithMusics?> = albumId.flatMapLatest { id ->
        if (id == null) {
            flowOf(null)
        } else {
            getAlbumWithMusicsUseCase(albumId = id)
        }
    }

    val state: StateFlow<ModifyAlbumState> = combine(
        initialAlbum,
        newCover
    ) { initialAlbum, newCover ->
        when {
            initialAlbum == null -> ModifyAlbumState.Loading
            else -> ModifyAlbumState.Data(
                initialAlbum = initialAlbum,
                editableElement = EditableElement(
                    initialCover = initialAlbum.cover,
                    newCover = newCover,
                )
            )
        }
    }.stateIn(
        scope = screenModelScope.plus(Dispatchers.IO),
        started = SharingStarted.Eagerly,
        initialValue = ModifyAlbumState.Loading,
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val formState: StateFlow<ModifyAlbumFormState> = initialAlbum.mapLatest { album ->
        if (album == null) {
            ModifyAlbumFormState.NoData
        } else {
            ModifyAlbumFormState.Data(
                initialAlbum = album,
                updateFoundAlbums = { getAlbumsNameFromSearchStringUseCase(it) },
                updateFoundArtists = { getArtistsNameFromSearchStringUseCase(it) },
            )
        }
    }.stateIn(
        scope = screenModelScope.plus(Dispatchers.IO),
        started = SharingStarted.Eagerly,
        initialValue = ModifyAlbumFormState.NoData,
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val artistsCover: StateFlow<CoverListState> = state.mapLatest { state ->
        when (state) {
            is ModifyAlbumState.Data -> CoverListState.Data(
                covers = coverRetriever.getAllUniqueCover(
                    covers = state.initialAlbum.musics.map { it.cover }
                )
            )

            ModifyAlbumState.Loading -> CoverListState.Loading
        }
    }.stateIn(
        scope = screenModelScope.plus(Dispatchers.IO),
        started = SharingStarted.Eagerly,
        initialValue = CoverListState.Loading,
    )

    /**
     * Set the selected album for the modify screen information.
     */
    fun init(albumId: UUID) {
        this.albumId.value = albumId
    }

    /**
     * Set the new cover name to show to the user.
     */
    private fun setNewCover(imageFile: PlatformFile) {
        screenModelScope.launch {
            newCover.value = imageFile.readBytes()
        }
    }

    fun consumeNavigation() {
        _navigationState.value = ModifyAlbumNavigationState.Back
    }

    fun showCoversBottomSheet() {
        _bottomSheetState.value = EditableElementCoversBottomSheet(
            title = { strings.coversOfTheAlbum },
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
     * Update the information of the selected album.
     */
    fun updateAlbum() {
        val state = (state.value as? ModifyAlbumState.Data) ?: return
        val form = (formState.value as? ModifyAlbumFormState.Data)?.takeIf { it.isFormValid() } ?: return

        loadingManager.withLoadingOnIO {
            // If the image has changed, we need to save it and retrieve its id.
            val coverFile: UUID? = state.editableElement.newCover?.let { coverData ->
                val newCoverId: UUID = UUID.randomUUID()

                upsertImageCoverUseCase(
                    id = newCoverId,
                    data = coverData,
                )
                newCoverId
            } ?: (state.initialAlbum.album.cover as? Cover.CoverFile)?.fileCoverId

            val albumWithArtist: AlbumWithArtist = state.initialAlbum.toAlbumWithArtist().copy(
                album = state.initialAlbum.album.copy(
                    albumName = form.getAlbumName().trim(),
                ),
                artist = state.initialAlbum.artist?.copy(
                    artistName = form.getArtistName().trim(),
                ),
            )
            val newAlbumWithArtistInformation: AlbumWithArtist = albumWithArtist.copy(
                album = albumWithArtist.album.copy(
                    cover = (albumWithArtist.album.cover as? Cover.CoverFile)?.copy(
                        fileCoverId = coverFile,
                    ) ?: coverFile?.let { Cover.CoverFile(fileCoverId = it) }
                )
            )

            // We update the information of the album.
            val result: SoulResult<Unit> =
                updateAlbumUseCase(newAlbumWithArtistInformation = newAlbumWithArtistInformation)
            feedbackPopUpManager.showResultErrorIfAny(result)

            // We retrieve the updated album
            val newAlbumWithMusics: AlbumWithMusics = getAlbumWithMusicsUseCase(
                albumId = newAlbumWithArtistInformation.album.albumId
            ).first() ?: return@withLoadingOnIO

            // We need to update the album's songs that are in the played list.
            for (music in newAlbumWithMusics.musics) playbackManager.updateMusic(music)

            _navigationState.value = ModifyAlbumNavigationState.Back

        }
    }
}