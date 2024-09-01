package com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.domain

import androidx.compose.ui.graphics.ImageBitmap
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.ext.toImageBitmap
import com.github.enteraname74.domain.model.ImageCover
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.usecase.album.GetAlbumsNameFromSearchStringUseCase
import com.github.enteraname74.domain.usecase.artist.GetArtistsNameFromSearchStringUseCase
import com.github.enteraname74.domain.usecase.imagecover.UpsertImageCoverUseCase
import com.github.enteraname74.domain.usecase.music.GetMusicUseCase
import com.github.enteraname74.domain.usecase.music.UpdateMusicUseCase
import com.github.enteraname74.soulsearching.feature.editableelement.domain.EditableElement
import com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.domain.state.ModifyMusicFormState
import com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.domain.state.ModifyMusicNavigationState
import com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.domain.state.ModifyMusicState
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*

class ModifyMusicViewModel(
    private val playbackManager: PlaybackManager,
    private val getMusicUseCase: GetMusicUseCase,
    private val getAlbumsNameFromSearchStringUseCase: GetAlbumsNameFromSearchStringUseCase,
    private val getArtistsNameFromSearchStringUseCase: GetArtistsNameFromSearchStringUseCase,
    private val upsertImageCoverUseCase: UpsertImageCoverUseCase,
    private val updateMusicUseCase: UpdateMusicUseCase,
) : ScreenModel {
    private val musicId: MutableStateFlow<UUID?> = MutableStateFlow(null)
    private val newCover: MutableStateFlow<ImageBitmap?> = MutableStateFlow(null)
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
                    initialCoverId = initialMusic.coverId,
                    newCover = newCover
                )
            )
        }
    }.stateIn(
        scope = screenModelScope.plus(Dispatchers.IO),
        started = SharingStarted.Eagerly,
        initialValue = ModifyMusicState.Loading,
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val formState: StateFlow<ModifyMusicFormState> = initialMusic.mapLatest { music ->
        if (music == null) {
            ModifyMusicFormState.NoData
        } else {
            ModifyMusicFormState.Data(
                initialMusic = music,
                updateFoundAlbums = { getAlbumsNameFromSearchStringUseCase(it) },
                updateFoundArtists = { getArtistsNameFromSearchStringUseCase(it) },
            )
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
            newCover.value = imageFile.readBytes().toImageBitmap()
        }
    }

    /**
     * Update selected music information.
     */
    fun updateMusic() {
        CoroutineScope(Dispatchers.IO).launch {

            val state = (state.value as? ModifyMusicState.Data) ?: return@launch
            val form = (formState.value as? ModifyMusicFormState.Data) ?: return@launch

            if (!form.isFormValid()) return@launch

            val coverId = if (state.editableElement.newCover != null) {
                val imageCover = ImageCover(
                    cover = state.editableElement.newCover
                )
                upsertImageCoverUseCase(imageCover = imageCover)
                imageCover.coverId
            } else {
                state.initialMusic.coverId
            }

            val newMusicInformation = state.initialMusic.copy(
                coverId = coverId,
                name = form.getMusicName().trim(),
                album = form.getAlbumName().trim(),
                artist = form.getArtistName().trim(),
            )

            updateMusicUseCase(
                legacyMusic = state.initialMusic,
                newMusicInformation = newMusicInformation
            )

            playbackManager.updateMusic(newMusicInformation)
            playbackManager.currentMusic?.let {
                if (it.musicId.compareTo(newMusicInformation.musicId) == 0
                    && state.editableElement.newCover != null) {
                    playbackManager.updateCover(
                        cover = state.editableElement.newCover
                    )
                }
            }

            _navigationState.value = ModifyMusicNavigationState.Back
        }
    }
}