package com.github.soulsearching.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.soulsearching.database.dao.ImageCoverDao
import com.github.soulsearching.database.model.ImageCover
import com.github.soulsearching.states.ImageCoverState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AllImageCoversViewModel @Inject constructor(
    private val imageCoverDao: ImageCoverDao
) : ViewModel() {
    private val _covers = imageCoverDao.getAllCovers().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ArrayList())
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
}