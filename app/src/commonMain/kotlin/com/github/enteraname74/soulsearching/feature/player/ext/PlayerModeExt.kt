package com.github.enteraname74.soulsearching.feature.player.ext

import com.github.enteraname74.domain.model.player.PlayerMode
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_repeat
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_repeat_one
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_shuffle
import org.jetbrains.compose.resources.DrawableResource

fun PlayerMode.icon(): DrawableResource = when(this) {
    PlayerMode.Normal -> CoreRes.drawable.ic_repeat
    PlayerMode.Shuffle -> CoreRes.drawable.ic_shuffle
    PlayerMode.Loop -> CoreRes.drawable.ic_repeat_one
}