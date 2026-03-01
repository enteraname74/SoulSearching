package com.github.enteraname74.soulsearching.feature.multiselection.state

import java.util.UUID

data class MultiSelectionState(
    val selectedIds: List<UUID> = emptyList(),
    val playlistId: UUID? = null,
)