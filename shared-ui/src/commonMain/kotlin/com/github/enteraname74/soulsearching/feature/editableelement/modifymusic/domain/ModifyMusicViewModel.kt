package com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.domain

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.usecase.album.GetAlbumsNameFromSearchStringUseCase
import com.github.enteraname74.domain.usecase.artist.GetArtistsNameFromSearchStringUseCase
import com.github.enteraname74.domain.usecase.artist.GetArtistsOfMusicUseCase
import com.github.enteraname74.domain.usecase.cover.UpsertImageCoverUseCase
import com.github.enteraname74.domain.usecase.music.GetMusicUseCase
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.feature.editableelement.domain.EditableElement
import com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.domain.state.ModifyMusicFormState
import com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.domain.state.ModifyMusicNavigationState
import com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.domain.state.ModifyMusicState
import com.github.enteraname74.soulsearching.features.filemanager.usecase.UpdateMusicUseCase
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.decodeToImageBitmap
import java.util.*
import kotlin.collections.HashMap

class ModifyMusicViewModel(
    private val playbackManager: PlaybackManager,
    private val getMusicUseCase: GetMusicUseCase,
    private val getAlbumsNameFromSearchStringUseCase: GetAlbumsNameFromSearchStringUseCase,
    private val getArtistsNameFromSearchStringUseCase: GetArtistsNameFromSearchStringUseCase,
    private val getArtistsOfMusicUseCase: GetArtistsOfMusicUseCase,
    private val upsertImageCoverUseCase: UpsertImageCoverUseCase,
    private val updateMusicUseCase: UpdateMusicUseCase,
    private val loadingManager: LoadingManager,
) : ScreenModel {
    private val musicId: MutableStateFlow<UUID?> = MutableStateFlow(null)
    private val deletedArtistIds: MutableStateFlow<List<UUID>> = MutableStateFlow(emptyList())
    private val newCover: MutableStateFlow<ByteArray?> = MutableStateFlow(null)
    private val _navigationState: MutableStateFlow<ModifyMusicNavigationState> = MutableStateFlow(
        ModifyMusicNavigationState.Idle
    )
    val navigationState: StateFlow<ModifyMusicNavigationState> = _navigationState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val initialMusic: Flow<Music?> = musicId.flatMapLatest { id ->
        if (id == null) {
            flowOf(null)
        } else {
            getMusicUseCase(musicId = id)
        }
    }

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
        scope = screenModelScope.plus(Dispatchers.IO),
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
            getArtistsOfMusicUseCase(music.musicId).flatMapLatest { artists ->
                addedArtists.flatMapLatest { addedArtists ->
                    deletedArtistIds.mapLatest { ids ->
                        ModifyMusicFormState.Data(
                            initialMusic = music,
                            updateFoundAlbums = { getAlbumsNameFromSearchStringUseCase(it) },
                            updateFoundArtists = { getArtistsNameFromSearchStringUseCase(it) },
                            artistsOfMusic = artists.plus(addedArtists).filter { it.artistId !in ids },
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
        }
    }.stateIn(
        scope = screenModelScope.plus(Dispatchers.IO),
        started = SharingStarted.Eagerly,
        initialValue = ModifyMusicFormState.NoData,
    )

    /**
     * Define the selected music in the state from a given id.
     * It will also fetch the cover of the music.
     */
    fun init(musicId: UUID) {
        this.musicId.value = musicId
    }

    fun consumeNavigation() {
        _navigationState.value = ModifyMusicNavigationState.Idle
    }

    /**
     * Set the cover of the modified music.
     */
    fun setNewCover(imageFile: PlatformFile) {
        screenModelScope.launch {
            newCover.value = imageFile.readBytes()
        }
    }

    fun addArtistField() {
        addedArtists.value = addedArtists.value.plus(Artist(artistName = ""))
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
                upsertImageCoverUseCase(
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

            val newMusicInformation = state.initialMusic.copy(
                cover = (state.initialMusic.cover as? Cover.CoverFile)?.copy(
                    fileCoverId = coverFile,
                ) ?: state.initialMusic.cover,
                name = form.getMusicName().trim(),
                album = form.getAlbumName().trim(),
                artist = cleanedNewArtistsName.joinToString(separator = ", "),
            )

            updateMusicUseCase(
                legacyMusic = state.initialMusic,
                newMusicInformation = newMusicInformation,
                previousArtists = getArtistsOfMusicUseCase(state.initialMusic.musicId).firstOrNull() ?: emptyList(),
                newArtistsNames = cleanedNewArtistsName,
            )

            playbackManager.updateMusic(newMusicInformation)
            if (
                playbackManager.isSameMusicAsCurrentPlayedOne(musicId = newMusicInformation.musicId)
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
}