package com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.domain

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.usecase.album.CommonAlbumUseCase
import com.github.enteraname74.domain.usecase.album.GetCorrespondingAlbumUseCase
import com.github.enteraname74.domain.usecase.artist.CommonArtistUseCase
import com.github.enteraname74.domain.usecase.cover.CommonCoverUseCase
import com.github.enteraname74.domain.usecase.music.CommonMusicUseCase
import com.github.enteraname74.soulsearching.coreui.bottomsheet.SoulBottomSheet
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.ext.toByteArray
import com.github.enteraname74.soulsearching.feature.editableelement.domain.CoverListState
import com.github.enteraname74.soulsearching.feature.editableelement.domain.EditableElement
import com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.domain.state.ModifyMusicFormState
import com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.domain.state.ModifyMusicNavigationState
import com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.domain.state.ModifyMusicState
import com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.presentation.ModifyMusicDestination
import com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.presentation.MusicCoversBottomSheet
import com.github.enteraname74.soulsearching.features.filemanager.cover.CachedCoverManager
import com.github.enteraname74.soulsearching.features.filemanager.cover.CoverFileManager
import com.github.enteraname74.soulsearching.features.filemanager.cover.CoverRetriever
import com.github.enteraname74.soulsearching.features.filemanager.usecase.UpdateMusicUseCase
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.decodeToImageBitmap
import java.util.*

