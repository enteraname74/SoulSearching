package com.github.soulsearching.viewModels

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.soulsearching.database.dao.*
import com.github.soulsearching.database.model.ImageCover
import com.github.soulsearching.states.ImageCoverState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*
import javax.inject.Inject

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
        state.copy(
            covers = covers as ArrayList<ImageCover>
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ImageCoverState()
    )

    fun getImageCover(coverId : UUID?) : Bitmap? {
        val cover = state.value.covers.find { it.coverId == coverId }?.cover
        Log.d("FOUND ?", cover.toString())
        return cover
    }

//    fun updateCovers(coverId : UUID) {
//        CoroutineScope(Dispatchers.IO).launch {
//            val copy = imageCovers.map { it.copy() } as ArrayList<ImageCover>
//            val cover = imageCoverDao.getCoverOfElement(coverId)
//
//            cover?.let {
//                copy.add(it)
//                imageCovers = copy
//            }
//        }
//    }

    suspend fun verifyIfAllImagesAreUsed(cover : ImageCover) {
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