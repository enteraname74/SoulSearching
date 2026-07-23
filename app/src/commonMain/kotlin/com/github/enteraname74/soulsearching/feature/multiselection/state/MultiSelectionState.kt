package com.github.enteraname74.soulsearching.feature.multiselection.state

import kotlin.uuid.Uuid

data class MultiSelectionState(
    val selectedIds: List<String> = emptyList(),
    val playlistId: Uuid? = null,
) {
    val totalSelected: Int = selectedIds.size
}
