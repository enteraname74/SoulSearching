package com.github.enteraname74.soulsearching.domain.model

import com.github.enteraname74.domain.model.player.PlayedListToContinue
import kotlin.uuid.Uuid

data class CachedPlayedListUiSpec(
    val playedListId: Uuid,
    val currentMusic: PlayedListToContinue.CurrentMusic,
    val onContinue: () -> Unit,
    val onDismiss: () -> Unit,
)