class ModifyMusicViewModel(
    private val playbackManager: PlaybackManager,
    commonMusicUseCase: CommonMusicUseCase,
    private val commonAlbumUseCase: CommonAlbumUseCase,
    private val commonArtistUseCase: CommonArtistUseCase,
    getCorrespondingAlbumUseCase: GetCorrespondingAlbumUseCase,
    private val commonCoverUseCase: CommonCoverUseCase,
    private val updateMusicUseCase: UpdateMusicUseCase,
    private val loadingManager: LoadingManager,
    private val cachedCoverManager: CachedCoverManager,
    private val coverFileManager: CoverFileManager,
    private val coverRetriever: CoverRetriever,
    destination: ModifyMusicDestination,
) : ViewModel() {
    private val musicId: UUID = destination.selectedMusicId
    private val deletedArtistIds: MutableStateFlow<List<UUID>> = MutableStateFlow(emptyList())
    private val newCover: MutableStateFlow<ByteArray?> = MutableStateFlow(null)
    private val _navigationState: MutableStateFlow<ModifyMusicNavigationState> = MutableStateFlow(
        ModifyMusicNavigationState.Idle
    )
    val navigationState: StateFlow<ModifyMusicNavigationState> = _navigationState.asStateFlow()

    private val _bottomSheetState: MutableStateFlow<SoulBottomSheet?> = MutableStateFlow(null)
    val bottomSheetState: StateFlow<SoulBottomSheet?> = _bottomSheetState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val initialMusic: Flow<Music?> = commonMusicUseCase.getFromId(musicId = musicId)

    val state: StateFlow<ModifyMusicState> = combine(
        initialMusic,
        newCover
    ) { initialMusic, newCover ->
        when {
            initialMusic == null -> ModifyMusicState.Loading
            else -> ModifyMusicState.Data(
                initialMusic = initialMusic,
                editableElement = EditableElement(
                    initialCover = initialMusic.cover,
                    newCover = newCover
                )
            )
        }
    }.stateIn(
        scope = viewModelScope.plus(Dispatchers.IO),
        started = SharingStarted.Eagerly,
        initialValue = ModifyMusicState.Loading,
    )

    private val savedData: HashMap<String, String> = hashMapOf()
    private var addedArtists: MutableStateFlow<List<Artist>> = MutableStateFlow(emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val formState: StateFlow<ModifyMusicFormState> = initialMusic.flatMapLatest { music ->
        if (music == null) {
            flowOf(ModifyMusicFormState.NoData)
        } else {
            addedArtists.flatMapLatest { addedArtists ->
                deletedArtistIds.mapLatest { ids ->
                    ModifyMusicFormState.Data(
                        initialMusic = music,
                        updateFoundAlbums = { commonAlbumUseCase.getAlbumsNameFromSearch(it) },
                        updateFoundArtists = { commonArtistUseCase.getArtistsNameFromSearch(it) },
                        artistsOfMusic = music.artists
                            .plus(addedArtists)
                            .filter { it.artistId !in ids },
                        onDeleteArtist = { artistId ->
                            deletedArtistIds.value = deletedArtistIds.value.plus(artistId)
                        },
                        savedData = savedData,
                        onFieldChange = { id, value ->
                            savedData[id] = value
                        }
                    )
                }
            }
        }
    }.stateIn(
        scope = viewModelScope.plus(Dispatchers.IO),
        started = SharingStarted.Eagerly,
        initialValue = ModifyMusicFormState.NoData,
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val coversOfAlbum: StateFlow<CoverListState> =
        getCorrespondingAlbumUseCase.withMusics(musicId = musicId).mapLatest { album ->
            CoverListState.Data(
                covers = album?.let {
                    coverRetriever.getAllUniqueCover(
                        covers = it.musics.map { it.cover }
                    )
                } ?: emptyList()
            )
        }.stateIn(
            scope = viewModelScope.plus(Dispatchers.IO),
            started = SharingStarted.Eagerly,
            initialValue = CoverListState.Loading,
        )

    fun consumeNavigation() {
        _navigationState.value = ModifyMusicNavigationState.Idle
    }

    private fun setNewCoverFromStorage(imageFile: PlatformFile) {
        viewModelScope.launch {
            newCover.value = imageFile.readBytes()
        }
    }

    private fun setNewCoverFromPath(musicPath: String) {
        CoroutineScope(Dispatchers.IO).launch {
            loadingManager.withLoading {
                val coverImage: ImageBitmap? =
                    cachedCoverManager.getCachedImage(musicPath) ?: cachedCoverManager.fetchCoverOfMusicFile(musicPath)
                newCover.value = coverImage?.toByteArray()
            }
        }
    }

    private fun setNewCoverFromCoverId(coverId: UUID) {
        CoroutineScope(Dispatchers.IO).launch {
            loadingManager.withLoading {
                newCover.value = coverFileManager.getCoverData(coverId)
            }
        }
    }

    fun addArtistField() {
        addedArtists.value = addedArtists.value.plus(Artist(artistName = ""))
    }

    fun showCoverBottomSheet() {
        (state.value as? ModifyMusicState.Data)?.let { dataState ->
            _bottomSheetState.value = MusicCoversBottomSheet(
                musicCover = dataState.initialMusic.cover,
                onMusicFileCoverSelected = ::setNewCoverFromPath,
                onFileCoverSelected = ::setNewCoverFromCoverId,
                onCoverFromStorageSelected = ::setNewCoverFromStorage,
                onClose = {
                    _bottomSheetState.value = null
                },
                albumCoversStateFlow = coversOfAlbum,
                onAlbumCoverSelected = { cover ->
                    newCover.value = cover
                }
            )
        }
    }

    /**
     * Update selected music information.
     */
    @OptIn(ExperimentalResourceApi::class)
    fun updateMusic() {
        CoroutineScope(Dispatchers.IO).launch {

            val state = (state.value as? ModifyMusicState.Data) ?: return@launch
            val form = (formState.value as? ModifyMusicFormState.Data) ?: return@launch

            if (!form.isFormValid()) return@launch

            loadingManager.startLoading()

            val coverFile: UUID? = state.editableElement.newCover?.let { coverData ->
                val newCoverId: UUID = UUID.randomUUID()
                commonCoverUseCase.upsert(
                    id = newCoverId,
                    data = coverData,
                )
                newCoverId
            } ?: (state.initialMusic.cover as? Cover.CoverFile)?.fileCoverId

            // We remove duplicate and we trim the inputs
            val cleanedNewArtistsName: List<String> = form.getArtistsName()
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .distinct()

            val updatedMusic = updateMusicUseCase(
                updateInformation = UpdateMusicUseCase.UpdateInformation(
                    legacyMusic = state.initialMusic,
                    newName = form.getMusicName().trim(),
                    newAlbumName = form.getAlbumName().trim(),
                    newCover = (state.initialMusic.cover as? Cover.CoverFile)?.copy(
                        fileCoverId = coverFile,
                    ) ?: state.initialMusic.cover,
                    newAlbumPosition = form.getAlbumPosition().trim().toIntOrNull(),
                    newAlbumArtistName = form.getAlbumArtist().trim(),
                    newArtistsNames = cleanedNewArtistsName,
                )
            )

            if (
                playbackManager.isSameMusicAsCurrentPlayedOne(musicId = updatedMusic.musicId)
                && state.editableElement.newCover != null
            ) {
                playbackManager.updateCover(
                    cover = runCatching {
                        state.editableElement.newCover.decodeToImageBitmap()
                    }.getOrNull()
                )
            }

            loadingManager.stopLoading()

            _navigationState.value = ModifyMusicNavigationState.Back
        }
    }

    fun navigateBack() {
        _navigationState.value = ModifyMusicNavigationState.Back
    }
}