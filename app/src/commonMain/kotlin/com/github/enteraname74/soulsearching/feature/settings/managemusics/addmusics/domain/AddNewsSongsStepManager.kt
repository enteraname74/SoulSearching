package com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.domain

import com.github.enteraname74.soulsearching.features.musicmanager.fetching.SelectableMusicItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class AddNewsSongsStepManager {
    private val _state: MutableStateFlow<AddNewsSongsStepState> = MutableStateFlow(AddNewsSongsStepState.Fetching)
    val state: Flow<AddNewsSongsStepState> = _state

    fun toStep(step: AddNewsSongsStepState) {
        _state.value = step
    }
}

sealed interface AddNewsSongsStepState {
    data object Fetching: AddNewsSongsStepState
    data object SongsSaved:
       AddNewsSongsStepState

    data class Data(
        val fetchedMusics: List<SelectableMusicItem>,
    ): AddNewsSongsStepState
}