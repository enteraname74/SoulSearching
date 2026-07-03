package com.github.enteraname74.soulsearching.feature.multiselection.state

import kotlin.uuid.Uuid

data class MultiSelectionState(
    val selectedIds: List<Uuid> = emptyList(),
    val playlistId: Uuid? = null,
)
