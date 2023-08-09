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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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

    fun getImageCover(coverId: UUID?): Bitmap? {
        return state.value.covers.find { it.coverId == coverId }?.cover
    }


    private var mutex = Mutex()
    suspend fun getCover(coverId: UUID?): Bitmap? {

        mutex.withLock {
            return if (coverId != null) {
                val res = imageCoverDao.getCoverOfElement(coverId = coverId)?.cover
                res
            } else {
                null
            }
        }

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

    suspend fun verifyIfImageIsUsed(cover: ImageCover) {
        if (
            musicDao.getNumberOfMusicsWithCoverId(cover.coverId) == 0
            && albumDao.getNumberOfArtistsWithCoverId(cover.coverId) == 0
            && playlistDao.getNumberOfPlaylistsWithCoverId(cover.coverId) == 0
            && artistDao.getNumberOfArtistsWithCoverId(cover.coverId) == 0
        ) {
            Log.d("VERIFY", "IMAGE NOT USED !")
            imageCoverDao.deleteFromCoverId(cover.coverId)
        }
    }
}