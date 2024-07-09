package com.github.enteraname74.soulsearching.feature.modifyelement.modifyartist.domain

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.ImageCoverRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Handler for managing the ModifyArtistViewModel.
 */
class ModifyArtistViewModelHandler(
    coroutineScope: CoroutineScope,
    private val artistRepository: ArtistRepository,
    private val imageCoverRepository: ImageCoverRepository
) : ViewModelHandler {
    private val _state = MutableStateFlow(ModifyArtistState())
    val state = _state.stateIn(
        coroutineScope,
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
                    matchingArtistsNames = artistRepository.getArtistsNameFromSearch(search = search)
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
            val artistWithMusics = artistRepository.getArtistWithMusics(artistId) ?: return@launch
            val cover = if (artistWithMusics.artist.coverId != null) {
                imageCoverRepository.getCoverOfElement(artistWithMusics.artist.coverId!!)?.cover
            } else {
                null
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
                    imageCoverRepository.save(cover = _state.value.cover!!)
                } else {
                    _state.value.artistWithMusics.artist.coverId
                }

            val newArtistInformation = _state.value.artistWithMusics.trim().copy(
                artist = _state.value.artistWithMusics.artist.copy(
                    coverId = coverId
                )
            )

            artistRepository.update(newArtistWithMusicsInformation = newArtistInformation)
        }
    }
}