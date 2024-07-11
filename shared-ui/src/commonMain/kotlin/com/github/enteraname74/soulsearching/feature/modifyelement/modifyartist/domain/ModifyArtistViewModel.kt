package com.github.enteraname74.soulsearching.feature.modifyelement.modifyartist.domain

import androidx.compose.ui.graphics.ImageBitmap
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.model.ImageCover
import com.github.enteraname74.domain.usecase.artist.GetArtistWithMusicsUseCase
import com.github.enteraname74.domain.usecase.artist.GetArtistsNameFromSearchStringUseCase
import com.github.enteraname74.domain.usecase.artist.UpdateArtistUseCase
import com.github.enteraname74.domain.usecase.imagecover.GetCoverOfElementUseCase
import com.github.enteraname74.domain.usecase.imagecover.UpsertImageCoverUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class ModifyArtistViewModel(
    private val getArtistsNameFromSearchStringUseCase: GetArtistsNameFromSearchStringUseCase,
    private val getArtistWithMusicsUseCase: GetArtistWithMusicsUseCase,
    private val getCoverOfElementUseCase: GetCoverOfElementUseCase,
    private val upsertImageCoverUseCase: UpsertImageCoverUseCase,
    private val updateArtistUseCase: UpdateArtistUseCase,
) : ScreenModel {
    private val _state = MutableStateFlow(ModifyArtistState())
    val state = _state.stateIn(
        screenModelScope,
        SharingStarted.WhileSubscribed(5000),
        ModifyArtistState()
    )

    /**
     * Utility method to trim values of an Artist when saving modification to it.
     */
    private fun ArtistWithMusics.trim() = this.copy(
        artist = this.artist.copy(
            artistName = this.artist.artistName.trim()
        )
    )

    /**
     * Manage artist events.
     */
    fun onEvent(event: ModifyArtistEvent) {
        when (event) {
            ModifyArtistEvent.UpdateArtist -> update()
            is ModifyArtistEvent.ArtistFromId -> setSelectedArtist(artistId = event.artistId)
            is ModifyArtistEvent.SetName -> setArtist(newArtistName = event.name)
            is ModifyArtistEvent.SetCover -> setArtistCover(cover = event.cover)
            is ModifyArtistEvent.SetMatchingArtists -> setMatchingArtists(search = event.search)
        }
    }

    /**
     * Set a list of matching artists names to use when modifying the artist field of an album.
     */
    private fun setMatchingArtists(search: String) {
        CoroutineScope(Dispatchers.IO).launch {
            _state.update {
                it.copy(
                    matchingArtistsNames = getArtistsNameFromSearchStringUseCase(
                        searchString = search,
                    )
                )
            }
        }
    }

    /**
     * Set the new cover name to show to the user.
     */
    private fun setArtistCover(cover: ImageBitmap) {
        _state.update {
            it.copy(
                cover = cover,
                hasCoverBeenChanged = true
            )
        }
    }

    /**
     * Set the new artist name to show to the user.
     */
    private fun setArtist(newArtistName: String) {
        _state.update {
            it.copy(
                artistWithMusics = it.artistWithMusics.copy(
                    artist = it.artistWithMusics.artist.copy(
                        artistName = newArtistName
                    )
                )
            )
        }
    }

    /**
     * Set the selected artist for the modify screen information.
     */
    private fun setSelectedArtist(artistId: UUID) {
        CoroutineScope(Dispatchers.IO).launch {
            val artistWithMusics = getArtistWithMusicsUseCase(artistId).first() ?: return@launch
            val cover: ImageBitmap? = artistWithMusics.artist.coverId?.let { coverId ->
                getCoverOfElementUseCase(
                    coverId = coverId,
                )?.cover
            }
            _state.update {
                it.copy(
                    artistWithMusics = artistWithMusics,
                    cover = cover,
                    hasCoverBeenChanged = false
                )
            }
        }
    }

    /**
     * Update the artist information.
     */
    private fun update() {
        CoroutineScope(Dispatchers.IO).launch {
            val coverId =
                if (_state.value.hasCoverBeenChanged && _state.value.cover != null) {
                    val imageCover = ImageCover(cover = _state.value.cover!!)
                    upsertImageCoverUseCase(imageCover = imageCover)
                    imageCover.coverId
                } else {
                    _state.value.artistWithMusics.artist.coverId
                }

            val newArtistInformation = _state.value.artistWithMusics.trim().copy(
                artist = _state.value.artistWithMusics.artist.copy(
                    coverId = coverId
                )
            )

            updateArtistUseCase(newArtistWithMusicsInformation = newArtistInformation)
        }
    }
}