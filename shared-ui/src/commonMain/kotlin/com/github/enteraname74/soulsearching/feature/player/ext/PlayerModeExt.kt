package com.github.enteraname74.soulsearching.feature.player.ext

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.ui.graphics.vector.ImageVector
import com.github.enteraname74.domain.model.PlayerMode

fun PlayerMode.imageVector(): ImageVector = when(this) {
    PlayerMode.Normal -> Icons.Rounded.Sync
    PlayerMode.Shuffle -> Icons.Rounded.Shuffle
    PlayerMode.Loop -> Icons.Rounded.Replay
}