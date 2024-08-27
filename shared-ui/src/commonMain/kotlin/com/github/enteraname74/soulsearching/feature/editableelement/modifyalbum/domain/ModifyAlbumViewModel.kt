package com.github.enteraname74.soulsearching.feature.editableelement.modifyalbum.domain

import androidx.compose.ui.graphics.ImageBitmap
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.ext.toImageBitmap
import com.github.enteraname74.domain.model.AlbumWithArtist
import com.github.enteraname74.domain.model.AlbumWithMusics
import com.github.enteraname74.domain.model.ImageCover
import com.github.enteraname74.domain.usecase.album.GetAlbumWithMusicsUseCase
import com.github.enteraname74.domain.usecase.album.GetAlbumsNameFromSearchStringUseCase
import com.github.enteraname74.domain.usecase.album.UpdateAlbumUseCase
import com.github.enteraname74.domain.usecase.artist.GetArtistsNameFromSearchStringUseCase
import com.github.enteraname74.domain.usecase.imagecover.UpsertImageCoverUseCase
import com.github.enteraname74.soulsearching.feature.editableelement.domain.EditableElement
import com.github.enteraname74.soulsearching.feature.editableelement.modifyalbum.domain.state.ModifyAlbumFormState
import com.github.enteraname74.soulsearching.feature.editableelement.modifyalbum.domain.state.ModifyAlbumNavigationState
import com.github.enteraname74.soulsearching.feature.editableelement.modifyalbum.domain.state.ModifyAlbumState
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
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
    private val playbackManager: PlaybackManager
) : ScreenModel {
    private val albumId: MutableStateFlow<UUID?> = MutableStateFlow(null)
    private val newCover: MutableStateFlow<ImageBitmap?> = MutableStateFlow(null)
    private val _navigationState: MutableStateFlow<ModifyAlbumNavigationState> = MutableStateFlow(
        ModifyAlbumNavigationState.Idle,
    )
    val navigationState: StateFlow<ModifyAlbumNavigationState> = _navigationState.asStateFlow()

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
                    initialCoverId = initialAlbum.album.coverId,
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

    /**
     * Set the selected album for the modify screen information.
     */
    fun init(albumId: UUID) {
        this.albumId.value = albumId
    }

    /**
     * Set the new cover name to show to the user.
     */
    fun setNewCover(imageFile: PlatformFile) {
        screenModelScope.launch {
            newCover.value = imageFile.readBytes().toImageBitmap()
        }
    }

    fun consumeNavigation() {
        _navigationState.value = ModifyAlbumNavigationState.Back
    }

    /**
     * Update the information of the selected album.
     */
    fun updateAlbum() {
        CoroutineScope(Dispatchers.IO).launch {


            val state = (state.value as? ModifyAlbumState.Data) ?: return@launch
            val form = (formState.value as? ModifyAlbumFormState.Data) ?: return@launch

            if (!form.isFormValid()) return@launch

            // If the image has changed, we need to save it and retrieve its id.
            val coverId = if (state.editableElement.newCover != null) {
                val imageCover = ImageCover(
                    cover = state.editableElement.newCover,
                )
                upsertImageCoverUseCase(imageCover = imageCover)
                imageCover.coverId
            } else {
                state.initialAlbum.album.coverId
            }

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
                    coverId = coverId
                )
            )

            // We update the information of the album.
            updateAlbumUseCase(newAlbumWithArtistInformation = newAlbumWithArtistInformation)

            // We retrieve the updated album
            val newAlbumWithMusics: AlbumWithMusics = getAlbumWithMusicsUseCase(
                albumId = newAlbumWithArtistInformation.album.albumId
            ).first() ?: return@launch

            // We need to update the album's songs that are in the played list.
            for (music in newAlbumWithMusics.musics) playbackManager.updateMusic(music)

            _navigationState.value = ModifyAlbumNavigationState.Back
        }
    }
}