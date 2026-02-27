package com.github.enteraname74.soulsearching.feature.multiselection

import com.github.enteraname74.soulsearching.feature.multiselection.state.MultiSelectionNavigationState
import com.github.enteraname74.soulsearching.feature.multiselection.state.MultiSelectionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class MultiSelectionManager {
    private var _selectionMode: SelectionMode = SelectionMode.Music
    val selectionMode: SelectionMode
        get() = _selectionMode

    private val _state: MutableStateFlow<MultiSelectionState> = MutableStateFlow(
        MultiSelectionState()
    )
    val state: StateFlow<MultiSelectionState> = _state.asStateFlow()

    private val _navigationState: MutableStateFlow<MultiSelectionNavigationState> =
        MutableStateFlow(
            MultiSelectionNavigationState.Idle
        )
    val navigationState: StateFlow<MultiSelectionNavigationState> = _navigationState.asStateFlow()

    /**
     * Add or remove an element to the selection.
     */
    fun toggleElementInSelection(
        id: UUID,
        mode: SelectionMode,
        playlistId: UUID? = null,
    ) {
        if (_selectionMode != mode) {
            clearMultiSelection()
            _selectionMode = mode
        }

        if (!_state.value.selectedIds.contains(id)) {
            _state.value = MultiSelectionState(
                selectedIds = buildList {
                    addAll(_state.value.selectedIds)
                    add(id)
                },
                playlistId = playlistId,
            )
        } else {
            val filteredList = _state.value.selectedIds.filter { it != id }
            _state.value = MultiSelectionState(
                selectedIds = filteredList,
                playlistId = playlistId,
            )
        }
    }

    fun clearMultiSelection() {
        _state.value = MultiSelectionState()
    }

    fun consumeNavigation() {
        _navigationState.value = MultiSelectionNavigationState.Idle
    }

    fun showBottomSheet() {
        when (_selectionMode) {
            SelectionMode.Music -> _navigationState.value =
                MultiSelectionNavigationState.ToMusicBottomSheet(
                    musicIds = _state.value.selectedIds,
                    playlistId = _state.value.playlistId,
                )

            SelectionMode.Playlist -> TODO()
            SelectionMode.Album -> TODO()
            SelectionMode.Artist -> TODO()
        }
    }
}

enum class SelectionMode {
    Music,
    Playlist,
    Album,
    Artist,
}