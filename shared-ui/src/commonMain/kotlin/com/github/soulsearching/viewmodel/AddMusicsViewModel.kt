package com.github.soulsearching.viewmodel

import com.github.soulsearching.classes.SelectableMusicItem
import com.github.soulsearching.events.AddMusicsEvent
import com.github.soulsearching.states.AddMusicsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Interface representing the AddMusicsViewModel and its functions.
 */
interface AddMusicsViewModel {

    /**
     * Internal state of the AddMusicsViewModel.
     */
    private var _state: MutableStateFlow<AddMusicsState>
        get() = MutableStateFlow(AddMusicsState())
        set(value) = TODO()

    val state: StateFlow<AddMusicsState>
        get() = _state.asStateFlow()

    /**
     * Manage add music events.
     */
    fun onAddMusicEvent(event: AddMusicsEvent)

    /**
     * Fetch and add new musics.
     */
    fun fetchAndAddNewMusics(updateProgressBar: (Float) -> Unit)

    /**
     * Fetch new musics.
     */
    fun fetchNewMusics(
        updateProgress: (Float) -> Unit,
        alreadyPresentMusicsPaths: List<String>,
        hiddenFoldersPaths: List<String>
    ) : ArrayList<SelectableMusicItem>
}