package com.github.enteraname74.soulsearching.coreui.multiselection

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

interface MultiSelectionManager {
    /**
     * Add or remove an element to the selection.
     */
    fun toggleElementInSelection(id: UUID, mode: SelectionMode)

    fun clearMultiSelection()

    val selectionMode: SelectionMode
}

class MultiSelectionManagerImpl: MultiSelectionManager {
    private var _selectionMode: SelectionMode = SelectionMode.Music
    override val selectionMode: SelectionMode
        get() = _selectionMode

    private val _state: MutableStateFlow<MultiSelectionState> = MutableStateFlow(
        MultiSelectionState(
            selectedIds = emptyList(),
        )
    )
    val state: StateFlow<MultiSelectionState> = _state.asStateFlow()

    /**
     * Add or remove an element to the selection.
     */
    override fun toggleElementInSelection(id: UUID, mode: SelectionMode) {

        if (_selectionMode != mode) {
            clearMultiSelection()
            _selectionMode  = mode
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

    override fun clearMultiSelection() {
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