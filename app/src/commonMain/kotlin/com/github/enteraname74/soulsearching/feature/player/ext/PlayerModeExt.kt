package com.github.enteraname74.soulsearching.feature.player.ext

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.github.enteraname74.domain.model.player.PlayerMode

fun PlayerMode.imageVector(): ImageVector = when(this) {
    PlayerMode.Normal -> Icons.Rounded.Repeat
    PlayerMode.Shuffle -> Icons.Rounded.Shuffle
    PlayerMode.Loop -> Icons.Rounded.RepeatOne
}