package com.github.soulsearching.viewmodel

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.model.ImageCover
import com.github.enteraname74.repository.AlbumRepository
import com.github.enteraname74.repository.ArtistRepository
import com.github.enteraname74.repository.ImageCoverRepository
import com.github.enteraname74.repository.MusicRepository
import com.github.enteraname74.repository.PlaylistRepository
import com.github.soulsearching.states.ImageCoverState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel for managing all image covers.
 */
@HiltViewModel
class AllImageCoversViewModel @Inject constructor(
    private val imageCoverRepository: ImageCoverRepository,
    private val musicRepository: MusicRepository,
    private val albumRepository: AlbumRepository,
    private val artistRepository: ArtistRepository,
    private val playlistRepository: PlaylistRepository
) : ViewModel() {
    private val _covers = imageCoverRepository.getAllCoversAsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ArrayList())
    private val _state = MutableStateFlow(ImageCoverState())
    val state = combine(
        _state,
        _covers
    ) { state, covers ->
        return@combine state.copy(
            covers = covers as ArrayList<ImageCover>
        )

    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ImageCoverState()
    )

    /**
     * Tries to retrieves an ImageBitmap representation of the id of an image cover.
     */
    fun getImageCover(coverId: UUID?): ImageBitmap? {
        return state.value.covers.find { it.coverId == coverId }?.cover
    }

    /**
     * Delete an image if it's not used by a song, an album, an artist or a playlist.
     */
    suspend fun deleteImageIsNotUsed(cover: ImageCover) {
        if (
            musicRepository.getNumberOfMusicsWithCoverId(cover.coverId) == 0
            && albumRepository.getNumberOfAlbumsWithCoverId(cover.coverId) == 0
            && playlistRepository.getNumberOfPlaylistsWithCoverId(cover.coverId) == 0
            && artistRepository.getNumberOfArtistsWithCoverId(cover.coverId) == 0
        ) {
            imageCoverRepository.deleteFromCoverId(cover.coverId)
        }
    }
}