package com.github.soulsearching.viewModels

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.soulsearching.database.dao.*
import com.github.soulsearching.database.model.ImageCover
import com.github.soulsearching.states.ImageCoverState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.util.*
import javax.inject.Inject

/**
 * ViewModel for managing all image covers.
 */
@HiltViewModel
class AllImageCoversViewModel @Inject constructor(
    private val imageCoverDao: ImageCoverDao,
    private val musicDao: MusicDao,
    private val albumDao: AlbumDao,
    private val artistDao: ArtistDao,
    private val playlistDao: PlaylistDao
) : ViewModel() {
    private val _covers = imageCoverDao.getAllCovers()
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
     * Tries to retrieves a Bitmap representation of the id of an image cover.
     */
    fun getImageCover(coverId: UUID?): Bitmap? {
        return state.value.covers.find { it.coverId == coverId }?.cover
    }

    /**
     * Delete an image if it's not used by a song, an album, an artist or a playlist.
     */
    suspend fun deleteImageIsNotUsed(cover: ImageCover) {
        if (
            musicDao.getNumberOfMusicsWithCoverId(cover.coverId) == 0
            && albumDao.getNumberOfArtistsWithCoverId(cover.coverId) == 0
            && playlistDao.getNumberOfPlaylistsWithCoverId(cover.coverId) == 0
            && artistDao.getNumberOfArtistsWithCoverId(cover.coverId) == 0
        ) {
            imageCoverDao.deleteFromCoverId(cover.coverId)
        }
    }
}