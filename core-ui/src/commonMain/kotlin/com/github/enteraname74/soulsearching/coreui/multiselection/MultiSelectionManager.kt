package com.github.enteraname74.soulsearching.coreui.multiselection

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

class MultiSelectionManager {
    var selectionMode: SelectionMode = SelectionMode.Music
        private set

    private val _state: MutableStateFlow<MultiSelectionState> = MutableStateFlow(
        MultiSelectionState(
            selectedIds = emptyList(),
        )
    )
    val state: StateFlow<MultiSelectionState> = _state.asStateFlow()

    /**
     * Add or remove an element to the selection.
     */
    fun toggle(id: UUID, mode: SelectionMode) {

        if (selectionMode != mode) {
            clear()
            selectionMode  = mode
        }

        if (!_state.value.selectedIds.contains(id)) {
            _state.value = MultiSelectionState(
                selectedIds = buildList {
                    addAll(_state.value.selectedIds)
                    add(id)
                },
            )
        } else {
            val filteredList = _state.value.selectedIds.filter { it != id }
            _state.value = MultiSelectionState(
                selectedIds = filteredList,
            )
        }
    }

    fun clear() {
        _state.value = MultiSelectionState(
            selectedIds = emptyList(),
        )
    }
}

enum class SelectionMode {
    Music,
    Playlist,
    Album,
    Artist,
}