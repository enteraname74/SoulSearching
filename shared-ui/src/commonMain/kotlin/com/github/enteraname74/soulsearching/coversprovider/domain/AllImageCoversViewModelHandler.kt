package com.github.soulsearching.coversprovider.domain

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.ImageCover
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.soulsearching.domain.viewmodel.handler.ViewModelHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.util.UUID

/**
 * Handler for managing the AllImageCoversViewModel.
 */
class AllImageCoversViewModelHandler(
    coroutineScope: CoroutineScope,
    private val imageCoverRepository: ImageCoverRepository,
    private val musicRepository: MusicRepository,
    private val albumRepository: AlbumRepository,
    private val artistRepository: ArtistRepository,
    private val playlistRepository: PlaylistRepository
): ViewModelHandler {
    private val _covers = imageCoverRepository.getAllCoversAsFlow()
        .stateIn(coroutineScope, SharingStarted.WhileSubscribed(), ArrayList())
    private val _state = MutableStateFlow(ImageCoverState())
    val state = combine(
        _state,
        _covers
    ) { state, covers ->
        return@combine state.copy(
            covers = covers as ArrayList<ImageCover>
        )

    }.stateIn(
        coroutineScope,
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
    suspend fun deleteImageIfNotUsed(cover: ImageCover) {
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