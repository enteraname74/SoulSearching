package com.github.enteraname74.soulsearching.feature.multiselection

import com.github.enteraname74.soulsearching.feature.multiselection.state.MultiSelectionNavigationState
import com.github.enteraname74.soulsearching.feature.multiselection.state.MultiSelectionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.uuid.Uuid

class MultiSelectionManager {
    private var _selectionMode: SelectionMode = SelectionMode.Music

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
        id: String,
        mode: SelectionMode,
        playlistId: Uuid? = null,
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

    fun isActive(): Boolean = _state.value.totalSelected > 0

    fun clearMultiSelection() {
        _state.value = MultiSelectionState()
    }

    fun consumeNavigation() {
        _navigationState.value = MultiSelectionNavigationState.Idle
    }

    fun showBottomSheet() {
        _navigationState.value = when (_selectionMode) {
            SelectionMode.Music -> MultiSelectionNavigationState.ToMusicBottomSheet(
                musicIds = _state.value.selectedUuids(),
                playlistId = _state.value.playlistId,
            )
            SelectionMode.Playlist -> MultiSelectionNavigationState.ToPlaylistBottomSheet(
                playlistIds = _state.value.selectedUuids(),
            )
            SelectionMode.Album -> MultiSelectionNavigationState.ToAlbumBottomSheet(
                albumIds = _state.value.selectedUuids(),
            )
            SelectionMode.Artist -> MultiSelectionNavigationState.ToArtistBottomSheet(
                artistIds = _state.value.selectedUuids(),
            )
            SelectionMode.Folder -> MultiSelectionNavigationState.ToFolderBottomSheet(
                folderPaths = _state.value.selectedIds,
            )
            SelectionMode.Month -> MultiSelectionNavigationState.ToMonthBottomSheet(
                months = _state.value.selectedIds,
            )
        }
    }

    private fun MultiSelectionState.selectedUuids(): List<Uuid> =
        selectedIds.mapNotNull { Uuid.parseOrNull(it) }
}

enum class SelectionMode {
    Music,
    Playlist,
    Album,
    Artist,
    Folder,
    Month,
}
